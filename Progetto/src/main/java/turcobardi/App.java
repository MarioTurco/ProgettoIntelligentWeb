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

		File kbFile = new File("KB_13.owl");
		OWLOntology kb = manKb.loadOntologyFromOntologyDocument(kbFile);
		System.out.println("KB size:" + kb.getAxiomCount());
		IRI iriKb = kb.getOntologyID().getOntologyIRI().get();

		OntologyPrintingVisitor visitor = new OntologyPrintingVisitor(iriKb);
		Set<OWLLogicalAxiom> logicalAxiomsKb = kb.getLogicalAxioms(Imports.fromBoolean(false));
		System.out.println("KB has " + logicalAxiomsKb.size()+ " axioms");
		
		System.out.println("##########KB###########");
    	for(OWLLogicalAxiom logicalAxiom: logicalAxiomsKb){
    		logicalAxiom.accept(visitor); 
    	}
    	OWLOntology query = getQueryFromStdIn(kb);

    	Set<OWLLogicalAxiom> logicalAxiomsQuery = query.getLogicalAxioms(Imports.fromBoolean(false));

    	System.out.println("Query size: " + logicalAxiomsQuery.size());
    	
    	System.out.println("\n#########Query##########: ");
    	for(OWLLogicalAxiom logicalAxiom: logicalAxiomsQuery){
    		
    		logicalAxiom.accept(visitor); 
    	}
    	
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
    	System.out.println("\n################Empty TBox################");
    	executeAndPrintTime("empty", kb, query, true);
    	System.out.println("\n################Non-Empty TBox################");
    	executeAndPrintTime("nonEmpty", kb, query, true);
    	System.out.println("\n############LazyUnfolding#############");
    	executeAndPrintTime("lazy", kb, query, true);
    	
	}
	
	
	private static OWLOntology getQueryFromFile(String filename)  throws OWLOntologyCreationException {
		OWLOntologyManager manQ = OWLManager.createOWLOntologyManager();
		File queryFile = new File(filename);
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
	
	private static void executeAndPrintTime(String what, OWLOntology kb, OWLOntology query, boolean printGraph) {
		if (what.equals("empty")) {
			ALCReasoner reasoner = new ALCReasoner(query, null);
			Instant start = Instant.now();
	    	System.out.println("\nSAT: " + reasoner.alcTableauxNonEmpyTbox(false, printGraph));
	    	Instant end = Instant.now();
	    	System.out.println("\nElapsed Time: "+ Duration.between(start, end).toMillis()+"ms");
	    	if(printGraph) {	    		
	    		reasoner.renderTableauxGraph("graph/Empty");
	    		reasoner.printRDF("Empty", false);
	    	}
		}
		else if (what.equals("nonEmpty")) {
			if(kb.getLogicalAxiomCount()==0) {
				System.out.println("TBox vuota.");
				return;
			}
			ALCReasoner reasoner = new ALCReasoner(query, kb);
			Instant start = Instant.now();
	    	System.out.println("\nSAT: " + reasoner.alcTableauxNonEmpyTbox(false, printGraph));
	    	//TODO stampare true e false in blu e rosso 
	    	Instant end = Instant.now();
	    	System.out.println("\nElapsed Time: "+ Duration.between(start, end).toMillis()+"ms");
	    	if(printGraph) {
	    		reasoner.renderTableauxGraph("graph/nonEmpty");
		    	reasoner.printRDF("nonEmpty", false);
	    	}    	
		}
		else if (what.equals("lazy")) {
			ALCReasoner reasoner = new ALCReasoner(query, kb);
			Instant start = Instant.now();
	    	System.out.println("\nSAT: " + reasoner.alcTableauxNonEmpyTbox(true, printGraph));
	    	Instant end = Instant.now();
	    	System.out.println("\nElapsed Time: "+ Duration.between(start, end).toMillis()+"ms");
	    	if(printGraph) {
	    		reasoner.renderTableauxGraph("graph/lazy");
		    	reasoner.printRDF("lazy", false);
	    	} 	
		}
		
	}
	

}
