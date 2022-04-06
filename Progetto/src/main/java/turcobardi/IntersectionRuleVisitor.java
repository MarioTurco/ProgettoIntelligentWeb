package turcobardi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;

public class IntersectionRuleVisitor implements OWLObjectVisitor{
	
	private Set<OWLClassExpression> operandsRet = new HashSet<>();
	
	public void visit(OWLObjectIntersectionOf o) {
		List<OWLClassExpression> operands = o.getOperandsAsList();
		System.out.print(operands);
		operandsRet.addAll(operands);
		System.out.print("\n");
	}
	
	public void visit(OWLEquivalentClassesAxiom eq) {

		eq.getOperandsAsList().get(1).accept(this); //destra
		//eq.getOperandsAsList().get(0).accept(this); //sinistra
		return;
	}
	
	
	public Set<OWLClassExpression> getOperands(){
		Set<OWLClassExpression> tmp = new HashSet<>(operandsRet);
		operandsRet.clear();
		return tmp;
	}
	
}


