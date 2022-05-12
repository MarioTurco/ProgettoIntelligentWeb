package turcobardi;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;

public class EquivalenceRuleVisitor implements OWLObjectVisitor{
		
		private OWLClassExpression rightSide = null;
		
		public void visit(OWLEquivalentClassesAxiom eq) {
			if(eq.getOperandsAsList().size() > 1)
				rightSide = eq.getOperandsAsList().get(1);
			else 
				rightSide = eq.getOperandsAsList().get(0);
			return;
		}
		
		public OWLClassExpression getRightSide(){
			return rightSide;
		}
		
		//TODO da cancellare
		//OLD non più usato
		public void visit(OWLOntology concept) {
			for(OWLAxiom axiom: concept.getLogicalAxioms(Imports.fromBoolean(false))) {
				axiom.getNNF().accept(this);			//PRENDI OGNI ASSIOMA IN NNF E ESPANDILO		
			}
		}
		
}
