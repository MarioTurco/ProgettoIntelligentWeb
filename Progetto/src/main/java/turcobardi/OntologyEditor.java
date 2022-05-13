package turcobardi;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;



/**
 * Classe che serve a creare individui ed asserzioni
 *
 */
public class OntologyEditor {
	OWLOntology ontology = null;
	IRI iri = null;
	public OntologyEditor(OWLOntology ontology) {
		this.ontology = ontology;
		this.iri = ontology.getOntologyID().getOntologyIRI().get();
	}
	
	
	/** Crea un individuo e, data la classe <i>className C </i> crea l'assioma C(individuo)
	 * @param className - classe
	 * @param indName - individuo
	 * @return l'assioma corrispondente
	 * @throws OWLOntologyCreationException
	 */
	public OWLClassAssertionAxiom createIndividual(OWLClassExpression className, String indName) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLNamedIndividual newInd = factory.getOWLNamedIndividual(IRI.create(iri + "#" + indName));
		OWLClassAssertionAxiom axiom= factory.getOWLClassAssertionAxiom(className, newInd);
		return axiom;
	}
	
	/** Crea un assioma del tipo Proprietà(ind1,ind2)
	 * @param expr - proprietà 
	 * @param ind1 - primo individuo
	 * @param ind2 - secondo individuo
	 * @return l'assioma corrispondente
	 * @throws OWLOntologyCreationException
	 */
	public OWLObjectPropertyAssertionAxiom createIndividualForProperty(OWLObjectPropertyExpression expr, OWLNamedIndividual ind1, String ind2) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLNamedIndividual newInd = factory.getOWLNamedIndividual(IRI.create(iri + "#" + ind2));
		return factory.getOWLObjectPropertyAssertionAxiom(expr, ind1, newInd);
	}
	

	/** Crea un assioma del tipo C(individuo) con un individuo già esistente
	 * @param className - Classe 
	 * @param existingIndividual - l'individuo
	 * @return l'assioma corrispondente
	 * @throws OWLOntologyCreationException
	 */
	public OWLClassAssertionAxiom createClassAssertionWithExistingIndividual(OWLClassExpression className, OWLNamedIndividual existingIndividual) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLClassAssertionAxiom axiom= factory.getOWLClassAssertionAxiom(className, existingIndividual);
		return axiom;
	}
	
	
	/**
	 * @return owl:Things
	 */
	public OWLClass getTop() {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLClass top= factory.getOWLThing();
		return top;
	}
	
	public OWLDataFactory getFactory() {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		return manager.getOWLDataFactory();
	}
}
