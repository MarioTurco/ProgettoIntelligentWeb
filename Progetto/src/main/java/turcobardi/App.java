package turcobardi;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
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
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
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
		File file = new File("concept.owl.xml");

		OWLOntology concept = man.loadOntologyFromOntologyDocument(file);
		System.out.println("Numero assiomi :" + concept.getAxiomCount());
		IRI iri = concept.getOntologyID().getOntologyIRI().get();
		
		/*
		//stampa il nome delle entità
    	o.signature().forEach(s -> System.out.println(s.toString().replace(iri.toString(), "")));
    	
    	}*/
		OntologyPrintingVisitor visitor = new OntologyPrintingVisitor(iri,"");
    	Set<OWLLogicalAxiom> logicalAxioms = concept.getLogicalAxioms(Imports.fromBoolean(false));

    	System.out.println("Concept size: " + logicalAxioms.size());

    	for(OWLLogicalAxiom logicalAxiom: logicalAxioms){

    		logicalAxiom.accept(visitor); //prints the logical axiom
    	}
    	ALCReasoner reasoner = new ALCReasoner(concept);
    	executeAndPrintTime("empty", reasoner);
    	
	}
	
	private static void executeAndPrintTime(String what, ALCReasoner reasoner) {
		if (what.equals("empty")) {
			Instant start = Instant.now();
	    	System.out.println("\nSAT: " + reasoner.alcTableaux());
	    	Instant end = Instant.now();
	    	System.out.println("\nElapsed Time: "+ Duration.between(start, end).toMillis()+"ms");
		}
		else if (what.equals("nonEmpty")) {
			//TODO tbox non vuota
		}
		
	}
	

}
