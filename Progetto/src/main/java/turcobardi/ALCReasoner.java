package turcobardi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
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

	private OWLObject forAllRule(OWLObject abox, OWLObjectPropertyAssertionAxiom prop ) {
		OWLObject toAdd = null; 
		ForAllRuleVisitor vis = new ForAllRuleVisitor();
		OWLNamedIndividual ind = null;
		abox.accept(vis);
		List<OWLObject> proAndFil = vis.getPropertyAndFiller();
		if (proAndFil.size()>0) {
			OWLObjectPropertyExpression property = (OWLObjectPropertyExpression) proAndFil.get(0);
			OWLClassExpression filler = (OWLClassExpression) proAndFil.get(1);
			
			if(prop.getProperty()==property) {
				ind = (OWLNamedIndividual) prop.getObject();
				try {
					if(ind!=null) {							
						toAdd = editor.createClassAssertionHavingIndividual(filler, ind);							
					}
				} catch (OWLOntologyCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
						
				}
			}
			
		}
		
		return toAdd;
	}
	
	private Set<OWLObject> existsRule(OWLObject abox, OWLNamedIndividual ind1 , String individual) {
		
		Set<OWLObject> toAdd = new HashSet<>(); 
		ExistsRuleVisitor vis = new ExistsRuleVisitor();
		abox.accept(vis);
		List<OWLObject> proAndFil = vis.getPropertyAndFiller();

		if (proAndFil.size()>0) {
			OWLObjectPropertyExpression property = (OWLObjectPropertyExpression) proAndFil.get(0);
			OWLClassExpression filler = (OWLClassExpression) proAndFil.get(1);
			try {
				toAdd.add(editor.createIndividualForProperty(property, ind1, individual));
				toAdd.add(editor.createIndividual(filler, individual));
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		return toAdd;
	}
	
	private Set<OWLObject> unionRule(OWLObject abox, String individual) {
		
		Set<OWLObject> toAdd = new HashSet<>(); 
		UnionRuleVisitor vis = new UnionRuleVisitor();
		abox.accept(vis);
		Set<OWLClassAssertionAxiom> newAxioms = new HashSet<>();
		for(OWLClassExpression ex : vis.getOperands()) {
			try {
				newAxioms.add(editor.createIndividual(ex, individual));
				toAdd.addAll(newAxioms);
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}
		}

		return toAdd;
	}
	
	private Set<OWLObject> intersectionRule(Set<OWLObject> abox, String individual) {

		Set<OWLObject> toAdd = new HashSet<>(); 
		IntersectionRuleVisitor vis = new IntersectionRuleVisitor();
		for(OWLObject a: abox) {
			a.accept(vis);
			Set<OWLClassAssertionAxiom> newAxioms = new HashSet<>();
			for(OWLClassExpression ex : vis.getOperands()) {
				try {
					newAxioms.add(editor.createIndividual(ex, individual));
					toAdd.addAll(newAxioms);
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
			}

    	}
		return toAdd;
	}
	/*private Set<OWLObject> instantiateMainConcept(Set<OWLObject> aBox){
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
	}*/
	
	public boolean alcTableaux() {
		//int ind=0;
		Set<OWLObject> Lx = new HashSet<>();
		Set<OWLObject> aBox = new HashSet<>();
		OWLNamedIndividual ind = null;
	
		//Instanziazione del concetto principale
		//Aggiunta degli altri assiomi
		
		//for (OWLLogicalAxiom obj :concept.getLogicalAxioms()) {
			equivalence.visit(concept);
			OWLClassExpression tmp = equivalence.getOperands();
			try {
				OWLClassAssertionAxiom mainConcept = editor.createIndividual(tmp, "x0");
				aBox.add(mainConcept);
				ind = (OWLNamedIndividual) mainConcept.getIndividual();
				Lx.add(equivalence.getOperands());
				
			} catch (OWLOntologyCreationException e) {
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
	
	private boolean implementTableaux(OWLNamedIndividual ind, Set<OWLObject> Lx, Set<OWLObject> aBox) {
		OntologyPrintingVisitor printer = new OntologyPrintingVisitor(concept.getOntologyID().getOntologyIRI().get(), "");
		boolean ret = true;
		

		//REGOLA INTERSEZIONE
		Set<OWLObject> tmp = this.intersectionRule(aBox,ind.getIRI().getShortForm());
    	aBox.addAll(tmp);
    	
    	for (OWLObject o: tmp) {
    		Lx.add(((OWLClassAssertionAxiom) o).getClassExpression());
    	}
    	//TODO controllo blocking
    	System.out.println("\nAbox dopo regola intersec: " );
		for(OWLObject a: aBox){
    		a.accept(printer);
    		System.out.print(",");
    	}
    	//REGOLA UNIONE
    	for (OWLObject ax: Lx) {
    		Set<OWLObject> resURule = this.unionRule(ax,ind.getIRI().getShortForm());
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
            			//System.out.println("Ret: "+ ret);
            			if (ret) {
            				break;
            			}
            			else {
            				aBox.remove(o);
            				//System.out.println("RIMOZIONE: ");
            				//o.accept(printer);
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
    		//System.out.println("HA CLASH");
    		//aBox.removeAll(tmp);
    		/*for (OWLObject o: tmp) {
        		Lx.remove(((OWLClassAssertionAxiom) o).getClassExpression());
        	}*/
			return false;
		}
    	
    	//Regola Esiste
    	int i = 1;
    	for (OWLObject o: Lx) {
    		String iri = ind.getIRI().getShortForm();
    		OWLObject toAddForAll = null;
    		Set<OWLObject> toAdd = this.existsRule(o,ind,"x"+Integer.parseInt(""+iri.charAt(iri.indexOf('x')+1))+i++);
    		if(toAdd.size()==0) {
    			i--;
    		}
    		else {
    			if(this.checkExistsRuleCondition(aBox, toAdd)) {
        			aBox.addAll(toAdd);
        		}
    			//Regola per ogni
    			OWLObjectPropertyAssertionAxiom propAxiom = this.getPropertyAssertionFromSet(toAdd);
    			for (OWLObject forAll: Lx) {
    				if(forAll instanceof OWLObjectAllValuesFrom) {
    					toAddForAll = this.forAllRule((OWLObjectAllValuesFrom) forAll, propAxiom);
            			if(!aBox.contains(toAddForAll)) {
            				aBox.add(toAddForAll);
            				Set<OWLObject> newLx = new HashSet<>();
            				newLx.add(((OWLObjectSomeValuesFrom) o).getFiller());
            				newLx.add(((OWLObjectAllValuesFrom) forAll).getFiller());
            				//System.out.println("NEWLX:" +newLx);
            				//Chiamata ricorsiva
            				ret = implementTableaux((OWLNamedIndividual)((OWLClassAssertionAxiom) toAddForAll).getIndividual(),newLx,aBox);
            				if (!ret) {
            					return false;
            				}
            			}
    				}
    				
    			}
    			
    		}
    		
    	}
		return true;
	}
	
	private OWLObjectPropertyAssertionAxiom getPropertyAssertionFromSet(Set<OWLObject> set) {
		OWLObjectPropertyAssertionAxiom propertyAxiom = null;
		for(OWLObject o:set) {
			if (o instanceof OWLObjectPropertyAssertionAxiom) {
				propertyAxiom = (OWLObjectPropertyAssertionAxiom) o;
			}
		}
		return propertyAxiom;
	}

	private boolean checkExistsRuleCondition(Set<OWLObject> abox, Set<OWLObject> toAdd) {
		OWLObjectPropertyAssertionAxiom propertyAxiom = null;
		OWLClassAssertionAxiom fillerAxiom = null;
		
		for(OWLObject o:abox) {
			if (o instanceof OWLObjectPropertyAssertionAxiom) {
				propertyAxiom = (OWLObjectPropertyAssertionAxiom) o;
			}
			if (o instanceof OWLClassAssertionAxiom) {
				fillerAxiom = (OWLClassAssertionAxiom) o;
			}
		}
		
		for(OWLObject o: abox) {
			if (o instanceof OWLObjectPropertyAssertionAxiom) {
				if (((OWLObjectPropertyAssertionAxiom) o).getProperty()==propertyAxiom.getProperty() && ((OWLObjectPropertyAssertionAxiom) o).getSubject()==propertyAxiom.getSubject()) {
					if(((OWLObjectPropertyAssertionAxiom) o).getObject()==fillerAxiom.getIndividual()) {
						for(OWLObject obj: abox) {
							if (obj instanceof OWLClassAssertionAxiom) {
								if(((OWLClassAssertionAxiom) obj).getClassExpression()==fillerAxiom.getClassExpression() && ((OWLClassAssertionAxiom) obj).getIndividual()==((OWLObjectPropertyAssertionAxiom) o).getObject()) {
									return false;
									
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
	
}
