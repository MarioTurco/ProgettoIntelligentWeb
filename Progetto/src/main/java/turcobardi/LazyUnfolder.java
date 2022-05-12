package turcobardi;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
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
	private Set<OWLClass> leftSideDependencies = new HashSet<>();
	private Set<OWLClass> rightSideDependencies = new HashSet<>();
	
	public LazyUnfolder(OWLOntology kb) {
		this.kb = kb;
	}
	
	public void doLazyUnfolding() {
		LazyUnfoldingVisitor visitor = new LazyUnfoldingVisitor();
		for(OWLLogicalAxiom axiom: this.kb.getLogicalAxioms()) {
			if(axiom instanceof OWLEquivalentClassesAxiom || axiom instanceof OWLSubClassOfAxiom) {
				boolean inserted = false;
				if(axiom instanceof OWLEquivalentClassesAxiom) {
					if(((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(0).isTopEntity() || !((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(0).isClassExpressionLiteral()) {
						T_g.add(axiom);	
						inserted = true;
					}
				}else if(axiom instanceof OWLSubClassOfAxiom) {
					if(((OWLSubClassOfAxiom) axiom).getSubClass().isTopEntity() || !((OWLSubClassOfAxiom) axiom).getSubClass().isClassExpressionLiteral()) {
						T_g.add(axiom);	
						inserted = true;
					}
					

				}
				
				
				if(!inserted && this.isUnfoldable(T_u, axiom)) {
					T_u.add(axiom);
					//Controlla se è aciclico
					if(axiom instanceof OWLEquivalentClassesAxiom) {
						((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(0).accept(visitor);
						leftSideDependencies.addAll(visitor.getDependencies());
						((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(1).accept(visitor);
						rightSideDependencies.addAll(visitor.getDependencies());
					}else if(axiom instanceof OWLSubClassOfAxiom) {
						((OWLSubClassOfAxiom) axiom).getSubClass().accept(visitor);
						leftSideDependencies.addAll(visitor.getDependencies());
						((OWLSubClassOfAxiom) axiom).getSuperClass().accept(visitor);
						rightSideDependencies.addAll(visitor.getDependencies());	
					}

				}else if(!inserted && !this.isUnfoldable(T_u, axiom)) {
					T_g.add(axiom);		
				}
				inserted = false;
			}
		}
	}
	
	private boolean isUnfoldable(Set<OWLObject> T_u, OWLLogicalAxiom axiom) {
		
		
		//PRIMA CONDIZIONE
		if(axiom instanceof OWLEquivalentClassesAxiom) {
			
			LazyUnfoldingVisitor visitor = new LazyUnfoldingVisitor();
			OWLClassExpression leftSide = ((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(0);
			leftSide.accept(visitor);
			Set<OWLObject> tmp = new HashSet<>(visitor.getDependencies());
			((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(1).accept(visitor);
			tmp.retainAll(visitor.getDependencies());
			
			if(tmp.size()>0) {
				return false;
			}
			
			for(OWLObject axiom1: T_u) {					
				if(axiom1 instanceof OWLSubClassOfAxiom) {
					if(((OWLSubClassOfAxiom) axiom1).getSubClass().equals(leftSide)) {
						return false;
					}
				}
				
				if(axiom1 instanceof OWLEquivalentClassesAxiom) {
					if(((OWLEquivalentClassesAxiom) axiom1).getOperandsAsList().get(0).equals(leftSide)) {
						return false;
					}
				}
			}
		}
			
		if(axiom instanceof OWLSubClassOfAxiom) {
			
			LazyUnfoldingVisitor visitor = new LazyUnfoldingVisitor();
			OWLClassExpression leftSide = ((OWLSubClassOfAxiom) axiom).getSubClass();
			leftSide.accept(visitor);
			Set<OWLObject> tmp = new HashSet<>(visitor.getDependencies());
			((OWLSubClassOfAxiom) axiom).getSuperClass().accept(visitor);
			tmp.retainAll(visitor.getDependencies());
			
			if(tmp.size()>0) {
				return false;
			}
			
			
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
		//SECONDA CONDIZIONE - GRAFO DELLE DIPENDENZE ACICLICO
		if(!isAcyclic(axiom))
			return false;
		
		return true;
	}
	
	private boolean isAcyclic(OWLLogicalAxiom axiom) {
		LazyUnfoldingVisitor visitor = new LazyUnfoldingVisitor();
		boolean cond1 = true;
		boolean cond2 = true;
		//devo prendere dipendenze destra e sx di axiom e di T_U 
		// se le dip  sx di axiom sta nelle dx di T_u 
		// se la parte dx di axiom sta nelle sx di T_u
		if(axiom instanceof OWLEquivalentClassesAxiom){
			Set<OWLClass> tmp = new HashSet<>();
			((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(0).accept(visitor);
			tmp.addAll(visitor.getDependencies());
			tmp.retainAll(rightSideDependencies);
			if(tmp.size()>0) {
				cond1 = false;
			}
			tmp.clear();
			
			((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(1).accept(visitor);
			tmp.addAll(visitor.getDependencies());
			tmp.retainAll(leftSideDependencies);
			if(tmp.size()>0) {
				cond2 = false;
			}
			tmp.clear();
		}
		else if(axiom instanceof OWLSubClassOfAxiom) {
			Set<OWLClass> tmp = new HashSet<>();
			((OWLSubClassOfAxiom) axiom).getSuperClass().accept(visitor);
			tmp.addAll(visitor.getDependencies());
			tmp.retainAll(leftSideDependencies);
			if(tmp.size()>0) {
				cond1 = false;
			}
			tmp.clear();
			
			((OWLSubClassOfAxiom) axiom).getSubClass().accept(visitor);
			tmp.addAll(visitor.getDependencies());
			tmp.retainAll(rightSideDependencies);
			if(tmp.size()>0) {
				cond2 = false;
			}
			tmp.clear();
		}
		return cond1 || cond2;
	}
	
	public Set<OWLObject> getT_u(){
		return T_u;
	}
	
	public Set<OWLObject> getT_g(){
		return T_g;
	}

}
