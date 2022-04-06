package turcobardi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.HasLogicalAxioms;
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
	//private Set<OWLObject> abox = null;
	
	public ALCReasoner(OWLOntology concept) {
		this.concept = concept;
		//this.abox = new HashSet<>();
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
	
	private Set<OWLObject> unionRule(Set<OWLObject> abox) {
		
		Set<OWLObject> toAdd = new HashSet<>(); 
		UnionRuleVisitor vis = new UnionRuleVisitor();
		for(OWLObject a: abox) {

			a.accept(vis);
			toAdd.addAll(vis.getOperands());

    	}
		return toAdd;
	}
	
	private Set<OWLObject> intersectionRule(Set<OWLObject> abox) {

		Set<OWLObject> toAdd = new HashSet<>(); 
		IntersectionRuleVisitor vis = new IntersectionRuleVisitor();
		for(OWLObject a: abox) {

			a.accept(vis);
			toAdd.addAll(vis.getOperands());

    	}
		return toAdd;
	}
	
	public boolean alcTableaux() {
		int ind=0;
		Set<OWLObject> Lx = new HashSet<>();
		Set<OWLObject> aBox = new HashSet<>();
		for (OWLObject obj :concept.getLogicalAxioms()) {
			aBox.add(((OWLLogicalAxiom) obj).getNNF());
		}
		
		return implementTableaux(ind, Lx, aBox);	
	}
	
	private boolean hasClash(Set<OWLObject> abox) {
		for (OWLObject o: abox) {
			if (o instanceof OWLClassExpression) {
				if(((OWLClassExpression) o).isClassExpressionLiteral() && abox.contains(((OWLClassExpression) o).getObjectComplementOf())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean implementTableaux(int ind, Set<OWLObject> Lx, Set<OWLObject> aBox) {
		OntologyPrintingVisitor printer = new OntologyPrintingVisitor(concept.getOntologyID().getOntologyIRI().get(), "");
		
		/*System.out.println("Abox prima: ");
    	for(OWLObject a: aBox){
    		//System.out.print(a);
    		a.accept(printer);
    	}*/
		//REGOLA INTERSEZIONE
    	aBox.addAll(this.intersectionRule(aBox));
    	//REGOLA UNIONE
    	for (OWLObject o : this.unionRule(aBox)) {
    		Set<OWLObject> intersec = new HashSet<>(((OWLClassExpression) o).asDisjunctSet());
    		intersec.retainAll(aBox);
    		if(intersec.size()==0) {
    			Set<OWLObject> tmp = new HashSet<>(aBox);
    			tmp.add(o);
    			if(hasClash(tmp)) {
    				return false;
    			}
    			implementTableaux(ind, Lx, tmp);
    		}
    	}
    	
		System.out.println("\nAbox dopo: " );
		for(OWLObject a: aBox){
    		a.accept(printer);
    		System.out.print(",");
    	}
		return true;
	}
	
}
