package edu.ufl.bmi.ontology.dronquery;

import static org.semanticweb.owlapi.search.EntitySearcher.getAnnotationObjects;

import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxClassExpressionParser;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import edu.ufl.bmi.ontology.dronquery.AnnotationExtractor;


public final class DlQueryExecutor {

    private final OWLReasonerFactory reasonerFactory;
    private final OWLOntologyManager manager;
    private final OWLOntology ontology;
    private final OWLDataFactory df;

    private final IRI labelIri = IRI.create("http://www.w3.org/2000/01/rdf-schema#label");

    private OWLReasoner reasoner;
    private PrefixManager pm;
    private String[] clsNames;

    ManchesterOWLSyntaxClassExpressionParser parser;

    public DlQueryExecutor(OWLReasonerFactory reasonerFactory, OWLOntology inputOntology) {
        this.reasonerFactory = reasonerFactory;
        this.ontology = inputOntology;
        
        manager = ontology.getOWLOntologyManager();
        this.df = manager.getOWLDataFactory();
        ShortFormProvider sfp = new SimpleShortFormProvider();
        Set<OWLOntology> importClosure = ontology.getImportsClosure();
        BidirectionalShortFormProvider bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager, importClosure, sfp);
        
        parser = new ManchesterOWLSyntaxClassExpressionParser(manager.getOWLDataFactory(), new ShortFormEntityChecker(bidiShortFormProvider));
        pm = new DefaultPrefixManager();
        pm.setPrefix("dq:","https://purl.obolibrary.org/obo/dron/dron-query/");
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

    public void addQueriesAsClassesToOntology(String[] dlQueryTxt) {
        this.clsNames = new String[dlQueryTxt.length];
        for (int i=0; i<dlQueryTxt.length; i++) {
            OWLClassExpression clExp = parser.parse(dlQueryTxt[i]);
            this.clsNames[i] = "Q" + i;
            OWLClass cls = this.df.getOWLClass(this.clsNames[i], pm);
            OWLEquivalentClassesAxiom oeca = this.df.getOWLEquivalentClassesAxiom(cls, clExp);
            this.manager.addAxiom(this.ontology, oeca);
        }
    }

    public void precomputeInferences(boolean includeInds) {
       if (reasoner == null) {
           reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
       }
       if (includeInds) {
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.CLASS_ASSERTIONS);
        } else {
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        }
    }

    public DronDlQueryResult getQueryResult(int queryNum, boolean includeInds, boolean includeEquivalentNamedClasses) {
        OWLClass clsQuery = this.df.getOWLClass(this.clsNames[queryNum], pm);
        NodeSet<OWLClass> result = reasoner.getSubClasses(clsQuery, false);
        DronDlQueryResult ddqr = new DronDlQueryResult();
        ddqr.addClassResults(result);

        if (includeEquivalentNamedClasses) {
            Node<OWLClass> clsResult = reasoner.getEquivalentClasses(clsQuery);
            if (!result.isEmpty())
                ddqr.addClassResult(clsResult);
        }
        if (includeInds) {
            NodeSet<OWLNamedIndividual> instResultFalse = reasoner.getInstances(clsQuery, false);
            System.out.println(instResultFalse.getNodes().size());
            NodeSet<OWLNamedIndividual> instResultTrue = reasoner.getInstances(clsQuery, true);
            System.out.println(instResultTrue.getNodes().size());
            ddqr.addIndividualResults(instResultTrue);
        }
        return ddqr;
    }


    public DronDlQueryResult runQuery(String dlQueryTxt, boolean includeIndividuals, boolean includeEquivalentNamedClasses) {
	   if (reasoner == null) {
	       reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
	   }

        OWLClassExpression clExp = parser.parse(dlQueryTxt);
        DronDlQueryResult ddqr = new DronDlQueryResult();
        NodeSet<OWLClass> result = reasoner.getSubClasses(clExp, false);    
	    ddqr.addClassResults(result);

        if (includeEquivalentNamedClasses) {
            Node<OWLClass> clsResult = reasoner.getEquivalentClasses(clExp);
            if (!result.isEmpty())
                ddqr.addClassResult(clsResult);
        }

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