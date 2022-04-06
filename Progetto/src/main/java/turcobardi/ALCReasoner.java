package turcobardi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;

/*
 * 
 * This class is the ALC Reasoner and contains all the methods
 * for the reasoning
 */
public class ALCReasoner{
	
	private OWLOntology concept = null;
	private Set<OWLObject> abox = null;
	
	public ALCReasoner(OWLOntology concept) {
		this.concept = concept;
		this.abox = new HashSet<>();
	}
	
	
	
	/*
	private Set<OWLLogicalAxiom> intersectionRule(OWLLogicalAxiom intersection) {
		if(!(intersection instanceof OWLObjectIntersectionOf))
			return null;
		Set<OWLClassExpression> toAdd = new HashSet<>();
		intersection.getAxiomType();
		for(OWLClassExpression ex: operands) {
			toAdd.add(ex);
		}
		return null;
	}*/
	
	private void UnionRule() {
		
		return  ;
	}
	
	private Set<OWLLogicalAxiom> intersectionRule(OWLObjectIntersectionOf intersection) {
		List<OWLClassExpression> operands = intersection.getOperandsAsList(); 
		Set<OWLLogicalAxiom> toAdd = new HashSet<>();
		for(OWLClassExpression ex: operands) {
			toAdd.add((OWLLogicalAxiom) ex);
		}
		return toAdd;
	}
	
	public boolean alcTableaux() {
		OntologyPrintingVisitor printer = new OntologyPrintingVisitor(concept.getOntologyID().getOntologyIRI().get(), "");
		int ind = 0;
		Set<OWLLogicalAxiom> Lx = new HashSet<>();
		
		Set<OWLLogicalAxiom> aBox = concept.getLogicalAxioms();
		/*for(OWLLogicalAxiom a: aBox) {
			Lx.add(a);
		}*/
		System.out.println("Abox prima: ");
    	for(OWLLogicalAxiom a: aBox){
    		a.accept(printer);
    	}
		//Usiamo la regola dell'AND
		for(OWLLogicalAxiom a: aBox) {
			if(a.getAxiomType().toString() == "OWLObjectIntersectionOf") {
				aBox.addAll(intersectionRule((OWLObjectIntersectionOf) a));
			}
		}
		System.out.println("Abox dopo: " );
		for(OWLLogicalAxiom a: aBox){
    		a.accept(printer);
    	}
		return false;
		
		//return implementTableaux(ind, Lx);	
	}
	
	private boolean implementTableaux(int ind, Set<OWLObject> Lx) {
		
		return false;
	}
	
}
