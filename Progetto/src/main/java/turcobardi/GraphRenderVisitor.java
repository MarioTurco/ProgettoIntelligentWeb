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
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class GraphRenderVisitor implements OWLObjectVisitor{
	
	private IRI iri = null;
	private String toRemove = "";
	private PrintStream out = null;
	private final char intersect = '\u2293';
	private final char union = '\u2294';
	private final char foreach = '\u2200';
	private final char exists = '\u2203';
	private final char not = '\u00AC';
	private final char inclusion = '\u2291';
	private String formula= "\n\n";
	
	
	
	public GraphRenderVisitor(IRI iri, String toRemove) {
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
	
	public void addCurrentLabel(String node) {
		formula = formula.concat("{" + node +"}\n");
	}
	
	public String getFormula() {
		String tmp = new String(formula);
		formula = "\n\n";
		return tmp;
	}
	
	public void visit(OWLObjectSomeValuesFrom desc) {
		formula=formula.concat("" + exists + "" + desc.getProperty().toString() );
		desc.getProperty().accept(this);
		formula=formula.concat(".");
	    desc.getFiller().accept(this);
		
	}
	
	public void visit(OWLClass c) {
		formula=formula.concat(conceptToString(iri,c.toString()) + " ");
		return;
	}
	
	public void visit(OWLObjectComplementOf eq) {
		formula=formula.concat(""+not);
		eq.getOperand().accept(this);
		//System.out.print(conceptToString(iri, eq.getOperand().toString()) + " ");
	}
	public void visit(OWLSubClassOfAxiom sub) {
		sub.getSubClass().accept(this);
		formula=formula.concat(inclusion+"");
		sub.getSuperClass().accept(this);
	}
	public void visit(OWLEquivalentClassesAxiom eq) {
		eq.getOperandsAsList().get(0).accept(this); //sinistra
		formula=formula.concat(" = ");
		eq.getOperandsAsList().get(1).accept(this); //destra
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
		formula=formula.concat(" " + foreach + " " + conceptToString(iri, desc.getProperty().toString()));
		desc.getProperty().accept(this);
		formula=formula.concat(".");
	    desc.getFiller().accept(this);
	    		
	}
	

	public void visit(OWLNamedIndividual i) {
		formula=formula.concat(i.getIRI().getShortForm());
	}
	
	//TODO cambiare?
	public void visit(OWLIndividual i) {
		for (OWLNamedIndividual ind: i.getIndividualsInSignature()) {
			i.accept(this);
		}
		
	}
	
	public void visit(OWLClassAssertionAxiom o) {
		OWLIndividual individual = o.getIndividual();
		//System.out.println(o);
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
	
    
    private String conceptToString(IRI iri, String str) {
		str = str.replace(iri.toString()+toRemove, "");
		str = str.replace("#", "");
		str = str.replace("<", "");
		str = str.replace(">", "");
		
		return str;
	}

	public void addSemicolon() {
		formula = formula.concat("; ");
		
	}

	public void addQuery() {
		formula = formula.concat("Query:");
		
	}
	
	/* TODO non serve
	public void addIndividual(String individual) {
		formula = formula.concat("[" + individual + "]");
		
	}*/
		
}
