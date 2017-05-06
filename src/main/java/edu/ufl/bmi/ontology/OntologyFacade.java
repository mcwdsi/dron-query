package edu.ufl.bmi.ontology;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

import edu.ufl.bmi.ontology.IriCounter;

public class OntologyFacade {

    OWLOntology o;
    OWLDataFactory odf;
    OWLOntologyManager om;
    Set<OWLOntology> importClosure;

    IriCounter iriCounter;

    public OntologyFacade(IRI ontologyIriLocation, IriCounter counter) throws OWLOntologyCreationException {
        om = OWLManager.createOWLOntologyManager();
	odf = OWLManager.getOWLDataFactory();
        o = om.loadOntologyFromOntologyDocument(ontologyIriLocation);
        importClosure = o.getImportsClosure();
	this.iriCounter = counter;
    }

    public OntologyFacade(File ontologyFileLocation, IriCounter counter) throws OWLOntologyCreationException {
	om = OWLManager.createOWLOntologyManager();
	odf = OWLManager.getOWLDataFactory();
	o = om.loadOntologyFromOntologyDocument(ontologyFileLocation);
        importClosure = o.getImportsClosure();
	this.iriCounter = counter;
    }

    public OWLObjectPropertyAssertionAxiom createOWLObjectPropertyAssertion(OWLNamedIndividual source, IRI objPropIri, OWLNamedIndividual target) {
        OWLObjectProperty oop = odf.getOWLObjectProperty(objPropIri);
        OWLObjectPropertyAssertionAxiom oopaa = odf.getOWLObjectPropertyAssertionAxiom(oop, source, target);
        om.addAxiom(o, oopaa);
	return oopaa;
    }

    public OWLNamedIndividual createNamedIndividualWithTypeAndLabel(
									   IRI classTypeIri, IRI labelPropIri, String rdfsLabel) {
        return createNamedIndividualWithIriTypeAndLabel(iriCounter.nextIri(), classTypeIri, labelPropIri, rdfsLabel);
    }

    public OWLNamedIndividual createNamedIndividualWithIriTypeAndLabel(
                                                                              IRI individualIri, IRI classTypeIri, IRI labelPropIri, String rdfsLabel) {
        OWLNamedIndividual oni = odf.getOWLNamedIndividual(individualIri);
        OWLClassAssertionAxiom ocaa = odf.getOWLClassAssertionAxiom(odf.getOWLClass(classTypeIri), oni);
        om.addAxiom(o, ocaa);
        addAnnotationToNamedIndividual(oni, labelPropIri, rdfsLabel);
        return oni;
    }

    public void addAnnotationToNamedIndividual(OWLNamedIndividual oni, IRI annPropIri, String value) {
        OWLLiteral li = odf.getOWLLiteral(value);
        OWLAnnotationProperty la = odf.getOWLAnnotationProperty(annPropIri);
        OWLAnnotation oa = odf.getOWLAnnotation(la, li);
        OWLAnnotationAssertionAxiom oaaa = odf.getOWLAnnotationAssertionAxiom(oni.getIRI(), oa);
        om.addAxiom(o, oaaa); 
    }

    public boolean containsClassIri(IRI classIri) {
	return o.containsClassInSignature(classIri, false);
    }
}