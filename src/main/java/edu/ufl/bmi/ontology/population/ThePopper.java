package edu.ufl.bmi.ontology.population;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.IRI;


import edu.ufl.bmi.ontology.OntologyFacade;
import edu.ufl.bmi.ontology.IriCounter;
import edu.ufl.bmi.ontology.population.NcbiTaxonId;

public class ThePopper {
    OntologyFacade apollo;
    OntologyFacade obcide;

    File config;

    NcbiTaxonId taxon;
    IRI regionIri;

    public ThePopper(File configFile) {
	this.config = configFile;
    }

    protected void initFromConfig() throws IOException, OWLOntologyCreationException {
	FileReader fr = new FileReader(config);
	LineNumberReader lnr = new LineNumberReader(fr);
	String line;
	while ((line=lnr.readLine())!=null) {
	    String[] flds = line.split(Pattern.quote("="));
	    if (flds[0].equals("apollofile")) {
		loadOntology(flds[1], "apollo");
	    } else if (flds[0].equals("obcfile")) {
		loadOntology(flds[1], "obcowl");
	    } else {
		System.err.println("Ignoring entry: " + line);
	    }
	}
	fr.close();
	lnr.close();
    }

    protected void loadOntology(String fName, String ontology) throws IOException, OWLOntologyCreationException {
	FileReader fr = new FileReader(fName);
	LineNumberReader lnr = new LineNumberReader(fr);
	String line;
	String ontologyFileName = "", iriBase = "";
	long iriCounter = -1;;
	int iriLen = -1;
	while((line=lnr.readLine())!=null) {
	    String[] flds = line.split(Pattern.quote("="));
	    if (flds[0].equals("file")) {
		ontologyFileName = flds[1].replaceFirst("^~", System.getProperty("user.home"));
	    } else if (flds[0].equals("iricounter")) {
		iriCounter = Long.parseLong(flds[1]);
	    } else if (flds[0].equals("iribase")) {
		iriBase = flds[1];
	    } else if (flds[0].equals("irilen")) {
		iriLen = Integer.parseInt(flds[1]);
	    } else {
		System.err.println("Ignoring entry: " + line);
	    }
	}

	IriCounter c = new IriCounter(iriCounter, iriLen, iriBase);
	File f = new File(ontologyFileName);
	if (ontology.equals("apollo")) {
	    apollo = new OntologyFacade(f, c); 
	} else if (ontology.equals("obcowl")) {
	    obcide = new OntologyFacade(f, c);
	} else {
	    System.err.println("Don't know ontology " + ontology);
	}
    }

    public void checkAndCreatePopulation(NcbiTaxonId taxon, IRI regionIri) throws IOException, OWLOntologyCreationException {
	initFromConfig();
	this.taxon = taxon;
	this.regionIri = regionIri;
	if (!organismClassExistsInApollo()) {
	    //go get taxon info from NCBI Taxonomy API
	    //something like apollo.createClassWithLabel(taxon.getIri(), annPropIri, taxon.getScientificName());
	} else {
	    System.out.println("taxon class already exists: " + taxon.getId() + "\t" + taxon.getIri());
	}
    }

    public boolean organismClassExistsInApollo() {
	return apollo.containsClassIri(taxon.getIri());
    }
}