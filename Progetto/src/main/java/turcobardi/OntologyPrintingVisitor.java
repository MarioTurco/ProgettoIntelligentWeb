package turcobardi;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;


/**
 * Visitor di formule che permette di stampare la formula visitata in modo compatto con simboli utf-8
 * @author turco
 *
 */
public class OntologyPrintingVisitor implements OWLObjectVisitor{
	
	private IRI iri = null;
	private PrintStream out = null;
	private final char intersect = '\u2293';
	private final char union = '\u2294';
	private final char foreach = '\u2200';
	private final char exists = '\u2203';
	private final char not = '\u00AC';
	private final char inclusion = '\u2291';
	//private final char top = '\u27D9';
	private final char top = 'T';
	private final char bottom = '\u27D8';
	private String toRemove = null;
	
	
		
	public OntologyPrintingVisitor(IRI iri) {
		this.iri=iri;
		try {
			this.out = new PrintStream(System.out, true, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		};
	}
	
	public void setToRemove(String toRemove) {
		this.toRemove = toRemove;
	}
	public void visit(OWLObjectSomeValuesFrom desc) {
		out.print(" " + exists + " ");
		System.out.print(removeIRIFromString(iri, desc.getProperty().toString()));
		desc.getProperty().accept(this);
		System.out.print(".");
	    desc.getFiller().accept(this);
		
	}
	
	public void visit(OWLClass c) {
		if(c.isBottomEntity()) {
			System.out.print(bottom + " ");
		}else if(c.isTopEntity()) {
			System.out.print(top + " ");
		}
		else {
			System.out.print(removeIRIFromString(iri,c.toString()) + " ");
		}
		return;
	}
	
	public void visit(OWLObjectComplementOf eq) {
		out.print(not);
		eq.getOperand().accept(this);
	}
	
	public void visit(OWLSubClassOfAxiom sub) {
		sub.getSubClass().accept(this);
		out.print(inclusion);
		sub.getSuperClass().accept(this);
		System.out.println("");
	}
	
	public void visit(OWLEquivalentClassesAxiom eq) {
		if(eq.getOperandsAsList().size() > 1) {
			eq.getOperandsAsList().get(0).accept(this); 
			System.out.print(" = ");
			eq.getOperandsAsList().get(1).accept(this); 
			System.out.println("");
		}
		else {
			eq.getOperandsAsList().get(0).accept(this); 
			System.out.println("");
		}
		return;
	}
	
	public void visit(OWLObjectIntersectionOf o) {
		List<OWLClassExpression> operands = o.getOperandsAsList(); 
		int i=operands.size()-1;
		System.out.print("(");
		for(OWLClassExpression ex: operands) {
			ex.accept(this);
			if(i > 0) {
				out.print(intersect + " " );
				i--;
			}
		}
		System.out.print(")");
		
	}
	
	public void visit(OWLObjectAllValuesFrom desc) {
		out.print(" " + foreach + " ");
		System.out.print(removeIRIFromString(iri, desc.getProperty().toString()));
		desc.getProperty().accept(this);
		System.out.print(".");
	    desc.getFiller().accept(this);
	    		
	}
	

	public void visit(OWLNamedIndividual i) {
		System.out.print(i.getIRI().getShortForm());
	}
	
	
	public void visit(OWLIndividual i) {
		for (OWLNamedIndividual ind: i.getIndividualsInSignature()) {
			ind.accept(this);
		}
		
	}
	
	public void visit(OWLClassAssertionAxiom o) {
		OWLIndividual individual = o.getIndividual();
		o.getClassExpression().accept(this);
		System.out.print("(");
		individual.accept(this);
		System.out.print(")");
	}
	
	public void visit(OWLObjectUnionOf o) {
		List<OWLClassExpression> operands = o.getOperandsAsList(); 
		int i=operands.size()-1;
		System.out.print("(");
		for(OWLClassExpression ex: operands) {
			ex.accept(this);
			if(i > 0) {
				out.print(union + " " );
				i--;
			}
		}
		System.out.print(")");
	}
	
	public void visit(OWLDisjointClassesAxiom ax) {
		for(OWLDisjointClassesAxiom axiom: ax.asPairwiseAxioms()) {
			axiom.getOperandsAsList().get(0).accept(this);
			System.out.print("Disjoint ");
			axiom.getOperandsAsList().get(1).accept(this);
			System.out.println("");
		}
	}
	
	public void visit(OWLObjectPropertyDomainAxiom ax) {
		System.out.print(removeIRIFromString(iri, ax.getProperty().toString()));
		ax.getProperty().accept(this);
		System.out.print(" Domain: ");
		ax.getDomain().accept(this);
		System.out.println("");
	}

	public void visit(OWLObjectPropertyRangeAxiom ax) {
		System.out.print(removeIRIFromString(iri, ax.getProperty().toString()));
		ax.getProperty().accept(this);
		System.out.print(" Range: ");
		ax.getRange().accept(this);
		System.out.println("");
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
		if(this.toRemove!=null)
			str = str.replace(this.toRemove, "");
		
		return str;
	}

	
		
}
