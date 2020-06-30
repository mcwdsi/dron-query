package edu.ufl.bmi.ontology.dronquery;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.NodeSet;

public class DronDlQueryResult {
	NodeSet<OWLClass> classResults;
	NodeSet<OWLNamedIndividual> individualResults;

	public void addClassResults(NodeSet<OWLClass> clsResult) {
		if (classResults == null) classResults = clsResult;
		else {
			System.err.println("Accumulating OWLClass results not yet supported.");
		}
	}

	public void addIndividualResults(NodeSet<OWLNamedIndividual> indResult) {
		if (individualResults == null) individualResults = indResult;
		else {
			System.err.println("Accumulating OWLNamedIndividual results not yet supported.");	
		}
	}

	public NodeSet<OWLClass> getClassResults() {
		return classResults;
	}

	public NodeSet<OWLNamedIndividual> getIndividualResults() {
		return individualResults;
	}
}