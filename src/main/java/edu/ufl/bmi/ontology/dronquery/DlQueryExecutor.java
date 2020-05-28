package edu.ufl.bmi.ontology.dronquery;

import static org.semanticweb.owlapi.search.EntitySearcher.getAnnotationObjects;

import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxClassExpressionParser;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import edu.ufl.bmi.ontology.dronquery.AnnotationExtractor;


public final class DlQueryExecutor {

    private final OWLReasonerFactory reasonerFactory;
    private final OWLOntology ontology;

    private final IRI labelIri = IRI.create("http://www.w3.org/2000/01/rdf-schema#label");

    private OWLReasoner reasoner;

    ManchesterOWLSyntaxClassExpressionParser parser;

    public DlQueryExecutor(OWLReasonerFactory reasonerFactory, OWLOntology inputOntology) {
        this.reasonerFactory = reasonerFactory;
        ontology = inputOntology;

        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        ShortFormProvider sfp = new SimpleShortFormProvider();
        Set<OWLOntology> importClosure = ontology.getImportsClosure();
        BidirectionalShortFormProvider bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager, importClosure, sfp);
        
        parser = new ManchesterOWLSyntaxClassExpressionParser(manager.getOWLDataFactory(), new ShortFormEntityChecker(bidiShortFormProvider));

    }

    private String labelFor(OWLClass clazz) {
        /*
         * Use a visitor to extract label annotations
         */
        AnnotationExtractor le = new AnnotationExtractor(labelIri);
        for (OWLAnnotation anno : getAnnotationObjects(clazz, ontology)) {
            anno.accept(le);
        }
        /* Print out the label if there is one. If not, just use the class URI */
        if (le.getResult() != null) {
            return le.getResult();
        } else {
            return clazz.getIRI().toString();
        }
    }


    public DronDlQueryResult runQuery(String dlQueryTxt, boolean includeIndividuals) {
	   if (reasoner == null) {
	       reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
	   }

        OWLClassExpression clExp = parser.parse(dlQueryTxt);
        DronDlQueryResult ddqr = new DronDlQueryResult();
        NodeSet<OWLClass> result = reasoner.getSubClasses(clExp, false);    
	    ddqr.addClassResults(result);

        if (includeIndividuals) {
           NodeSet<OWLNamedIndividual> instResultFalse = reasoner.getInstances(clExp, false);
	       System.out.println(instResultFalse.getNodes().size());
	       NodeSet<OWLNamedIndividual> instResultTrue = reasoner.getInstances(clExp, true);
	       System.out.println(instResultTrue.getNodes().size());
           ddqr.addIndividualResults(instResultTrue);

       }
        return ddqr;
    }
}