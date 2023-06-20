package edu.ufl.bmi.ontology.dronquery;

import java.util.Set;
import java.util.HashSet;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

public class DronDlQueryResult {
	Set<OWLClass> classResults;
	Set<OWLNamedIndividual> individualResults;

	public void addClassResults(NodeSet<OWLClass> clsResult) {
		if (classResults == null) classResults = clsResult.getFlattened();
		else {
			classResults.addAll(clsResult.getFlattened());
		}
	}

	public void addClassResult(Node<OWLClass> clsResult) {
		if (classResults == null) {
			classResults = new HashSet<OWLClass>();
		}
		classResults.addAll(clsResult.getEntities());
	}

	public void addIndividualResults(NodeSet<OWLNamedIndividual> indResult) {
		if (individualResults == null) individualResults = indResult.getFlattened();
		else {
			individualResults.addAll(indResult.getFlattened());
		}
	}

	public Set<OWLClass> getClassResults() {
		return classResults;
	}

	public Set<OWLNamedIndividual> getIndividualResults() {
		return individualResults;
	}
}