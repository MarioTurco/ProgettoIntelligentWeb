package turcobardi;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;
import static guru.nidi.graphviz.model.Factory.node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Size.Mode;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.model.Node;

public class RenderVisitor implements OWLObjectVisitor{
	private IRI iri;
	private String toRemove = null;
	private List<String> classNames = null;
	private List<Node> nodes;
	private final char intersect = '\u2293';
	private final char union = '\u2294';
	private final char foreach = '\u2200';
	private final char exists = '\u2203';
	private final char not = '\u00AC';
	private MutableNode main, second;
	public RenderVisitor(IRI iri, String toRemove) {
		this.iri = iri;
		this.toRemove = toRemove;
		classNames = new ArrayList<>();
		nodes = new ArrayList<>();
		main = mutNode("main").add(Label.html("<b>main</b><br/>start"), Color.AQUAMARINE);
		second = mutNode("second").add(Label.html("<b>second</b><br/>end"), Color.AQUAMARINE);
	}
	
	public void visit(OWLClass c) {
		classNames.add(conceptToString(iri,c.toString()) + " ");
		return;
	}
	public void visit(OWLObjectSomeValuesFrom desc) {
		desc.getProperty().accept(this);
		desc.getFiller().accept(this);
	}
	public void visit(OWLObjectComplementOf eq) {
		classNames.add(not + conceptToString(iri,eq.getOperand().toString()));
		//eq.getOperand().accept(this);
	}
	
	public void visit(OWLEquivalentClassesAxiom eq) {
		eq.getOperandsAsList().get(1).accept(this); //destra
		//eq.getOperandsAsList().get(0).accept(this); //sinistra
		return;
	}
	
	public void visit(OWLObjectIntersectionOf o) {
		List<OWLClassExpression> operands = o.getOperandsAsList(); 
		for(OWLClassExpression ex: operands) {
			ex.accept(this);
		}
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
	

	
	private String conceptToString(IRI iri, String str) {
		str = str.replace(iri.toString()+toRemove, "");
		str = str.replace("#", "");
		str = str.replace("<", "");
		str = str.replace(">", "");
		
		return str;
	}
	
	public void renderClasses() {
		MutableGraph g = mutGraph("example1").setDirected(true);
		for(String name : classNames) {
			g.add(mutNode(name).add((Color.RED)));
		}
		
		try {
			Node third = node("a").with(Label.html("<b>main</b><br/>start"), Color.rgb("1020d0").font());
			MutableNode e = main.addLink(second);
					//.with(Label.of("AOOO"));
			main.addTo(g);
			third.addTo(g);
			second.addTo(g);
			Graphviz.fromGraph(g).render(Format.SVG).toFile(new File("example/ex1m.svg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void accept(OWLOntology o) {
		// TODO Auto-generated method stub
		
	}
}
