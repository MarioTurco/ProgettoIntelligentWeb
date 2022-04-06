package turcobardi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;

public class IntersectionRuleVisitor implements OWLObjectVisitor{
	
	private Set<OWLClassExpression> operandsRet = null; 
	
	public void visit(OWLObjectIntersectionOf o) {
		List<OWLClassExpression> operands = o.getOperandsAsList();
		operandsRet = new HashSet<>();
		operandsRet.addAll(operands);
	}
	
	public Set<OWLClassExpression> getOperands(){
		return operandsRet;
	}
	
}


