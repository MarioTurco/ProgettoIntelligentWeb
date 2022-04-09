package turcobardi;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

public class EquivalenceRuleVisitor implements OWLObjectVisitor{
		
		private OWLClassExpression leftSide = null;
		
		public void visit(OWLEquivalentClassesAxiom eq) {
			eq.getOperandsAsList().get(1).accept(this); //sinistra
			leftSide = eq.getOperandsAsList().get(1);
			return;
		}
		
		
		public OWLClassExpression getOperands(){
			return leftSide;
		}
		
		public void visit(OWLOntology concept) {
			for(OWLAxiom axiom: concept.getLogicalAxioms(Imports.fromBoolean(false))) {
				System.out.println(axiom.getAxiomType().getName());
				axiom.getNNF().accept(this);			//PRENDI OGNI ASSIOMA IN NNF E ESPANDILO		
			}
		}
		
}
