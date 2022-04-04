package turcobardi;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import javax.annotation.Nonnull;
import org.semanticweb.owlapi.model.IRI;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLObjectVisitor;

public class OntologyVisitor implements OWLObjectVisitor{
	
	private IRI iri = null;
	private PrintStream out = null;
	private final char intersect = '\u2293';
	private final char union = '\u2294';
	private final char foreach = '\u2200';
	private final char exists = '\u2203';
	private final char not = '\u2235';
	
	public OntologyVisitor(IRI iri) {
		this.iri=iri;
		try {
			this.out = new PrintStream(System.out, true, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		};
	}
	
	public void visit(OWLObjectSomeValuesFrom desc) {
		out.print(exists + " ");
		//System.out.println(desc);
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
		System.out.print(conceptToString(iri, eq.getOperand().toString()) + " ");
	}

	public void visit(OWLEquivalentClassesAxiom eq) {
		System.out.println(conceptToString(iri, eq.toString()) +" ");
		eq.getOperandsAsList().get(1).accept(this); //destra
		System.out.print(" = ");
		eq.getOperandsAsList().get(0).accept(this); //sinistra
		return;
	}
	
	public void visit(OWLObjectIntersectionOf o) {
		for(OWLClassExpression ex: o.getOperandsAsList()) {
			ex.accept(this);
		    out.print(intersect + " " );
		}
		System.out.print("T");
	}
	
    public void renderGraph(@Nonnull OWLOntology ontology) {
    	IRI iri = ontology.getOntologyID().getOntologyIRI().get();
    	ontology.signature().forEach(s -> System.out.println(s.toString().replace(iri.toString(), "")));
    	return;
    }
    
    private String conceptToString(IRI iri, String str) {
		str = str.replace(iri.toString(), "");
		str = str.replace("#", "");
		str = str.replace("<", "");
		str = str.replace(">", "");
		
		return str;
	}
		
}
