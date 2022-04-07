package turcobardi;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;

public class ForAllRuleVisitor implements OWLObjectVisitor{

	private OWLObjectPropertyExpression property;
	private OWLClassExpression filler;
	private List<OWLObject> ret = new ArrayList<>();
	

	public void visit(OWLObjectAllValuesFrom desc) {
		ret.add(desc.getProperty());
	    ret.add(desc.getFiller());		
	}
	
	public void visit(OWLClassAssertionAxiom ca) {
		ca.getClassExpression().accept(this);
	}
	
	public List<OWLObject> getPropertyAndFiller() {
		List<OWLObject> tmp = new ArrayList<>(ret);
		ret.clear();
		return tmp;
	}
}
