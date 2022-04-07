package turcobardi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.HasLogicalAxioms;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/*
 * 
 * This class is the ALC Reasoner and contains all the methods
 * for the reasoning
 */
public class ALCReasoner{
	
	private OWLOntology concept = null;
	private OntologyEditor editor = null;
	private EquivalenceRuleVisitor equivalence = null;

	public ALCReasoner(OWLOntology concept) {
		this.concept = concept;
		this.editor = new OntologyEditor(concept);
		this.equivalence = new EquivalenceRuleVisitor();
	}
	
	private List<OWLObject> existsRule(OWLObject abox) {
		ExistsRuleVisitor vis = new ExistsRuleVisitor();
		abox.accept(vis);
		return vis.getPropertyAndFiller();
	}
	
	private Set<OWLObject> unionRule(OWLObject abox) {
		
		Set<OWLObject> toAdd = new HashSet<>(); 
		UnionRuleVisitor vis = new UnionRuleVisitor();
		abox.accept(vis);
		Set<OWLClassAssertionAxiom> newAxioms = new HashSet<>();
		for(OWLClassExpression ex : vis.getOperands()) {
			try {
				newAxioms.add(editor.createIndividual(ex, "x0"));
				toAdd.addAll(newAxioms);
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			} catch (OWLOntologyStorageException e) {
				e.printStackTrace();
			}
		}

		return toAdd;
	}
	
	private Set<OWLObject> intersectionRule(Set<OWLObject> abox) {

		Set<OWLObject> toAdd = new HashSet<>(); 
		IntersectionRuleVisitor vis = new IntersectionRuleVisitor();
		for(OWLObject a: abox) {
			a.accept(vis);
			Set<OWLClassAssertionAxiom> newAxioms = new HashSet<>();
			for(OWLClassExpression ex : vis.getOperands()) {
				try {
					newAxioms.add(editor.createIndividual(ex, "x0"));
					toAdd.addAll(newAxioms);
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				} catch (OWLOntologyStorageException e) {
					e.printStackTrace();
				}
			}

    	}
		return toAdd;
	}
	private Set<OWLObject> instantiateMainConcept(Set<OWLObject> aBox){
		equivalence.visit(concept);
		OWLClassExpression tmp = equivalence.getOperands();
		try {
			OWLClassAssertionAxiom mainConcept = editor.createIndividual(tmp, "x0");
			aBox.add(mainConcept);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
		return aBox;
	}
	
	public boolean alcTableaux() {
		//int ind=0;
		Set<OWLObject> Lx = new HashSet<>();
		Set<OWLObject> aBox = new HashSet<>();
		OWLIndividual ind = null;
	
		//Instanziazione del concetto principale
		//Aggiunta degli altri assiomi
		
		//for (OWLLogicalAxiom obj :concept.getLogicalAxioms()) {
			equivalence.visit(concept);
			OWLClassExpression tmp = equivalence.getOperands();
			try {
				OWLClassAssertionAxiom mainConcept = editor.createIndividual(tmp, "x0");
				aBox.add(mainConcept);
				ind = mainConcept.getIndividual();
				Lx.add(equivalence.getOperands());
				
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			} catch (OWLOntologyStorageException e) {
				e.printStackTrace();
			}
			
		//}
		
		return implementTableaux(ind, Lx, aBox);	
	}
	
	private boolean hasClash(Set<OWLObject> abox) {
		for (OWLObject o: abox) {
			if (o instanceof OWLClassExpression) {
				if(((OWLClassExpression) o).isClassExpressionLiteral() && abox.contains(((OWLClassExpression) o).getObjectComplementOf())) {
					return true;
				}
			}
		}
		return false;
	}
	/* OLD
	public boolean alcTableaux() {
		int ind=0;
		Set<OWLObject> Lx = new HashSet<>();
		Set<OWLObject> aBox = new HashSet<>();
	
		//Instanziazione del concetto principale
		 aBox = instantiateMainConcept(aBox);
		
		//Aggiunta degli altri assiomi
		for (OWLObject obj :concept.getLogicalAxioms()) {
			
			aBox.add(((OWLLogicalAxiom) obj).getNNF());
		}
		
		return implementTableaux(ind, Lx, aBox);	
	}
	
	private boolean hasClash(Set<OWLObject> abox) {
		for (OWLObject o: abox) {
			if (o instanceof OWLClassExpression) {
				if(((OWLClassExpression) o).isClassExpressionLiteral() && abox.contains(((OWLClassExpression) o).getObjectComplementOf())) {
					return true;
				}
			}
		}
		return false;
	}*/
	
	private boolean implementTableaux(OWLIndividual ind, Set<OWLObject> Lx, Set<OWLObject> aBox) {
		OntologyPrintingVisitor printer = new OntologyPrintingVisitor(concept.getOntologyID().getOntologyIRI().get(), "");
		boolean ret = true;
		

		//REGOLA INTERSEZIONE
		Set<OWLObject> tmp = this.intersectionRule(aBox);
    	aBox.addAll(tmp);
    	for (OWLObject o: tmp) {
    		Lx.add(((OWLClassAssertionAxiom) o).getClassExpression());
    	}
    	System.out.println("\nAbox dopo regola intersec: " );
		for(OWLObject a: aBox){
    		a.accept(printer);
    		System.out.print(",");
    	}
    	//REGOLA UNIONE
    	for (OWLObject ax: Lx) {
    		Set<OWLObject> resURule = this.unionRule(ax);
    		if(resURule.size()>0) {
    			/*System.out.println("INSIEME DISGIUNTI:");
    			for(OWLObject a: resURule){
    	    		a.accept(printer);
    	    		System.out.print(",");
    	    	}*/
    			for (OWLObject o : resURule) {
            		if(!aBox.contains(o)) {
            			Set<OWLObject> tmpLx = new HashSet<>(Lx);
            			aBox.add(o);
            			//System.out.println("INSERISCO: ");
            			//o.accept(printer);
            			tmpLx.add(((OWLClassAssertionAxiom) o).getClassExpression());
            			
            			System.out.println("\nAbox dopo regola disg: ");
            			for(OWLObject a: aBox){
            	    		a.accept(printer);
            	    		System.out.print(",");
            	    	}	
            			ret = implementTableaux(ind, tmpLx, aBox);
            			System.out.println("Ret: "+ ret);
            			if (ret) {
            				break;
            			}
            			else {
            				aBox.remove(o);
            				System.out.println("RIMOZIONE: ");
            				o.accept(printer);
            				tmpLx.remove(((OWLClassAssertionAxiom) o).getClassExpression());
            			}
            		}
            		else {
            			break;
            		}
            	}
    			if (!ret) {
    				return false;
    			}
    			if(ret) {
    				break;
    			}
    		}
    		
    	}
    	
    	if(hasClash(Lx)) {
    		//aBox.removeAll(tmp);
    		/*for (OWLObject o: tmp) {
        		Lx.remove(((OWLClassAssertionAxiom) o).getClassExpression());
        	}*/
			return false;
		}
    	//Regola Esiste
    	for (OWLObject o: aBox) {
    		List<OWLObject> propertyAndFiller = this.existsRule(o);
    		if(propertyAndFiller.size()>0) {
    			OWLObjectPropertyExpression property = (OWLObjectPropertyExpression) propertyAndFiller.get(0);
    			OWLClassExpression filler = (OWLClassExpression) propertyAndFiller.get(1);
    			System.out.println(property+"."+filler);
    		}
    	}
		return true;
	}
	
}
