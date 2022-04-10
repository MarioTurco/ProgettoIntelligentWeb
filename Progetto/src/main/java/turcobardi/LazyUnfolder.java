package turcobardi;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class LazyUnfolder {
	
	private OWLOntology kb = null;
	private Set<OWLObject> T_u = new HashSet<>();
	private Set<OWLObject> T_g = new HashSet<>();
	
	public LazyUnfolder(OWLOntology kb) {
		this.kb = kb;
	}
	
	public void doLazyUnfolding() {
		for(OWLLogicalAxiom axiom: this.kb.getLogicalAxioms()) {
			T_u.add(axiom);
			if(!this.isUnfoldable(T_u)) {
				T_u.remove(axiom);
				T_g.add(axiom);
			}
		}
	}
	
	private boolean isUnfoldable(Set<OWLObject> T_u) {
		//PRIMA CONDIZIONE
		for(OWLObject axiom: T_u) {
			if(axiom instanceof OWLEquivalentClassesAxiom) {
				OWLClassExpression leftSide = ((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(0);
				for(OWLObject axiom1: T_u) {
					if(axiom1 instanceof OWLSubClassOfAxiom && !axiom1.equals(axiom)) {
						if(((OWLSubClassOfAxiom) axiom1).getSubClass().equals(leftSide)) {
							return false;
						}
					}
					if(axiom1 instanceof OWLEquivalentClassesAxiom && !axiom1.equals(axiom)) {
						if(((OWLEquivalentClassesAxiom) axiom1).getOperandsAsList().get(0).equals(leftSide)) {
							return false;
						}
					}
				}
			}
			
		}
		//TODO SECONDA CONDIZIONE - GRAFO DELLE DIPENDENZE ACICLICO
		return true;
	}
	
	public Set<OWLObject> getT_u(){
		return T_u;
	}
	
	public Set<OWLObject> getT_g(){
		return T_g;
	}

}
