package turcobardi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

/**
 * Classe che contiene il main 
 *
 */
public class App {

	
	public static void main(String[] args) throws OWLOntologyCreationException, UnsupportedEncodingException {
		OWLOntologyManager manKb = OWLManager.createOWLOntologyManager();
		//OWLOntologyManager manQ = OWLManager.createOWLOntologyManager();
		File kbFile = new File("kb5.owl");
		//File queryFile = new File("concept_2.owl");
		OWLOntology kb = manKb.loadOntologyFromOntologyDocument(kbFile);
		System.out.println("Numero assiomi :" + kb.getAxiomCount());
		IRI iriKb = kb.getOntologyID().getOntologyIRI().get();
		//System.out.println("KB: "+iriKb);
		//OWLOntology query = manQ.loadOntologyFromOntologyDocument(queryFile);
		//System.out.println("Numero assiomi :" + query.getAxiomCount());
		//IRI iriQuery = kb.getOntologyID().getOntologyIRI().get();
		//System.out.println("QUERY: " +iriQuery);
		/*
		//stampa il nome delle entità
    	o.signature().forEach(s -> System.out.println(s.toString().replace(iri.toString(), "")));
    	
    	}*/
		OntologyPrintingVisitor visitor = new OntologyPrintingVisitor(iriKb,"");
		Set<OWLLogicalAxiom> logicalAxiomsKb = kb.getLogicalAxioms(Imports.fromBoolean(false));
		System.out.println("KB size: " + logicalAxiomsKb.size());
		
		System.out.println("##########KB###########");
    	for(OWLLogicalAxiom logicalAxiom: logicalAxiomsKb){
    		logicalAxiom.accept(visitor); //prints the logical axiom
    	}
    	OWLOntology query = getQueryFromStdIn(kb);
    	System.out.println("QUERY CREATA");

    	Set<OWLLogicalAxiom> logicalAxiomsQuery = query.getLogicalAxioms(Imports.fromBoolean(false));

    	System.out.println("Query size: " + logicalAxiomsQuery.size());
    	
    	System.out.println("\n#########Query##########: ");
    	for(OWLLogicalAxiom logicalAxiom: logicalAxiomsQuery){
    		
    		logicalAxiom.accept(visitor); //prints the logical axiom
    	}
    	ALCReasoner reasoner = new ALCReasoner(query, kb);

    	LazyUnfolder lazy = new LazyUnfolder(kb);
    	lazy.doLazyUnfolding();
    	System.out.println("\n##########Tu#########");
    	for(OWLObject o: lazy.getT_u()) {
    		o.accept(visitor);
    	}
    	System.out.println("\n###########Tg###########");
    	for(OWLObject o: lazy.getT_g()) {
    		o.accept(visitor);
    	}
    	System.out.println("\n################Normal################");
    	executeAndPrintTime("nonEmpty", reasoner);
    	//System.out.println("\n############LazyUnfolding#############");
    	//executeAndPrintTime("lazy", reasoner);
    	
    	
    	
    	
	}
	
	private static OWLOntology getQueryFromFile()  throws OWLOntologyCreationException {
		OWLOntologyManager manQ = OWLManager.createOWLOntologyManager();
		File queryFile = new File("concept_2.owl");
		OWLOntology query = manQ.loadOntologyFromOntologyDocument(queryFile);
		return  query;
	}
	
	private static OWLOntology getQueryFromStdIn(OWLOntology kb) throws OWLOntologyCreationException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    	System.out.println("Enter query: ");
    	String queryInput = null;
    	try {
    		queryInput = new String(reader.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Set<OWLOntology> ont = new HashSet<>();
    	Set<OWLAxiom> toAdd = new HashSet<>();
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    	ont.add(kb);
    	ShortFormProvider sfp = new SimpleShortFormProvider();
    			/*new AnnotationValueShortFormProvider(Arrays.asList(new OntologyEditor(kb).getFactory().getRDFSLabel()),
    																Collections.<OWLAnnotationProperty, List<String>>emptyMap(), manager);*/

    	BidirectionalShortFormProvider shortFormProvider = new BidirectionalShortFormProviderAdapter(ont, sfp);
        ShortFormEntityChecker owlEntityChecker = new ShortFormEntityChecker(shortFormProvider);
    	        
        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        parser.setOWLEntityChecker(owlEntityChecker);
        parser.setStringToParse(queryInput);
        parser.setDefaultOntology(kb);
        toAdd.add(parser.parseAxiom());

    	OWLOntology queryIn = manager.createOntology(toAdd, kb.getOntologyID().getOntologyIRI().get());
    	return queryIn;
	}
	
	private static void executeAndPrintTime(String what, ALCReasoner reasoner) {
		if (what.equals("empty")) {
			Instant start = Instant.now();
	    	System.out.println("\nSAT: " + reasoner.alcTableaux());
	    	Instant end = Instant.now();
	    	System.out.println("\nElapsed Time: "+ Duration.between(start, end).toMillis()+"ms");
	    	//reasoner.renderTableauxGraph();
		}
		else if (what.equals("nonEmpty")) {
			Instant start = Instant.now();
			boolean ret = reasoner.alcTableauxNonEmpyTbox(false);
	    	System.out.println("\nSAT: " + ret);
	    	Instant end = Instant.now();
	    	System.out.println("\nElapsed Time: "+ Duration.between(start, end).toMillis()+"ms");
	    	reasoner.renderTableauxGraph("graph/nonEmpty");
	    	reasoner.printRDF("nonEmpty", false);
		}
		else if (what.equals("lazy")) {
			Instant start = Instant.now();
			boolean ret = reasoner.alcTableauxNonEmpyTbox(true);
	    	System.out.println("\nSAT: " + ret);
	    	Instant end = Instant.now();
	    	System.out.println("\nElapsed Time: "+ Duration.between(start, end).toMillis()+"ms");
	    	reasoner.renderTableauxGraph("graph/lazy");
	    	reasoner.printRDF("lazy", false);
		}
		
	}
	

}
