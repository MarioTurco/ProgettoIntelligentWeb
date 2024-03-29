package turcobardi;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;


/**
 * Visitor dei quantificatori universali
 */
public class ForAllRuleVisitor implements OWLObjectVisitor{

	private List<OWLObject> ret = new ArrayList<>();
	

	public void visit(OWLObjectAllValuesFrom desc) {
		ret.add(desc.getProperty());
	    ret.add(desc.getFiller());		
	}
	
	public void visit(OWLClassAssertionAxiom ca) {
		ca.getClassExpression().accept(this);
	}
	
	/**
	 * @return la property ed il filler(Concetto) dell'esistenziale visitato
	 */
	public List<OWLObject> getPropertyAndFiller() {
		List<OWLObject> tmp = new ArrayList<>(ret);
		ret.clear();
		return tmp;
	}
}
