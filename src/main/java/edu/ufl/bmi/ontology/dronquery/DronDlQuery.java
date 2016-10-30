package edu.ufl.bmi.ontology.dronquery;

import java.io.File;
import java.io.PrintStream;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLAnnotation;

import static org.semanticweb.owlapi.search.EntitySearcher.getAnnotationObjects;

public class DronDlQuery {

    Options opt;
    CommandLineParser clp;
    CommandLine cl;
    String[] arg;
    String dlQueryTxt;
    DronDlQueryTranslator tx;

    NodeSet<OWLClass> results;
    IRI labelIRI = IRI.create("http://www.w3.org/2000/01/rdf-schema#label");
    IRI rxcuiIRI = IRI.create("http://purl.obolibrary.org/obo/DRON_00010000");
    Set<OWLOntology> importClosure;

    public DronDlQuery() {
	opt = new Options();
	Option purl = Option.builder("p")
	    .argName("URL")
	    .hasArg()
	    .desc("the permanent URL to the Drug Ontology")
	    .longOpt("purl")
	    .required()
	    .build();

	Option out = Option.builder("f")
	    .argName("output file")
	    .hasArg()
	    .desc("the output will go to this file")
	    .optionalArg(true)
	    .longOpt("output")
	    .build();

	Option query = Option.builder("q")
	    .argName("DL query string")
	    .hasArg()
	    .required()
	    .longOpt("query")
	    .build();

	Option txfile = Option.builder("t")
	    .argName("label to IRI translation file")
	    .hasArg()
	    .optionalArg(true)
	    .longOpt("txfile")
	    .build();

	Option reasoner = Option.builder("r")
	    .argName("reasoner for query")
	    .hasArg()
	    .optionalArg(true)
	    .longOpt("reasoner")
	    .build();

	opt.addOption(purl);
	opt.addOption(out);
	opt.addOption(query);
	opt.addOption(txfile);
	opt.addOption(reasoner);
    }

    public void runQuery(String[] arg) {
	try {
	    getCommandLine(arg);
            setupQuery();
	    runQuery();
	} catch (ParseException e) {
	    System.err.println("Error parsing command line.");
	    e.printStackTrace();
	}
    }

    protected void getCommandLine(String[] arg) throws ParseException {
	this.arg = arg;
	clp = new DefaultParser();
	cl = clp.parse(opt, this.arg);
    }

    protected void setupQuery() {
	String queryTxt = cl.getOptionValue("query");
	System.out.println(queryTxt);
	if (cl.hasOption("txfile")) {
	    tx = new DronDlQueryTranslator(cl.getOptionValue("txfile"));
	} else {
	    tx = new DronDlQueryTranslator("./src/main/resources/dron-query-helper.txt");
	}
	dlQueryTxt = tx.translateQuery(queryTxt);
	System.out.println(dlQueryTxt);
    }


    protected void runQuery() {
	try {
	    OWLOntology o = loadOntology();
	    OWLReasonerFactory rf = createReasonerFactory();
	    DlQueryExecutor dqe = new DlQueryExecutor(rf, o);
	    
	    results = dqe.runQuery(dlQueryTxt);
	    processResults();

	} catch (OWLOntologyCreationException ooce) {
	    System.err.println("Could not create the ontology");
	    ooce.printStackTrace();
	} catch (InstantiationException ie) {
	    System.err.println("error creating reasoner factory, couldn't instantiate class");
	    ie.printStackTrace();
	} catch (ClassNotFoundException cnfe) {
	    System.err.println("Could not find class associated with reasoner factory for specified reasoner");
	    cnfe.printStackTrace();
	} catch (IllegalAccessException iae) {
	    System.err.println("Default constructor for reasoner factory class is not public");
	    iae.printStackTrace();
	}
    }

    protected OWLOntology loadOntology() throws OWLOntologyCreationException {
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	IRI ontologyIRI = IRI.create(cl.getOptionValue("purl"));
	OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyIRI);
	importClosure = ontology.getImportsClosure();
	return ontology;
    }

    protected OWLReasonerFactory createReasonerFactory() throws InstantiationException, 
		ClassNotFoundException, IllegalAccessException {
	String reasonerFactoryClassName;
	if (cl.hasOption("reasoner")) {
	    String optValue = cl.getOptionValue("reasoner");
	    if (optValue.equals("elk")) {
		reasonerFactoryClassName = "org.semanticweb.elk.owlapi.ElkReasonerFactory";
	    } else if (optValue.equals("hermit")) {
		reasonerFactoryClassName = "";
	    } else {
		System.err.println("Unrecognized reasoner option: " + optValue);
		System.err.println("using elk reasoner instead");
		reasonerFactoryClassName = "org.semanticweb.elk.owlapi.ElkReasonerFactory";
	    }
	} else {
		reasonerFactoryClassName = "org.semanticweb.elk.owlapi.ElkReasonerFactory";
	}

	OWLReasonerFactory rf = (OWLReasonerFactory)Class.forName(
			  reasonerFactoryClassName).newInstance();

	return rf;
    }

    protected void processResults() {
	PrintStream out = setupOutputStream();

	Set<OWLClass> clsResults = results.getFlattened();
        for (OWLClass c : clsResults) {
	    AnnotationExtractor leLabel = new AnnotationExtractor(labelIRI);
	    AnnotationExtractor leRxcui = new AnnotationExtractor(rxcuiIRI);
	    for (OWLOntology o : importClosure) {
		for (OWLAnnotation anno : getAnnotationObjects(c, o)) {
		    anno.accept(leLabel);
		    anno.accept(leRxcui);
		}
	    }
	    out.println(leLabel.getResult() + "\t" + c.getIRI().toString() + "\t" + leRxcui.getResult());
        }
        
        out.close();
    }

    protected PrintStream setupOutputStream() {
	PrintStream s = null;

	if (cl.hasOption("file")) {
	    try {
		File f = new File(cl.getOptionValue("file"));
		s = new PrintStream(f);
	    } catch (IOException ioe) {
		System.err.println("Could not create output file " + cl.getOptionValue("file"));
		ioe.printStackTrace();
		System.out.println("Using standard output instead.");
	    }
	} 

	if (s == null) s = System.out;

	return s;
    }

    public static void main(String[] args) {
	DronDlQuery ddlq = new DronDlQuery();
	ddlq.runQuery(args);
    }
}