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
import org.semanticweb.owlapi.model.OWLObject;
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
		OWLOntologyManager manKb = OWLManager.createOWLOntologyManager();
		OWLOntologyManager manQ = OWLManager.createOWLOntologyManager();
		File kbFile = new File("kb7.owl");
		File queryFile = new File("prova2.owl");
		OWLOntology kb = manKb.loadOntologyFromOntologyDocument(kbFile);
		System.out.println("Numero assiomi :" + kb.getAxiomCount());
		IRI iriKb = kb.getOntologyID().getOntologyIRI().get();
		OWLOntology query = manQ.loadOntologyFromOntologyDocument(queryFile);
		System.out.println("Numero assiomi :" + query.getAxiomCount());
		IRI iriQuery = kb.getOntologyID().getOntologyIRI().get();
		/*
		//stampa il nome delle entità
    	o.signature().forEach(s -> System.out.println(s.toString().replace(iri.toString(), "")));
    	
    	}*/
		OntologyPrintingVisitor visitor = new OntologyPrintingVisitor(iriKb,"");
		Set<OWLLogicalAxiom> logicalAxiomsKb = kb.getLogicalAxioms(Imports.fromBoolean(false));
    	Set<OWLLogicalAxiom> logicalAxiomsQuery = query.getLogicalAxioms(Imports.fromBoolean(false));

    	System.out.println("KB size: " + logicalAxiomsKb.size());
    	System.out.println("Query size: " + logicalAxiomsQuery.size());
    	System.out.println("KB: ");
    	for(OWLLogicalAxiom logicalAxiom: logicalAxiomsKb){
    		
    		logicalAxiom.accept(visitor); //prints the logical axiom
    	}
    	System.out.println("Query: ");
    	for(OWLLogicalAxiom logicalAxiom: logicalAxiomsQuery){
    		
    		logicalAxiom.accept(visitor); //prints the logical axiom
    	}
    	ALCReasoner reasoner = new ALCReasoner(query, kb);
    	/*LazyUnfolder lazy = new LazyUnfolder(kb);
    	lazy.doLazyUnfolding();
    	System.out.println("TU");
    	for(OWLObject o: lazy.getT_u()) {
    		o.accept(visitor);
    	}
    	System.out.println("Tg");
    	for(OWLObject o: lazy.getT_g()) {
    		o.accept(visitor);
    	}*/
    	System.out.println("\n################Normal################");
    	executeAndPrintTime("nonEmpty", reasoner);
    	System.out.println("\n############LazyUnfolding#############");
    	executeAndPrintTime("lazy", reasoner);
    	/*ALCReasoner reasoner2 = new ALCReasoner(concept, concept);
    	System.out.println("\nKB convertita: ");
    	reasoner2.convertKB().accept(visitor);*/
    	
    	
    	
	}
	
	private static void executeAndPrintTime(String what, ALCReasoner reasoner) {
		if (what.equals("empty")) {
			Instant start = Instant.now();
	    	System.out.println("\nSAT: " + reasoner.alcTableaux());
	    	Instant end = Instant.now();
	    	System.out.println("\nElapsed Time: "+ Duration.between(start, end).toMillis()+"ms");
		}
		else if (what.equals("nonEmpty")) {
			Instant start = Instant.now();
			boolean ret = reasoner.alcTableauxNonEmpyTbox(false);
	    	System.out.println("\nSAT: " + ret);
	    	Instant end = Instant.now();
	    	System.out.println("\nElapsed Time: "+ Duration.between(start, end).toMillis()+"ms");
		}
		else if (what.equals("lazy")) {
			Instant start = Instant.now();
			boolean ret = reasoner.alcTableauxNonEmpyTbox(true);
	    	System.out.println("\nSAT: " + ret);
	    	Instant end = Instant.now();
	    	System.out.println("\nElapsed Time: "+ Duration.between(start, end).toMillis()+"ms");
		}
		
	}
	

}
