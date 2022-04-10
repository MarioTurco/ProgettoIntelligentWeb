package turcobardi;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
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
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;

public class OntologyPrintingVisitor implements OWLObjectVisitor{
	
	private IRI iri = null;
	private String toRemove = "";
	private PrintStream out = null;
	private final char intersect = '\u2293';
	private final char union = '\u2294';
	private final char foreach = '\u2200';
	private final char exists = '\u2203';
	private final char not = '\u00AC';
	private final char inclusion = '\u2291';
	
	public OntologyPrintingVisitor(IRI iri, String toRemove) {
		
		this.iri=iri;
		if(toRemove!=null) {
			this.toRemove=toRemove;
		}
		
		try {
			this.out = new PrintStream(System.out, true, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		};
	}
	
	
	public void visit(OWLObjectSomeValuesFrom desc) {
		out.print(" " + exists + " ");
		System.out.print(conceptToString(iri, desc.getProperty().toString()));
		desc.getProperty().accept(this);
		System.out.print(".");
	    desc.getFiller().accept(this);
		
	}
	
	public void visit(OWLClass c) {
		System.out.print(conceptToString(iri,c.toString()) + " ");
		return;
	}
	
	public void visit(OWLObjectComplementOf eq) {
		out.print(not);
		eq.getOperand().accept(this);
		//System.out.print(conceptToString(iri, eq.getOperand().toString()) + " ");
	}
	public void visit(OWLSubClassOfAxiom sub) {
		sub.getSubClass().accept(this);
		out.print(inclusion);
		sub.getSuperClass().accept(this);
		System.out.println("");
		
	}
	public void visit(OWLEquivalentClassesAxiom eq) {


		eq.getOperandsAsList().get(0).accept(this); //sinistra
		System.out.print(" = ");
		eq.getOperandsAsList().get(1).accept(this); //destra
		System.out.println("");
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
		System.out.print(conceptToString(iri, desc.getProperty().toString()));
		desc.getProperty().accept(this);
		System.out.print(".");
	    desc.getFiller().accept(this);
	    		
	}
	

	public void visit(OWLNamedIndividual i) {
		System.out.print(i.getIRI().getShortForm());
	}
	
	public void visit(OWLIndividual i) {
		for (OWLNamedIndividual ind: i.getIndividualsInSignature()) {
			i.accept(this);
		}
		
	}
	
	public void visit(OWLClassAssertionAxiom o) {
		OWLIndividual individual = o.getIndividual();
		//System.out.println(o);
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
	
    
    private String conceptToString(IRI iri, String str) {
		str = str.replace(iri.toString()+toRemove, "");
		str = str.replace("#", "");
		str = str.replace("<", "");
		str = str.replace(">", "");
		
		return str;
	}
		
}
