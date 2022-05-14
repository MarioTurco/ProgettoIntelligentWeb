package turcobardi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;


/**
 *Visitor per il lazy unfolding che tiene traccia delle dipendenze dei concetti 
 *Per poter verificare se questi sono aciclici
 *
 */
public class LazyUnfoldingVisitor implements OWLObjectVisitor{
	
	private Set<OWLClass> dependencies = new HashSet<>();
	
	public void visit(OWLObjectComplementOf eq) {
		eq.getOperand().accept(this);
	}
	
	public void visit(OWLObjectIntersectionOf o) {
		List<OWLClassExpression> operands = o.getOperandsAsList(); 
		for(OWLClassExpression ex: operands) {
			ex.accept(this);
		}
	}

	public void visit(OWLClassAssertionAxiom ca) {
		ca.getClassExpression().accept(this);
	}
	
	public void visit(OWLClass c) {
		dependencies.add(c);
		return;
	}
	
	public void visit(OWLObjectSomeValuesFrom desc) {
		desc.getProperty().accept(this);
	    desc.getFiller().accept(this);
	}
	
	public void visit(OWLObjectAllValuesFrom desc) {
		desc.getProperty().accept(this);
	    desc.getFiller().accept(this);
	}
	
	public void visit(OWLObjectUnionOf o) {
		List<OWLClassExpression> operands = o.getOperandsAsList(); 
		for(OWLClassExpression ex: operands) {
			ex.accept(this);
		}
	}
	
	public Set<OWLClass> getDependencies() {
		Set<OWLClass> tmp = new HashSet<>(dependencies);
		dependencies.clear();
		return tmp;
	}
	
}
