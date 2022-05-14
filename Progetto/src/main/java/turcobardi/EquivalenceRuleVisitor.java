package turcobardi;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;


/**
 * Visitor che mantiene in memoria il lato destro dell'equivalenza che visita
 */
public class EquivalenceRuleVisitor implements OWLObjectVisitor{
		
		private OWLClassExpression rightSide = null;
		
		public void visit(OWLEquivalentClassesAxiom eq) {
			if(eq.getOperandsAsList().size() > 1)
				rightSide = eq.getOperandsAsList().get(1);
			else 
				rightSide = eq.getOperandsAsList().get(0);
			
			for (OWLClass cl: rightSide.getClassesInSignature()) {
				if (cl.getIRI().toString().contains("Query")) {
					rightSide = eq.getOperandsAsList().get(0);
				}
			}
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
