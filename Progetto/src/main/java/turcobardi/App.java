package turcobardi;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;

public class App {

	
	public static void main(String[] args) throws OWLOntologyCreationException, UnsupportedEncodingException {
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		File file = new File("C:\\Users\\Mario\\Desktop\\concept.owl.xml");
		OWLOntology o = man.loadOntologyFromOntologyDocument(file);
		System.out.println("Assiomi :" + o.getAxiomCount());
		IRI iri = o.getOntologyID().getOntologyIRI().get();
		
		/*
		//stampa il nome delle entità
    	o.signature().forEach(s -> System.out.println(s.toString().replace(iri.toString(), "")));
    	
    	}*/
    	OWLObjectVisitor v = new OWLObjectVisitor() {
    		PrintStream out = new PrintStream(System.out, true, "UTF-8");
    		char intersect = '\u2293';
    		char union = '\u2294';
    		char foreach = '\u2200';
    		char exists = '\u2203';
    		char not = '\u2235';
    		@Override
    		public void visit(OWLObjectSomeValuesFrom desc) {
	    		out.print(exists + " ");
	    //		System.out.println(desc);
	    		System.out.print(conceptToString(iri, desc.getProperty().toString()));
	    		desc.getProperty().accept(this);
	    		System.out.print(".");
	    	    desc.getFiller().accept(this);
	    		
    		}
    		
    		@Override
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
    		
    		
    		
    	};
    	Set<OWLLogicalAxiom> aBox = o.getLogicalAxioms(Imports.fromBoolean(false));
    	System.out.println(aBox.size());
    	for(OWLLogicalAxiom a: aBox){
    		a.accept(v);
    	}
    	
    	
	}
	public static String conceptToString(IRI iri, String c) {
		c = (c.replace(iri.toString(), ""));
		c = (c.replace("#", ""));
		c = c.replace("<", "");
		c = (c.replace(">", ""));
		
		return c;
	}
		
		

}
