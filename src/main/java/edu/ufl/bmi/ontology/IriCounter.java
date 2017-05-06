package edu.ufl.bmi.ontology;

import org.semanticweb.owlapi.model.IRI;

public class IriCounter {

    long counter;
    int iriLength;
    String iriPrefix;

    public IriCounter(long counter, int length, String prefix) {
	this.counter = counter;
	this.iriLength = length;
	this.iriPrefix = prefix;
    }

    public IRI nextIri() {
        String counterTxt = Long.toString(counter++);
        StringBuilder sb = new StringBuilder(iriPrefix);
        int numZero = iriLength-counterTxt.length();
        for (int i=0; i<numZero; i++) 
            sb.append("0");
        sb.append(counterTxt);
        return IRI.create(new String(sb));
    }
}