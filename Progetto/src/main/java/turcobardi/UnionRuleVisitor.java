package turcobardi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;

public class UnionRuleVisitor implements OWLObjectVisitor {
		
		private Set<OWLClassExpression> operandsRet = null; 
		
		public void visit(OWLObjectUnionOf o) {
			List<OWLClassExpression> operands = o.getOperandsAsList();
			operandsRet = new HashSet<>();
			operandsRet.addAll(operands);
		}
		
		public Set<OWLClassExpression> getOperands(){
			return operandsRet;
		}

}
