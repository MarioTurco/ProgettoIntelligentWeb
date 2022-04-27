package turcobardi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;

public class IntersectionRuleVisitor implements OWLObjectVisitor{
	
	private Set<OWLClassExpression> operandsRet = new HashSet<>();
	
	public void visit(OWLObjectIntersectionOf o) {
		List<OWLClassExpression> operands = o.getOperandsAsList();
		//System.out.println("Entrato "+ operands);
		operandsRet.addAll(operands);
		//System.out.print("\n");
	}
	
	public void visit(OWLEquivalentClassesAxiom eq) {

		eq.getOperandsAsList().get(1).accept(this); //destra
		//eq.getOperandsAsList().get(0).accept(this); //sinistra
		return;
	}
	
	public void visit(OWLClassAssertionAxiom ca) {
		ca.getClassExpression().accept(this);
	}
	
	public Set<OWLClassExpression> getOperands(){
		Set<OWLClassExpression> tmp = new HashSet<>(operandsRet);
		operandsRet.clear();
		return tmp;
	}
	
}


