package turcobardi;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;



public class OntologyEditor {
	OWLOntology ontology = null;
	IRI iri = null;
	public OntologyEditor(OWLOntology ontology, IRI iri) {
		this.ontology = ontology;
		this.iri = iri;
	}
	
	public OWLClassAssertionAxiom createIndividual(OWLClass className, String indName) throws OWLOntologyCreationException,
	    OWLOntologyStorageException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ont = manager.createOntology();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLIndividual newInd = factory.getOWLNamedIndividual(IRI.create(iri + "#" + indName));
		OWLClassAssertionAxiom axiom= factory.getOWLClassAssertionAxiom(className, newInd);
		return axiom;

	}
	
	public void addIndividualsToClass(String className, String indName) throws Exception{
	    
	}
	
}
