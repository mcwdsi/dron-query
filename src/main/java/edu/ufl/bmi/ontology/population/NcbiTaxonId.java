package edu.ufl.bmi.ontology.population;

import java.util.ArrayList;
import org.semanticweb.owlapi.model.IRI;

public class NcbiTaxonId {

    String taxonId;
    IRI iri;

    String name;
    ArrayList<String> alternativeTerms;

    public NcbiTaxonId(String id) {
        taxonId = id;
        iri = IRI.create("http://purl.obolibrary.org/obo/NCBITaxon_" + id);
    }

    public String getId() {
        return taxonId;
    }

    public IRI getIri() {
        return iri;
    }

}