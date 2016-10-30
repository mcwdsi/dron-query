package edu.ufl.bmi.ontology.dronquery;
/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitor;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

/**
 * Simple visitor that grabs any labels on an entity.
 * 
 * @author Sean Bechhofer, The University Of Manchester, Information Management
 *         Group
 * @since 2.0.0
 */
@SuppressWarnings("javadoc")
public class AnnotationExtractor implements OWLAnnotationObjectVisitor {
	
	IRI propIRI;
	
	public AnnotationExtractor(IRI annoPropIRI) {
		if (annoPropIRI == null) throw new NullPointerException("annoPropIRI may not be null.");
		propIRI = annoPropIRI;
	}

    @Nullable
    String result = null;

    
    public void visit(OWLAnonymousIndividual individual) {}

  
    public void visit(IRI iri) {}

    
    public void visit(OWLLiteral literal) {}

    
    public void visit(@Nonnull OWLAnnotation node) {
        /*
         * If it's a label, grab it as the result. Note that if there are
         * multiple labels, the last one will be used.
         */
        if (node.getProperty().getIRI().equals(propIRI)) {
        	//System.out.println(node.toString());
            OWLLiteral c = (OWLLiteral) node.getValue();
            result = c.getLiteral();
            if (result == null) {
            	result = node.toString();
            }
        }
    }

    
    public void visit(OWLAnnotationAssertionAxiom axiom) {}

   
    public void visit(OWLAnnotationPropertyDomainAxiom axiom) {}

    
    public void visit(OWLAnnotationPropertyRangeAxiom axiom) {}

   
    public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {}

    
    public void visit(OWLAnnotationProperty property) {}

   
    public void visit(OWLAnnotationValue value) {}

    @Nullable
    public String getResult() {
        return result;
    }
}
