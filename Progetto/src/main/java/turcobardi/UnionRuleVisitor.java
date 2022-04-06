package turcobardi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;

public class UnionRuleVisitor implements OWLObjectVisitor {
		
		private Set<OWLClassExpression> operandsRet = null; 
		public UnionRuleVisitor() {
			operandsRet = new HashSet<>();
		}
		
		public void visit(OWLObjectUnionOf o) {
			List<OWLClassExpression> operands = o.getOperandsAsList();
			operandsRet.addAll(operands);
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
