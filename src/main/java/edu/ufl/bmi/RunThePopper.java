package edu.ufl.bmi;

import java.io.File;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.IRI;

import edu.ufl.bmi.ontology.population.ThePopper;
import edu.ufl.bmi.ontology.population.NcbiTaxonId;

public class RunThePopper {

    public static void main(String[] args) {

	ThePopper p = new ThePopper(new File(args[0]));
	NcbiTaxonId taxon = new NcbiTaxonId("9606");
	IRI regionIri = IRI.create("http://purl.obolibrary.org/obo/GEO_000000380"); //region of Florida
	try {
	    p.checkAndCreatePopulation(taxon, regionIri);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	} catch (OWLOntologyCreationException ooce) {
	    ooce.printStackTrace();
	}
    }
}
