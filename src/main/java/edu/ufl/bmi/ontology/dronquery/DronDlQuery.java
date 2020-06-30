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
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLAnnotation;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.HermiT.Reasoner;

import static org.semanticweb.owlapi.search.EntitySearcher.getAnnotationObjects;

public class DronDlQuery {

	//static final String ELK_REASONER_FACTORY_CLASS_NAME = "org.semanticweb.elk.reasoner.ReasonerFactory";  //0.4.3
	static final String ELK_REASONER_FACTORY_CLASS_NAME = "org.semanticweb.elk.owlapi.ElkReasonerFactory";  //0.4.0

    Options opt;
    CommandLineParser clp;
    CommandLine cl;
    String[] arg;
    String[] dlQueryTxt;
    PrintStream[] outStream;
    DronDlQueryTranslator tx;

    DronDlQueryResult[] results;
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

	Option ff = Option.builder("f")
	    .argName("from file")
	    .hasArg(false)
	    .desc("whether the PURL points to the file")
	    .longOpt("from_file")
	    .required()
	    .build();

	Option out = Option.builder("f")
	    .argName("output file")
	    .hasArgs()
	    .desc("the output will go to this file")
	    .optionalArg(true)
	    .longOpt("output")
	    .valueSeparator(',')
	    .build();

	Option query = Option.builder("q")
	    .argName("DL query string")
	    .hasArgs()
	    .required()
	    .longOpt("query")
	    .valueSeparator(',')
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

	Option includeIndividuals = Option.builder("i")
		.argName("flag to include individuals or not")
		.hasArg(false)
		.required(false)
		.longOpt("individuals")
		.build();

	opt.addOption(purl);
	opt.addOption(ff);
	opt.addOption(out);
	opt.addOption(query);
	opt.addOption(txfile);
	opt.addOption(reasoner);
	opt.addOption(includeIndividuals);
    }

    public void runQuery(String[] arg) {
	try {
	    getCommandLine(arg);
        setupQuery();
        setupOutputStream();
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
	String[] queryTxt = cl.getOptionValues("query");
	for (String query : queryTxt)
		System.out.println(query);
	if (cl.hasOption("txfile")) {
	    tx = new DronDlQueryTranslator(cl.getOptionValue("txfile"));
	} else {
	    tx = new DronDlQueryTranslator("./src/main/resources/dron-query-helper.txt");
	}
	dlQueryTxt = new String[queryTxt.length];
	results = new DronDlQueryResult[queryTxt.length];
	for (int i=0; i<queryTxt.length; i++) {
		dlQueryTxt[i] = tx.translateQuery(queryTxt[i]);
	}
	for (String dlQuery : dlQueryTxt)
		System.out.println(dlQuery);
    }


    protected void runQuery() {
	try {
	    OWLOntology o = loadOntology();
	    OWLReasonerFactory rf = createReasonerFactory();
	    DlQueryExecutor dqe = new DlQueryExecutor(rf, o);
	    boolean includeInds = cl.hasOption("individuals");
	    
	    for (int i=0; i<dlQueryTxt.length; i++) {
	    	results[i] = dqe.runQuery(dlQueryTxt[i], includeInds);
	    	processResults(results[i], outStream[i]);
	    }

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

	IRI ontologyIRI;
	if (cl.hasOption("from_file")) {
		File f = new File(cl.getOptionValue("purl"));
		ontologyIRI = IRI.create(f);
	} else {
		ontologyIRI = IRI.create(cl.getOptionValue("purl"));
	}
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
		//reasonerFactoryClassName = "org.semanticweb.elk.owlapi.ElkReasonerFactory";
	    reasonerFactoryClassName = ELK_REASONER_FACTORY_CLASS_NAME;
	    } else if (optValue.equals("hermit")) {
		reasonerFactoryClassName = "org.semanticweb.HermiT.ReasonerFactory";
	    } else {
		System.err.println("Unrecognized reasoner option: " + optValue);
		System.err.println("using elk reasoner instead");
		reasonerFactoryClassName = ELK_REASONER_FACTORY_CLASS_NAME;
	    }
	} else {
	    System.out.println("No reasoner option specified.  Using ELK as default reasoner.");
	    reasonerFactoryClassName = ELK_REASONER_FACTORY_CLASS_NAME;
	}

	OWLReasonerFactory rf = (OWLReasonerFactory)Class.forName(
			  reasonerFactoryClassName).newInstance();

	return rf;
    }

    protected void processResults(DronDlQueryResult result,
    	PrintStream out) {
	

	Set<OWLClass> clsResult = result.getClassResults().getFlattened();
        for (OWLClass c : clsResult) {
	    AnnotationExtractor leLabel = new AnnotationExtractor(labelIRI);
	    AnnotationExtractor leRxcui = new AnnotationExtractor(rxcuiIRI);
	    for (OWLOntology o : importClosure) {
		for (OWLAnnotation anno : getAnnotationObjects(c, o)) {
		    anno.accept(leLabel);
		    anno.accept(leRxcui);
		}
	    }
	    out.println(leLabel.getResult() + "\t" + c.getIRI().toString() + "\t" + leRxcui.getResult() + "\t" + "OWLClass");
        }

       Set<OWLNamedIndividual> indResult = result.getIndividualResults().getFlattened();
        for (OWLNamedIndividual i : indResult) {
	    AnnotationExtractor leLabel = new AnnotationExtractor(labelIRI);
	    AnnotationExtractor leRxcui = new AnnotationExtractor(rxcuiIRI);
	    for (OWLOntology o : importClosure) {
		for (OWLAnnotation anno : getAnnotationObjects(i, o)) {
		    anno.accept(leLabel);
		    anno.accept(leRxcui);
		}
	    }
	    out.println(leLabel.getResult() + "\t" + i.getIRI().toString() + "\t" + leRxcui.getResult() + "\t" + "OWLNamedIndividual");
        }
        
        out.close();
    }

    protected void setupOutputStream() {
	outStream = new PrintStream[dlQueryTxt.length];

	if (cl.hasOption("output")) {
	    try {
	    String[] vals = cl.getOptionValues("output");
	    for (int i=0; i<vals.length; i++) {
			File f = new File(vals[i]);
			outStream[i] = new PrintStream(f);
		}
	    } catch (IOException ioe) {
		System.err.println("Could not create output file " + cl.getOptionValue("output"));
		ioe.printStackTrace();
		System.out.println("Using standard output instead.");
	    }
	} 

	for (int i=0; i<outStream.length; i++) {
		if (outStream[i] == null) outStream[i] = System.out;
	}
    }

    public static void main(String[] args) {
	DronDlQuery ddlq = new DronDlQuery();
	ddlq.runQuery(args);
    }
}