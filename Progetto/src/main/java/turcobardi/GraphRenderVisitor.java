package turcobardi;

import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;


/**
 * Visitor di formule che permette di ottenere la formula visitata in modo compatto con simboli utf-8 per essere utilizzata nel grafo del tableaux
 * @author turco
 *
 */
public class GraphRenderVisitor implements OWLObjectVisitor{
	
	private IRI iri = null;
	private final char intersect = '\u2293';
	private final char union = '\u2294';
	private final char foreach = '\u2200';
	private final char exists = '\u2203';
	private final char not = '\u00AC';
	private final char inclusion = '\u2291';
	private String formula= "";
	
	
	public GraphRenderVisitor(IRI iri) {
		this.iri=iri;
	}
	
	
	/**
	 *  Stampa la formula visitata
	 */
	public void printFormula() {
		System.out.println(formula);
	}
	
		
	/** Metodo che restituisce l'ultima formula visitata e la cancella dal visitor. </br>
	 * Si noti che quindi dopo la chiamata di tale funzione, il valore <code> this.formula </code> sarà la stringa vuota
	 * @return una stringa contenente la formula 
	 */
	public String getAndClearFormula() {
		String tmp = new String(formula);
		formula = "";
		//rimuoviamo gli ultimi due caratteri, ovvero una virgola ed uno spazio		
		return tmp.substring(0, tmp.length()-2);
	}
	
	public void visit(OWLObjectSomeValuesFrom desc) {
		formula=formula.concat("" + exists + "" + removeIRIFromString(iri,desc.getProperty().toString()) );
		desc.getProperty().accept(this);
		formula=formula.concat(".");
	    desc.getFiller().accept(this);
		
	}
	
	public void visit(OWLClass c) {
		formula=formula.concat(removeIRIFromString(iri,c.toString()) + " ");
		return;
	}
	
	public void visit(OWLObjectComplementOf eq) {
		formula=formula.concat(""+not);
		eq.getOperand().accept(this);
	}
	
	public void visit(OWLSubClassOfAxiom sub) {
		sub.getSubClass().accept(this);
		formula=formula.concat(inclusion+"");
		sub.getSuperClass().accept(this);
	}
	
	public void visit(OWLEquivalentClassesAxiom eq) {
		eq.getOperandsAsList().get(0).accept(this); 
		formula=formula.concat(" = ");
		eq.getOperandsAsList().get(1).accept(this); 
		return;
	}
	
	public void visit(OWLObjectIntersectionOf o) {
		List<OWLClassExpression> operands = o.getOperandsAsList(); 
		int i=operands.size()-1;
		formula=formula.concat("(");
		for(OWLClassExpression ex: operands) {
			ex.accept(this);
			if(i > 0) {
				formula=formula.concat(intersect + " ");
				i--;
			}
		}
		formula=formula.concat(")");
		
	}
	
	public void visit(OWLObjectAllValuesFrom desc) {
		formula=formula.concat(" " + foreach + " " + removeIRIFromString(iri, desc.getProperty().toString()));
		desc.getProperty().accept(this);
		formula=formula.concat(".");
	    desc.getFiller().accept(this);
	    		
	}
	

	public void visit(OWLNamedIndividual i) {
		formula=formula.concat(i.getIRI().getShortForm());
	}
	
	public void visit(OWLIndividual i) {
		for (OWLNamedIndividual ind: i.getIndividualsInSignature()) {
			ind.accept(this);
		}
		
	}
	
	public void visit(OWLClassAssertionAxiom o) {
		OWLIndividual individual = o.getIndividual();
		o.getClassExpression().accept(this);
		formula=formula.concat("(");
		individual.accept(this);
		formula=formula.concat(")");
	}
	
	public void visit(OWLObjectUnionOf o) {
		List<OWLClassExpression> operands = o.getOperandsAsList(); 
		int i=operands.size()-1;
		formula=formula.concat("(");
		for(OWLClassExpression ex: operands) {
			ex.accept(this);
			if(i > 0) {
				formula=formula.concat(union +  " ");
				i--;
			}
		}
		formula=formula.concat(")");
	}
	
    
    /** Data una stringa ed un iri, rimuove dalla stringa l'iri più i caratteri <code> "#", "<", ">"</code>
     * @param iri
     * @param str
     * @return la stringa modificata
     */
    private String removeIRIFromString(IRI iri, String str) {
		str = str.replace(iri.toString(), "");
		str = str.replace("#", "");
		str = str.replace("<", "");
		str = str.replace(">", "");
		return str;
	}
    
    
	/**
	 * Aggiunge una virgola come ultimo carattere della formula
	 */
	public void addColonToFormula() {
		formula = formula.concat(", ");
		
	}
		
}
