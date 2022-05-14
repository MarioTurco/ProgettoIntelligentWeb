package turcobardi;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;


/**
 * Visitor dei quantificatori esistenziali
 */
public class ExistsRuleVisitor implements OWLObjectVisitor{

	private OWLObjectPropertyExpression property;
	private OWLClassExpression filler;
	private List<OWLObject> ret = new ArrayList<>();
	
	public void visit(OWLObjectSomeValuesFrom desc) {
		property = desc.getProperty();
	    filler = desc.getFiller();
	    ret.add(property);
	    ret.add(filler);
		
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
	
	/**
	 * @return il filler (Concetto) dell'esistenziale visitato
	 */
	public OWLClassExpression getFiller() {
		return filler;
	}
}
