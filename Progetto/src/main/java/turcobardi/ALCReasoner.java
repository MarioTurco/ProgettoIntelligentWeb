package turcobardi;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

/*
 * 
 * This class is the ALC Reasoner and contains all the methods
 * for the reasoning
 */
public class ALCReasoner {
	
	private OWLOntology concept = null;
	
	public ALCReasoner(OWLOntology concept) {
		this.concept=concept;
	}
	/* rule to resolve intersection */
	private void IntersectionRule() {
		return;
	}
	
	public void alcTableaux(int entity, Set<OWLObject> abox, Set<OWLObject> Lx) {
		
		
		
	}
	
}
