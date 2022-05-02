package turcobardi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.model.Node;

/*
 * 
 * This class is the ALC Reasoner and contains all the methods
 * for the reasoning
 */
/**
 * Reasoner ALC che implementa i metodi per il tableaux
 *
 */
/**
 * @author turco
 *
 */
public class ALCReasoner{
	private OWLOntology concept = null;
	private String lazyLabelsPath = null;
	private String normalLabelsPath = null;
	private int nClash=0;
	private final char union = '\u2294';
	private RDFWriter rdf= null;
	private final String printingPath1 = "<table color='green' scale='both' cellspacing='0' cellpadding='4' border='1'> <tr > <td title='Clicca per visualizzare Lx' target='_blank' href='";
	private final String printingPath2 = ".txt'> Label </td> </tr> </table>";
	private OntologyEditor editor = null;
	private EquivalenceRuleVisitor equivalence = null;
	private OWLOntology kb = null;
	private OntologyPrintingVisitor printer = null;
	private OWLSubClassOfAxiom KBinclusion = null;
	private OWLSubClassOfAxiom C_g = null;
	private GraphRenderer gr = null;
	private GraphRenderVisitor gv = null;
	private IRI iri = null;
	private int individual = 0;
	
	public ALCReasoner(OWLOntology concept, OWLOntology kb) {
		this.kb = kb;
		this.rdf = new RDFWriter("graph", concept.getOntologyID().getOntologyIRI().get().toString());
		this.gr = new GraphRenderer();
		this.concept = concept;
		if(kb!=null) {
			this.iri = kb.getOntologyID().getOntologyIRI().get();
			this.editor = new OntologyEditor(kb);
			this.printer = new OntologyPrintingVisitor(iri, "");
			this.gv = new GraphRenderVisitor(iri);		
		}else {
			this.iri = concept.getOntologyID().getOntologyIRI().get();
			this.editor = new OntologyEditor(concept);
			this.printer = new OntologyPrintingVisitor(iri, "");
			this.gv = new GraphRenderVisitor(iri);
		}
		this.equivalence = new EquivalenceRuleVisitor();
		this.lazyLabelsPath = new File("graph\\lazy").getAbsolutePath();
		this.normalLabelsPath = new File("graph\\normal").getAbsolutePath();
		if (kb!=null) {
			this.kb = this.preProcDisjointClassesAxioms(kb);
		}
	}
	
	private OWLSubClassOfAxiom convertT_gWithFactory(Set<OWLObject> T_g) {
		OWLDataFactory factory = this.editor.getFactory();
		List<OWLClassExpression> conjuncts = new ArrayList<>();
		for(OWLObject axiom: T_g) {
			if(axiom instanceof OWLSubClassOfAxiom) {

				List<OWLClassExpression> operands = new ArrayList<>();
				OWLObjectComplementOf compl = factory.getOWLObjectComplementOf(((OWLSubClassOfAxiom) axiom).getSubClass());
				operands.add(compl);
				operands.add(((OWLSubClassOfAxiom) axiom).getSuperClass());
				OWLObjectUnionOf union = factory.getOWLObjectUnionOf(operands);
				conjuncts.add(union);
			}
			if(axiom instanceof OWLEquivalentClassesAxiom) {
				
				List<OWLClassExpression> operands1 = new ArrayList<>();
				List<OWLClassExpression> operands2 = new ArrayList<>();
				OWLClassExpression left = ((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(0);
				OWLClassExpression right = ((OWLEquivalentClassesAxiom) axiom).getOperandsAsList().get(1);
				
				OWLObjectComplementOf compl1 = factory.getOWLObjectComplementOf(left);
				//System.out.println(compl1);
				//System.out.println(concept);
				operands1.add(compl1);
				operands1.add(right);
				OWLObjectUnionOf union1 = factory.getOWLObjectUnionOf(operands1);
				if(union1.getOperands().size()>1) {		
					conjuncts.add(union1);
				}
				else if (union1.getOperands().size()==1) {
					conjuncts.add(right);
				}

				OWLObjectComplementOf compl2 = factory.getOWLObjectComplementOf(right);
				operands2.add(compl2);
				operands2.add(left);
				OWLObjectUnionOf union2 = factory.getOWLObjectUnionOf(operands2);
				if(union2.getOperands().size()>1) {					
					conjuncts.add(union2);
				}
				else if (union2.getOperands().size()==1) {
					conjuncts.add(left);
				}
			}
			
		}
		OWLSubClassOfAxiom inclusionToAdd = null;
		if(conjuncts.size()>0) {
			OWLObjectIntersectionOf cHat = factory.getOWLObjectIntersectionOf(conjuncts);
			inclusionToAdd = factory.getOWLSubClassOfAxiom(editor.getTop(), cHat);
		}
		
		return inclusionToAdd;
	}
	
	private OWLSubClassOfAxiom convertKBWithFactory() {
		OWLDataFactory factory = this.editor.getFactory();
		List<OWLClassExpression> conjuncts = new ArrayList<>();
		for(OWLLogicalAxiom logicalAxiom: kb.getLogicalAxioms()) {
			if(logicalAxiom.getAxiomType().getName().equals("SubClassOf")) {
				List<OWLClassExpression> operands = new ArrayList<>();
				OWLObjectComplementOf compl = factory.getOWLObjectComplementOf(((OWLSubClassOfAxiom) logicalAxiom).getSubClass());
				operands.add(compl);
				operands.add(((OWLSubClassOfAxiom) logicalAxiom).getSuperClass());
				OWLObjectUnionOf union = factory.getOWLObjectUnionOf(operands);
				conjuncts.add(union);
			}
			if(logicalAxiom.getAxiomType().getName().equals("EquivalentClasses")) {
				List<OWLClassExpression> operands1 = new ArrayList<>();
				List<OWLClassExpression> operands2 = new ArrayList<>();
				OWLClassExpression left = ((OWLEquivalentClassesAxiom) logicalAxiom).getOperandsAsList().get(0);
				OWLClassExpression right = ((OWLEquivalentClassesAxiom) logicalAxiom).getOperandsAsList().get(1);
				
				OWLObjectComplementOf compl1 = factory.getOWLObjectComplementOf(left);
				operands1.add(compl1);
				operands1.add(right);
				OWLObjectUnionOf union1 = factory.getOWLObjectUnionOf(operands1);
				if(union1.getOperands().size()>1) {					
					conjuncts.add(union1);
				}
				else if (union1.getOperands().size()==1) {
					conjuncts.add(right);
				}

				OWLObjectComplementOf compl2 = factory.getOWLObjectComplementOf(right);
				operands2.add(compl2);
				operands2.add(left);
				OWLObjectUnionOf union2 = factory.getOWLObjectUnionOf(operands2);
				if(union2.getOperands().size()>1) {					
					conjuncts.add(union2);
				}
				else if (union2.getOperands().size()==1) {
					conjuncts.add(left);
				}
			}
			
		}
		OWLObjectIntersectionOf cHat = factory.getOWLObjectIntersectionOf(conjuncts);
		OWLSubClassOfAxiom inclusionToAdd = factory.getOWLSubClassOfAxiom(editor.getTop(), cHat);
		return inclusionToAdd;
	}
	
	//TODO in realtà abox è una regola forAll e va rinominata
	//TODO prop è l'esistenziale appena istanziato e va rinominato
	private OWLObject forAllRule(OWLObject abox, OWLObjectPropertyAssertionAxiom prop ) {
		OWLObject toAdd = null; 
		ForAllRuleVisitor vis = new ForAllRuleVisitor();
		OWLNamedIndividual ind = null;
		abox.accept(vis);
		List<OWLObject> proAndFil = vis.getPropertyAndFiller();
		if (proAndFil.size()>0) {
			OWLObjectPropertyExpression property = (OWLObjectPropertyExpression) proAndFil.get(0);
			OWLClassExpression filler = (OWLClassExpression) proAndFil.get(1);
			
			if(prop.getProperty().equals(property)) {
				//Data una relazione R(x,z) getObject() restituisce la z, getSubject() restituisce x
				ind = (OWLNamedIndividual) prop.getObject();
				try {
					if(ind!=null) {							
						toAdd = editor.createClassAssertionWithExistingIndividual(filler, ind);							
					}
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
						
				}
			}
			
		}
		
		return toAdd;
	}
	
	//TODO rinominare abox
	//TODO volendo si può rinominare toAdd in qualcosa che dica che è un assioma
	private Set<OWLObject> existsRule(OWLObject abox, OWLNamedIndividual ind1 , String newIndividualName) {
		
		Set<OWLObject> toAdd = new HashSet<>(); 
		ExistsRuleVisitor vis = new ExistsRuleVisitor();
		abox.accept(vis);
		List<OWLObject> proAndFil = vis.getPropertyAndFiller();

		if (proAndFil.size()>0) {
			OWLObjectPropertyExpression property = (OWLObjectPropertyExpression) proAndFil.get(0);
			OWLClassExpression filler = (OWLClassExpression) proAndFil.get(1);
			try {
				toAdd.add(editor.createIndividualForProperty(property, ind1, newIndividualName));
				toAdd.add(editor.createIndividual(filler, newIndividualName));
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		return toAdd;
	}
	
	
	private Set<OWLObject> existsRuleNonEmpyTbox(OWLObject abox, OWLNamedIndividual ind1 , String newIndividualName) {
		Set<OWLObject> toAdd = new HashSet<>(); 
		ExistsRuleVisitor vis = new ExistsRuleVisitor();
		abox.accept(vis);
		List<OWLObject> proAndFil = vis.getPropertyAndFiller();

		if (proAndFil.size()>0) {
			OWLObjectPropertyExpression property = (OWLObjectPropertyExpression) proAndFil.get(0);
			OWLClassExpression filler = (OWLClassExpression) proAndFil.get(1);
			try {
				toAdd.add(editor.createIndividualForProperty(property, ind1, newIndividualName));
				toAdd.add(editor.createIndividual(filler, newIndividualName));
				if (kb!=null) {
					toAdd.add(editor.createIndividual(this.KBinclusion.getSuperClass().getNNF(), newIndividualName));					
				}
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		return toAdd;
	}
	
	private Set<OWLObject> existsRuleNonEmpyTboxLazyUnfolding(OWLObject abox, OWLNamedIndividual ind1 , String newIndividualName) {
		
		Set<OWLObject> toAdd = new HashSet<>(); 
		ExistsRuleVisitor vis = new ExistsRuleVisitor();
		abox.accept(vis);
		List<OWLObject> proAndFil = vis.getPropertyAndFiller();

		if (proAndFil.size()>0) {
			OWLObjectPropertyExpression property = (OWLObjectPropertyExpression) proAndFil.get(0);
			OWLClassExpression filler = (OWLClassExpression) proAndFil.get(1);
			try {
				toAdd.add(editor.createIndividualForProperty(property, ind1, newIndividualName));
				toAdd.add(editor.createIndividual(filler, newIndividualName));
				if(this.C_g!=null) {
					toAdd.add(editor.createIndividual(this.C_g.getSuperClass().getNNF(), newIndividualName));
					
				}		
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}
		}
		

		return toAdd;
	}
	
	private Set<OWLObject> unionRule(OWLObject abox, String individual) {
		
		Set<OWLObject> toAdd = new HashSet<>(); 
		UnionRuleVisitor vis = new UnionRuleVisitor();
		abox.accept(vis);
		for(OWLClassExpression ex : vis.getOperands()) {
			try {
				toAdd.add(editor.createIndividual(ex, individual));
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
			for(OWLClassExpression ex : vis.getOperands()) {
				try {
					toAdd.add(editor.createIndividual(ex, individual));
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
			}

    	}
		return toAdd;
	}
	
	
	/** Stampa il grafo del tableaux su file svg
	 * @param path - path del file su cui stampare (ad es. "graph/lazy", senza estensione)
	 */
	public void renderTableauxGraph(String path) {
		try {
			gr.renderGraph(path);
		} catch (IOException e) {
			System.out.println("Impossibile stampare il grafo in: " + path);
		}
	}
	 
	
	/** Stampa il grafo RDF su file
	 * @param fileName - nome del file rdf su cui stampare (senza estensione) 
	 * @param printToConsole - se true, stampa il grafo anche sulla console
	 */
	public void printRDF(String fileName, boolean printToConsole) {
		 rdf.printAndClearModel(fileName, printToConsole);
	 }
	
	
	/** Modifica una knowledge base trasformando tutti gli assiomi di disgiunzione in assiomi di contenimento <br>
	 *  <code>(Disj(A,B) -> A SubClassOf not(B)</code>
	 * @param kb - Knowledge Base da processare
	 * @return kb modificata
	 * 		   
	 */
	private OWLOntology preProcDisjointClassesAxioms(OWLOntology kb) {
		OWLDataFactory factory = this.editor.getFactory();
		for (OWLLogicalAxiom axiom: kb.getLogicalAxioms()) {
			if(axiom instanceof OWLDisjointClassesAxiom)
				for (OWLDisjointClassesAxiom disjAx: ((OWLDisjointClassesAxiom) axiom).asPairwiseAxioms()) {
					OWLClassExpression left = disjAx.getOperandsAsList().get(0);
					OWLClassExpression right = disjAx.getOperandsAsList().get(1);
					OWLClassExpression complRight = factory.getOWLObjectComplementOf(right);
					OWLSubClassOfAxiom axiomToAdd = factory.getOWLSubClassOfAxiom(left, complRight);
					OWLOntologyManager manKb = OWLManager.createOWLOntologyManager();
					manKb.addAxiom(kb, axiomToAdd);
				}
		}
		return kb;
	}
	
	
	public boolean alcTableauxNonEmpyTbox(boolean useLazyUnfolding) {
		this.individual = 0;
		Set<OWLObject> Lx = new HashSet<>();
		Set<OWLObject> aBox = new HashSet<>();
		OWLNamedIndividual ind = null;
		if(!useLazyUnfolding) {
			//Instanziazione del concetto principale
			//Aggiunta degli altri assiomi
			for (OWLLogicalAxiom axiom :concept.getLogicalAxioms()) {
				axiom.getNNF().accept(equivalence);
				OWLClassExpression rightSide = equivalence.getRightSide();
				try {
					OWLClassAssertionAxiom mainConcept = editor.createIndividual(rightSide, "x0");
					//STO STAMPANDO
					//this.KBinclusion.getSuperClass().accept(printer);
					aBox.add(mainConcept);
					ind = (OWLNamedIndividual) mainConcept.getIndividual();
					Lx.add(rightSide);
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
			}
			
			if(kb!=null) {
				this.KBinclusion = this.convertKBWithFactory();
				OWLClassAssertionAxiom KBinclusionIstance;
				try {
					KBinclusionIstance = editor.createIndividual(this.KBinclusion.getSuperClass().getNNF(), "x0");
					aBox.add(KBinclusionIstance);
					//this.KBinclusion.getSuperClass().getNNF().accept(printer);
					Lx.add(this.KBinclusion.getSuperClass().getNNF());
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}			
			}
			for(OWLObject o: Lx) {
				o.accept(gv);
				gv.addColonToFormula();
			}
			//Creiamo il nodo principale
			String formula = gv.getAndClearFormula();
			Node current = gr.createNode(printingPath1+normalLabelsPath+"\\"+gr.getLastNodeID()+printingPath2, ind.getIRI().getShortForm().replace("x", "") );
			gr.printLabelToFile(formula,current.name().toString(),"normal");
			System.out.println("FORMULA: " + formula);
			rdf.addResource(current.name().toString(), formula);
			return implementTableauxNonEmptyTbox(ind, Lx, aBox, null, current);		
		}
		else if (useLazyUnfolding && kb!=null){
				
			for (OWLLogicalAxiom axiom :concept.getLogicalAxioms()) {
				
				axiom.getNNF().accept(equivalence);
				OWLClassExpression rightSide = equivalence.getRightSide();
				
				try {
					OWLClassAssertionAxiom mainConcept = editor.createIndividual(rightSide, "x0");
					aBox.add(mainConcept);
					ind = (OWLNamedIndividual) mainConcept.getIndividual();
					Lx.add(rightSide);
					
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
				
			
			}
			
			LazyUnfolder lazyUnfolder = new LazyUnfolder(kb);
			lazyUnfolder.doLazyUnfolding();
			Set<OWLObject> T_u = lazyUnfolder.getT_u();
			Set<OWLObject> T_g = lazyUnfolder.getT_g();
			this.C_g = this.convertT_gWithFactory(T_g);
			if(this.C_g!=null) {			
				OWLClassAssertionAxiom C_gInclusionIstance = null;
				
				try {
					C_gInclusionIstance = editor.createIndividual(this.C_g.getSuperClass().getNNF(), "x0");
					aBox.add(C_gInclusionIstance);
					Lx.add(this.C_g.getSuperClass().getNNF());
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
			}
			for(OWLObject o: Lx) {
				o.accept(gv);
				gv.addColonToFormula();
			}
			String formula = gv.getAndClearFormula();
			Node current = gr.createNode(printingPath1+lazyLabelsPath+"\\"+gr.getLastNodeID()+printingPath2, ind.getIRI().getShortForm().replace("x", "") );
			gr.printLabelToFile(formula,current.name().toString(),"lazy");
			rdf.addResource(current.name().toString(), formula);
			return implementTableauxNonEmptyTboxLazyUnfolding(ind, Lx, aBox, null, T_u, current);
		}else if(kb==null) {
			System.out.println("Impossibile usare il lazy unfolding su TBox vuota");
			//throw new IllegalArgumentException("Cannot use lazy unfolding on empty tbox");
		}
		
		return false;
	}
	
	
	/** Controlla se c'è un clash nell'etichetta Lx di un nodo
	 * @param Lx etichetta 
	 * @return <i>true</i> se c'è un clash, false altrimenti
	 */
	private boolean hasClash(Set<OWLObject> Lx) { 
		for (OWLObject o: Lx) {
			if (o instanceof OWLClassExpression) {
				if(((OWLClassExpression) o).isClassExpressionLiteral() && Lx.contains(((OWLClassExpression) o).getObjectComplementOf())) {
					return true;
				}
			}
			if(o.isBottomEntity())
				return true;
		}
		return false;
	}
	
	private boolean implementTableauxNonEmptyTbox(OWLNamedIndividual ind, Set<OWLObject> Lx, Set<OWLObject> aBox, Set<OWLObject> predLx, Node parent) {
		boolean ret = true;
		
		//REGOLA INTERSEZIONE
		Set<OWLObject> tmp = this.intersectionRule(Lx,ind.getIRI().getShortForm());
    	Set<OWLObject> inserted = new HashSet<>();
    	for(OWLObject ins: tmp) {
    		if(aBox.add(ins)) {
    			inserted.add(ins);
    		}
    	}
    	
    	for (OWLObject o: tmp) {
    		Lx.add(((OWLClassAssertionAxiom) o).getClassExpression());
    		
    	}
    	for (OWLObject o: Lx) {
    		o.accept(gv);
    		gv.addColonToFormula();
    	}
    	String formula = gv.getAndClearFormula();
    	parent = gr.editNodeLabel(parent, parent.name().toString().replace("x", ""), printingPath1 +normalLabelsPath+"\\"+parent.name().toString()+printingPath2 );
    	rdf.editLabelProperty(parent.name().toString().replace("x", ""), formula);
    	gr.printLabelToFile(formula, parent.name().toString().replace("x", ""), "normal");
    	if(hasClash(Lx)) {
    		Node current = gr.createNode("CLASH");
    		gr.createLink2(current, parent, "",Color.RED);
    		rdf.addResource("clashNode"+nClash);
    		rdf.addStatement(parent.name().toString().replace("x", ""), "clash", "clashNode"+nClash  );
    		nClash++;
    		aBox.removeAll(inserted);
    		for (OWLObject o: inserted) {
        		Lx.remove(((OWLClassAssertionAxiom) o).getClassExpression());
        	}
			return false;
		}
    	

    	//BLOCKING
    	if(kb!=null && predLx!=null) {
    		if(predLx.containsAll(Lx)) {
    			Node blocking = gr.createNode("BLOCKING");
    			gr.createLink2(blocking, parent, "", Color.ORANGE);
    			return true;  
    		}
    	}
   
    	boolean arePresentDisj = false;
    	for(OWLObject axiom: Lx) {
    		if(axiom instanceof OWLObjectUnionOf)
    			arePresentDisj = true;
    	}
    	//REGOLA UNIONE
    	if(arePresentDisj) {
    		
    		for (OWLObject axiom: Lx) {
    			//ax.accept(printer);
    			Set<OWLObject> resURule = this.unionRule(axiom,ind.getIRI().getShortForm());
    			if(resURule.size()>0) {
    				Set<OWLObject> intersec = new HashSet<>(aBox);
    				intersec.retainAll(resURule);
    				//mi salvo il padre comune a tutti i nodi disgiunti
    				if(intersec.size()==0) {
    					//int tmpParent = gr.getLastParent()+1;
    					for (OWLObject disjoint : resURule) {
    						Set<OWLObject> tmpLx = new HashSet<>(Lx);
    						aBox.add(disjoint);
    						tmpLx.add(((OWLClassAssertionAxiom) disjoint).getClassExpression());
    						//TODO grafo
    						for(OWLObject a: tmpLx){
                    	    	a.accept(gv);
                    	    	gv.addColonToFormula();
                    	    }
    						formula = gv.getAndClearFormula();
    						Node current = gr.createNode( printingPath1+normalLabelsPath+"\\"+gr.getLastNodeID()+printingPath2, ind.getIRI().getShortForm().replace("x",""));
    						gr.createLink2(current, parent, union+"");
    						gr.printLabelToFile(formula,current.name().toString(),"normal");
    						rdf.addResource(current.name().toString().replace("x", ""), formula);
    						rdf.addStatement(parent.name().toString().replace("x",""), "Union", current.name().toString().replace("x", ""));
    						ret = implementTableauxNonEmptyTbox(ind, tmpLx, aBox,null, current);
    						
    						if (ret) {
    							return true;
    						}
    						if(!ret){
    							aBox.remove(disjoint);
    							tmpLx.remove(((OWLClassAssertionAxiom) disjoint).getClassExpression());
    						}
    					}
    					if (!ret) { 
    						return false;	
    					}
    				}
    			}
    		}
    	}
    	if(hasClash(Lx)) {
    		Node current = gr.createNode("CLASH");
    		gr.createLink2(current, parent, "",Color.RED);
    		rdf.addResource("clashNode"+nClash);
    		rdf.addStatement(parent.name().toString().replace("x", ""), "clash", "clashNode"+nClash  );
    		nClash++;
    		aBox.removeAll(inserted);
    		for (OWLObject o: inserted) {
        		Lx.remove(((OWLClassAssertionAxiom) o).getClassExpression());
        	}
			return false;
		}
    	
    	//Regola Esiste
    	for (OWLObject o: Lx) {
    		Set<OWLObject> newLx = new HashSet<>();
    		OWLObject toAddForAll = null;
    		//Set<OWLObject> tmpLx = new HashSet<>(Lx);
    		//String newIndName = "x"+Integer.parseInt(""+iri.charAt(iri.indexOf('x')+1))+i++;
    		this.individual++;
    		String newIndName = "x"+ this.individual;
    		Set<OWLObject> toAddExists = this.existsRuleNonEmpyTbox(o,ind,newIndName);
    		
    		if(toAddExists.isEmpty()) {
    			this.individual--;
    		}
    		else {
    			if(this.checkExistsRuleCondition(aBox, toAddExists)) {
        			aBox.addAll(toAddExists);
        			
        			/*System.out.println("\nAbox dopo regola esiste: " );
    				for(OWLObject a: aBox){
    		    		a.accept(printer);
    		    		System.out.println(",");
    		    	}*/
        			String relationName = null;
        			for(OWLObject add: toAddExists) {
        				if (add instanceof OWLObjectPropertyAssertionAxiom) {
        					//tmpLx.add(((OWLObjectPropertyAssertionAxiom) add).getProperty());
        					if(kb!=null)
        						relationName=((OWLObjectPropertyAssertionAxiom) add).getProperty().toString().replace(kb.getOntologyID().getOntologyIRI().get(),"");
        					else
            					relationName=((OWLObjectPropertyAssertionAxiom) add).getProperty().toString().replace(concept.getOntologyID().getOntologyIRI().get(),"");
        				}
        				
        				if (kb!=null) {
        					if (add instanceof OWLClassAssertionAxiom) {
            					if(!((OWLClassAssertionAxiom) add).getClassExpression().equals(this.KBinclusion.getSuperClass())) {					
            						newLx.add(((OWLClassAssertionAxiom) add).getClassExpression());
            					}
            				}
            				if (add instanceof OWLClassAssertionAxiom) {
            					if(((OWLClassAssertionAxiom) add).getClassExpression().equals(this.KBinclusion.getSuperClass())) {					
            						newLx.add(((OWLClassAssertionAxiom) add).getClassExpression());
            					}
            				}
        				}  else {
        					if (add instanceof OWLClassAssertionAxiom) {
            					newLx.add(((OWLClassAssertionAxiom) add).getClassExpression());         				
            				}
        				}
        				
        				
        			}
        			for (OWLObject ax: newLx) {
        	    		ax.accept(gv);
        	    		gv.addColonToFormula();
        	    	}
        			formula = gv.getAndClearFormula();
        			relationName = relationName.replace("<", "");
        			relationName = relationName.replace(">", "");
        			relationName = relationName.replace("#", "");
        			
        			Node current = gr.createNode(printingPath1+normalLabelsPath+"\\"+gr.getLastNodeID()+printingPath2,  newIndName.replace("x",""));
        			gr.printLabelToFile(formula,current.name().toString(),"normal");
        			gr.createLink2(current, parent, relationName);
        			rdf.addResource(current.name().toString(), formula);
        			rdf.addStatement(parent.name().toString(), "Esiste_".concat(relationName), current.name().toString());
        			

        			//Regola per ogni
        			OWLObjectPropertyAssertionAxiom propAxiom = this.getPropertyAssertionFromSet(toAddExists);
        			for (OWLObject forAll: Lx) {
        				if(forAll instanceof OWLObjectAllValuesFrom) {
        					toAddForAll = this.forAllRule((OWLObjectAllValuesFrom) forAll, propAxiom);
        					if(!aBox.contains(toAddForAll) && toAddForAll!=null) {
        						
        						aBox.add(toAddForAll);
        						//newLx.add(((OWLObjectSomeValuesFrom) o).getFiller());
        						//newLx.add(((OWLObjectAllValuesFrom) forAll).getFiller());
        						newLx.add(((OWLClassAssertionAxiom) toAddForAll).getClassExpression());
        						for(OWLObject ax: newLx) {
        							ax.accept(gv);
        							gv.addColonToFormula();
        						}
        						formula = gv.getAndClearFormula();
        						current = gr.editNodeLabel(current, ind.getIRI().getShortForm().replace("x", ""), printingPath1+normalLabelsPath+"\\"+gr.getLastNodeID()+printingPath2);
        						gr.printLabelToFile(formula, current.name().toString(), "normal");
        						rdf.editLabelProperty(current.name().toString(), formula);
        				    	
        					}
        				}
        				
        			}
        			
        			ret = implementTableauxNonEmptyTbox((OWLNamedIndividual) propAxiom.getObject(),newLx,aBox, Lx, current);
        			
					if (!ret) {
						aBox.remove(toAddForAll); //Asserzioni perogni
						aBox.removeAll(toAddExists); //Asserzioni esistenziale
						return false;
					}
					else {
						continue;
						//return true;
						//break;
					}
        			
        		}
    			
    		}
    		
    	}
    	if(onlyClassAxioms(Lx)) {
    		Node clashFree = gr.createNode("CLASH-FREE");
    		gr.createLink2(clashFree, parent, "", Color.GREEN);
    		rdf.addResource("clash-Free");
    		rdf.addStatement(parent.name().toString(), "clash-free", "clash-Free"  );    	
    	}
    	return ret;
	}
	
	private Set<OWLObject> lazyUnfoldingRules(Set<OWLObject> aBox, Set<OWLObject> T_u, String individual) {
		Set<OWLObject> toAdd = new HashSet<>();
		for (OWLObject unfoldableAx: T_u) {
			if(unfoldableAx instanceof OWLEquivalentClassesAxiom) {
				OWLClassExpression leftSide = ((OWLEquivalentClassesAxiom) unfoldableAx).getOperandsAsList().get(0);
				for(OWLObject aboxAx: aBox) {
					//PRIMA REGOLA
					if(aboxAx instanceof OWLClassAssertionAxiom) {
						//System.out.println("\nAssioma da Abox");
						//aboxAx.accept(printer);
						if(((OWLClassAssertionAxiom) aboxAx).getClassExpression().equals(leftSide)) {
							OWLClassExpression rightSide = ((OWLEquivalentClassesAxiom) unfoldableAx).getOperandsAsList().get(1);
							try {
								toAdd.add(editor.createIndividual(rightSide, individual));
							} catch (OWLOntologyCreationException e) {
								e.printStackTrace();
							}
						}
						//SECONDA REGOLA
						OWLDataFactory factory = this.editor.getFactory();
						OWLObjectComplementOf complAx = factory.getOWLObjectComplementOf(((OWLClassAssertionAxiom) aboxAx).getClassExpression().getNNF());
						if(complAx.equals(leftSide)) {
							OWLClassExpression rightSide = ((OWLEquivalentClassesAxiom) unfoldableAx).getOperandsAsList().get(1);
							OWLObjectComplementOf rightSideCompl = factory.getOWLObjectComplementOf(rightSide);
							try {
								toAdd.add(editor.createIndividual(rightSideCompl, individual));
							} catch (OWLOntologyCreationException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			//TERZA REGOLA
			if(unfoldableAx instanceof OWLSubClassOfAxiom) {
				OWLClassExpression leftSide = ((OWLSubClassOfAxiom) unfoldableAx).getSubClass();
				for(OWLObject aboxAx: aBox) {
					if(aboxAx instanceof OWLClassAssertionAxiom) {
						if(((OWLClassAssertionAxiom) aboxAx).getClassExpression().equals(leftSide)) {
							OWLClassExpression rightSide = ((OWLSubClassOfAxiom) unfoldableAx).getSuperClass();
							try {
								toAdd.add(editor.createIndividual(rightSide, individual));
							} catch (OWLOntologyCreationException e) {
								e.printStackTrace();
							}
						}
					}
				}
				
			}
			
		}
		return toAdd;
	}
	
	private Set<OWLObject> lazyUnfoldingRules2(Set<OWLObject> aBox , Set<OWLObject> Lx, Set<OWLObject> T_u, String individual) {
		Set<OWLObject> insertedLazy = new HashSet<>();
		for (OWLObject unfoldableAx: T_u) {
			if(unfoldableAx instanceof OWLEquivalentClassesAxiom) {
				OWLClassExpression leftSide = ((OWLEquivalentClassesAxiom) unfoldableAx).getOperandsAsList().get(0);
				//PRIMA REGOLA
				if (Lx.contains(leftSide)) {
					OWLClassExpression rightSide = ((OWLEquivalentClassesAxiom) unfoldableAx).getOperandsAsList().get(1);
					Lx.add(rightSide);
					try {
						OWLClassAssertionAxiom ax = editor.createIndividual(rightSide, individual);
						aBox.add(ax);
						insertedLazy.add(ax);
					} catch (OWLOntologyCreationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//SECONDA REGOLA
				OWLDataFactory factory = this.editor.getFactory();
				OWLObjectComplementOf complLeftSide = factory.getOWLObjectComplementOf(leftSide.getNNF());
				if (Lx.contains(complLeftSide)) {
					OWLClassExpression rightSide = ((OWLEquivalentClassesAxiom) unfoldableAx).getOperandsAsList().get(1);
					OWLObjectComplementOf rightSideCompl = factory.getOWLObjectComplementOf(rightSide);
					Lx.add(rightSideCompl);
					try {
						OWLClassAssertionAxiom ax =editor.createIndividual(rightSideCompl, individual);
						aBox.add(ax);
						insertedLazy.add(ax);
					} catch (OWLOntologyCreationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
			//TERZA REGOLA
			if(unfoldableAx instanceof OWLSubClassOfAxiom) {
				OWLClassExpression leftSide = ((OWLSubClassOfAxiom) unfoldableAx).getSubClass();
				
				if(Lx.contains(leftSide)) {
					OWLClassExpression rightSide = ((OWLSubClassOfAxiom) unfoldableAx).getSuperClass();
					Lx.add(rightSide);
					try {
						OWLClassAssertionAxiom ax = editor.createIndividual(rightSide, individual);
						aBox.add(ax);
						insertedLazy.add(ax);
					} catch (OWLOntologyCreationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
			}
			
		}
		return insertedLazy;
	}
	
	private boolean implementTableauxNonEmptyTboxLazyUnfolding(OWLNamedIndividual ind, Set<OWLObject> Lx, Set<OWLObject> aBox, Set<OWLObject> predLx, Set<OWLObject> T_u, Node parent) {
		boolean ret = true;
		//Set<OWLObject> lazyUnfoldingRulesRes = lazyUnfoldingRules(aBox, T_u, ind.getIRI().getShortForm());
		Set<OWLObject> insertedLazyUnf = lazyUnfoldingRules2(aBox, Lx, T_u, ind.getIRI().getShortForm());
		//Set<OWLObject> insertedLazyUnf = new HashSet<>();

		/*for(OWLObject ins: lazyUnfoldingRulesRes) {
			if (aBox.add(ins)) {	
				insertedLazyUnf.add(ins);
			}
		}
		for(OWLObject ins: lazyUnfoldingRulesRes) {
			Lx.add(((OWLClassAssertionAxiom) ins).getClassExpression());
		}*/
		
		//REGOLA INTERSEZIONE
		Set<OWLObject> tmp = this.intersectionRule(Lx,ind.getIRI().getShortForm());
    	Set<OWLObject> inserted = new HashSet<>();
    	
    	for(OWLObject ins: tmp) {
    		if(aBox.add(ins)) {
    			inserted.add(ins);
    		}
    	}
    	
    	for (OWLObject o: tmp) {
    		Lx.add(((OWLClassAssertionAxiom) o).getClassExpression());
    	}
    	
    	insertedLazyUnf.addAll(lazyUnfoldingRules2(aBox, Lx, T_u, ind.getIRI().getShortForm()));
    	//lazyUnfoldingRulesRes = lazyUnfoldingRules(aBox, T_u, ind.getIRI().getShortForm());

    	/*for(OWLObject ins: lazyUnfoldingRulesRes) {
			if (aBox.add(ins)) {	
				insertedLazyUnf.add(ins);
			}
		}
		for(OWLObject ins: lazyUnfoldingRulesRes) {
			Lx.add(((OWLClassAssertionAxiom) ins).getClassExpression());
		}*/
    	for (OWLObject o: Lx) {
    		o.accept(gv);
    		gv.addColonToFormula();
    	}
    	//Edit the parent node
    	String formula = gv.getAndClearFormula();
    	parent = gr.editNodeLabel(parent, ind.getIRI().getShortForm().replace("x", ""),  printingPath1 +lazyLabelsPath+"\\"+parent.name().toString()+printingPath2 );
    	gr.printLabelToFile(formula, parent.name().toString(), "lazy");
    	rdf.editLabelProperty(parent.name().toString(), formula);
    	
    	if(hasClash(Lx)) {
    		Node clashNode = gr.createNode("CLASH");
    		gr.createLink2(clashNode, parent, "", Color.RED);
    		rdf.addResource("clashNode"+nClash);
    		rdf.addStatement(parent.name().toString(), "clash", "clashNode"+nClash  );
    		nClash++;
    		aBox.removeAll(inserted);
    		aBox.removeAll(insertedLazyUnf);
    		for (OWLObject o: inserted) {
        		Lx.remove(((OWLClassAssertionAxiom) o).getClassExpression());
        	}
    		
    		for (OWLObject o: insertedLazyUnf) {
        		Lx.remove(((OWLClassAssertionAxiom) o).getClassExpression());
        	}
			return false;
		}
    	
    	//BLOCKING
    	if(predLx!=null) {
    		if(predLx.containsAll(Lx)) {
    			//System.out.println("\nBLOCKING TRUE");
    			Node blocking = gr.createNode("BLOCKING");
    			gr.createLink2(blocking, parent, "", Color.ORANGE);
    			return true;  
    		}
    	}
    	
    	/*System.out.println("\nAbox dopo regola intersezione: " );
		for(OWLObject a: aBox){
    		a.accept(printer);
    		System.out.println(",");
    	}*/
    	boolean arePresentDisj = false;
    	for(OWLObject axiom: Lx) {
    		if(axiom instanceof OWLObjectUnionOf)
    			arePresentDisj = true;
    	}
    	//REGOLA UNIONE
    	if(arePresentDisj) {
    		
    		for (OWLObject axiom: Lx) {
    			//System.out.println("\nSPACCHETTO OR: ");
    			//ax.accept(printer);
    			Set<OWLObject> resURule = this.unionRule(axiom,ind.getIRI().getShortForm());
    			if(resURule.size()>0) {
    				/*System.out.println("\nINSIEME DISGIUNTI:");
    			for(OWLObject a: resURule){
    	    		a.accept(printer);
    	    		System.out.print(",");
    	    	}*/
    				Set<OWLObject> intersec = new HashSet<>(aBox);
    				intersec.retainAll(resURule);
    				if(intersec.size()==0) {
    					for (OWLObject disjoint : resURule) {
    						//	System.out.println("\nDISGIUNTO SCELTO: ");
    						//o.accept(printer);
    						
    						Set<OWLObject> tmpLx = new HashSet<>(Lx);
    						aBox.add(disjoint);
    						//System.out.println("INSERISCO: ");
    						//o.accept(printer);
    						tmpLx.add(((OWLClassAssertionAxiom) disjoint).getClassExpression());
    						
    						for(OWLObject a: tmpLx){
                    	    	a.accept(gv);
                    	    	gv.addColonToFormula();
                    	    }
    						formula = gv.getAndClearFormula();
    						Node currentNode = gr.createNode( printingPath1 +lazyLabelsPath+"\\"+gr.getLastNodeID()+printingPath2, ind.getIRI().getShortForm().replace("x", ""));
    						gr.createLink2(currentNode, parent, union+"");
    						gr.printLabelToFile(formula, currentNode.name().toString(), "lazy");
    						rdf.addResource(currentNode.name().toString(), formula);
    						rdf.addStatement(parent.name().toString(), "Union", currentNode.name().toString());
    						ret = implementTableauxNonEmptyTboxLazyUnfolding(ind, tmpLx, aBox,null, T_u, currentNode);
    						
    						if (ret) 
    							return true;
    						
    						if(!ret){
    							aBox.remove(disjoint);
    							tmpLx.remove(((OWLClassAssertionAxiom) disjoint).getClassExpression());
    						}
    					}
    					if (!ret) 
    						return false;	
    				}
    			}
    		}
    	}
    	if(hasClash(Lx)) {
    		Node clashNode = gr.createNode( "CLASH");
    		gr.createLink2(clashNode, parent, "", Color.RED);
    		rdf.addResource("clashNode"+nClash);
    		rdf.addStatement(parent.name().toString(), "clash", "clashNode"+nClash  );
    		nClash++;
    		aBox.removeAll(inserted);
    		aBox.removeAll(insertedLazyUnf);
    		for (OWLObject o: inserted) {
        		Lx.remove(((OWLClassAssertionAxiom) o).getClassExpression());
        	}
    		
    		for (OWLObject o: insertedLazyUnf) {
        		Lx.remove(((OWLClassAssertionAxiom) o).getClassExpression());
        	}
			return false;
		}
    	
    	//Regola Esiste
    	for (OWLObject o: Lx) {
    		OWLObject toAddForAll = null;
    		Set<OWLObject> newLx = new HashSet<>();
    		//Set<OWLObject> tmpLx = new HashSet<>(Lx);
    		//String newIndName = "x"+Integer.parseInt(""+iri.charAt(iri.indexOf('x')+1))+i++;
    		this.individual++;
    		String newIndName = "x"+ this.individual;
    		Set<OWLObject> toAdd = this.existsRuleNonEmpyTboxLazyUnfolding(o,ind,newIndName);
    		if(toAdd.size()==0) {
    			this.individual--;
    		}
    		else {
    			if(this.checkExistsRuleCondition(aBox, toAdd)) {
        			aBox.addAll(toAdd);
        			
        			/*System.out.println("\nAbox dopo regola esiste: " );
    				for(OWLObject a: aBox){
    		    		a.accept(printer);
    		    		System.out.println(",");
    		    	}*/
        			String relationName = null;
        			for(OWLObject add: toAdd) {
        				if (add instanceof OWLObjectPropertyAssertionAxiom) {
        					//tmpLx.add(((OWLObjectPropertyAssertionAxiom) add).getProperty());
        					if(kb!=null)
        						relationName=((OWLObjectPropertyAssertionAxiom) add).getProperty().toString().replace(kb.getOntologyID().getOntologyIRI().get(),"");
        					else
            					relationName=((OWLObjectPropertyAssertionAxiom) add).getProperty().toString().replace(concept.getOntologyID().getOntologyIRI().get(),"");
        					
        				}
        				if (add instanceof OWLClassAssertionAxiom) {
        					if(this.C_g!=null) {
        						if(!((OWLClassAssertionAxiom) add).getClassExpression().equals(this.C_g.getSuperClass().getNNF())) {
        							newLx.add(((OWLClassAssertionAxiom) add).getClassExpression());
        						}
        					}
        					else {
        							newLx.add(((OWLClassAssertionAxiom) add).getClassExpression());
        					}
        				}
        				if (add instanceof OWLClassAssertionAxiom) {
        					if(this.C_g!=null) {
        						if(((OWLClassAssertionAxiom) add).getClassExpression().equals(this.C_g.getSuperClass().getNNF())) {
        							newLx.add(((OWLClassAssertionAxiom) add).getClassExpression());
        						}
        					}
        				}
        				
        			}
        			for (OWLObject ax: newLx) {
        	    		ax.accept(gv);
        	    		gv.addColonToFormula();
        	    	}
        			relationName = relationName.replace("<", "");
        			relationName = relationName.replace(">", "");
        			relationName = relationName.replace("#", "");
        			formula = gv.getAndClearFormula();
        			Node currentNode = gr.createNode(printingPath1+ lazyLabelsPath+"\\"+gr.getLastNodeID()+printingPath2,  newIndName.replace("x", ""));

        			gr.printLabelToFile(formula, currentNode.name().toString(), "lazy");
        			gr.createLink2(currentNode, parent, relationName);
        			rdf.addResource(currentNode.name().toString(), formula);
        			rdf.addStatement(parent.name().toString(), "Esiste_".concat(relationName), currentNode.name().toString());
        			//Regola per ogni
        			OWLObjectPropertyAssertionAxiom propAxiom = this.getPropertyAssertionFromSet(toAdd);
        			for (OWLObject forAll: Lx) {
        				if(forAll instanceof OWLObjectAllValuesFrom) {
        					toAddForAll = this.forAllRule((OWLObjectAllValuesFrom) forAll, propAxiom);
        					if(!aBox.contains(toAddForAll) && toAddForAll!=null) {
        						aBox.add(toAddForAll);
        						//newLx.add(((OWLObjectSomeValuesFrom) o).getFiller());
        						//newLx.add(((OWLObjectAllValuesFrom) forAll).getFiller());
        						newLx.add(((OWLClassAssertionAxiom) toAddForAll).getClassExpression());
        						for(OWLObject ax: newLx) {
        							ax.accept(gv);
        							gv.addColonToFormula();
        						}
        						formula = gv.getAndClearFormula();
        						currentNode = gr.editNodeLabel(currentNode, ind.getIRI().getShortForm().replace("x",""), printingPath1 +lazyLabelsPath+"\\"+currentNode.name().toString()+printingPath2);
        						gr.printLabelToFile(formula, currentNode.name().toString(), "lazy");
        						rdf.editLabelProperty(currentNode.name().toString(), formula);
        					}
        				}
        				
        			}
        			
        			ret = implementTableauxNonEmptyTboxLazyUnfolding((OWLNamedIndividual) propAxiom.getObject(),newLx,aBox, Lx, T_u, currentNode);
					if (!ret) {
						aBox.remove(toAddForAll); //Asserzioni perogni
						aBox.removeAll(toAdd); //Asserzioni esistenziale

						return false;
					}
					else {
						continue;
						//return true;
						//break;
					}	
        			
        		}
    			
    		}
    		
    	}
    	if(onlyClassAxioms(Lx)) {
    		Node clashFree = gr.createNode("CLASH-FREE");
    		gr.createLink2(clashFree, parent, "", Color.GREEN);
    		rdf.addResource("clash-Free");
    		rdf.addStatement(parent.name().toString(), "clash-free", "clash-Free"  );    	
    	}
    	return ret;
	}
	
	
	/** Data una Lx controlla se contiene solo classi (non ci sono altre regole da applicare)
	 * @param Lx
	 * @return true se non ci sono altre regole da applicare
	 */
	private boolean onlyClassAxioms( Set<OWLObject> Lx) {
		for (OWLObject obj: Lx) {
    		if(!(obj instanceof OWLClass)) {
    			return false;
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
		
		for(OWLObject axiomToAdd: toAdd) {
			if (axiomToAdd instanceof OWLObjectPropertyAssertionAxiom) {
				propertyAxiom = (OWLObjectPropertyAssertionAxiom) axiomToAdd;
			}
			if (axiomToAdd instanceof OWLClassAssertionAxiom) {
				if(this.KBinclusion!=null) {
					if(!((OWLClassAssertionAxiom) axiomToAdd).getClassExpression().equals(this.KBinclusion.getSuperClass().getNNF())) {
						fillerAxiom = (OWLClassAssertionAxiom) axiomToAdd;
					}
				}
				else if(this.C_g!=null) {
					if(!((OWLClassAssertionAxiom) axiomToAdd).getClassExpression().equals(this.C_g.getSuperClass().getNNF())) {
						fillerAxiom = (OWLClassAssertionAxiom) axiomToAdd;
					}
				}
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
