package turcobardi;

import static guru.nidi.graphviz.model.Factory.*;

import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

/**
 * Classe che si occupa di creare il grafo corrispondete al tableaux in formato .SVG
 * @author Mario
 *
 */
public class GraphRenderer {
	private MutableGraph g = null;
	private List<Node> nodes = null;
	private int lastNodeID = 0; //identificativo univoco dell'ultimo nodo creato
	
	public GraphRenderer() {
		g = mutGraph("tableaux").setDirected(true);
		nodes = new ArrayList<>();
	}
	
	/**
	 * Modifica i label interni ed esterni di un nodo
	 * @param nodeToEdit - il nodo da modificare
	 * @param internal - il nuovo label interno
	 * @param external - il nuovo label esterno
	 * @return - il riferimento al nodo modificato
	 */
	public Node editNodeLabel(Node nodeToEdit, String internal, String external) {
		Node newNode = nodeToEdit.with(Label.html(external).external(), Label.markdown("x"+"__"+internal+"__"));
		nodes.remove(nodes.indexOf(nodeToEdit));
		nodes.add(newNode);
		return newNode;
	}
	
	/**
	 * Crea un file di nome 'fileName.txt' in posizione 'graph/pathSubfolder/' 
	 * contenente la stringa 'label'
	 *
	 * @param label - Stringa da scrivere sul file
	 * @param fileName - nome del file da creare
	 * @param pathSubfolder - path del file ('/graph/pathSubfolder/') 
	 */
	public void printLabelToFile(String label, String fileName, String pathSubfolder){
		String path = "graph\\" + pathSubfolder + "\\" + fileName + ".txt";
		File file = new File(path);
		try {
			File folder = new File("graph\\" + pathSubfolder);
			folder.mkdirs();
			file.createNewFile();
			FileWriter myWriter = new FileWriter(file);				
			myWriter.append(label);
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Crea un nodo con label interno ed esterno, aggiunge il nodo alla lista dei nodi creati 
	 * @param externalLabel - label esterno del nodo
	 * @param internalLabel - label interno del nodo
	 * @return il riferimento al nodo appena creato
	 */
	public Node createNode(String externalLabel, String internalLabel) {
		if(externalLabel==null) 
			externalLabel="";
		if(internalLabel==null) 
			internalLabel="";
		Node current = node(Integer.toString(getLastNodeID())).with(Label.html(externalLabel).external(), Label.markdown("x"+"__"+internalLabel+"__")); 
		nodes.add(current);
		lastNodeID+=1;
		return current;
	}
	
	/**
	 * Crea un nodo con label interno ed aggiunge il nodo alla lista dei nodi creati 
	 * @param internalLabel - label interno del nodo
	 * @return il riferimento al nodo appena creato
	 */
	public Node createNode(String internalLabel) {
		Node current = null;
		if(internalLabel=="CLASH")
			current = node(Integer.toString(getLastNodeID())).with(Label.markdown("CLASH")); 
		else if	(internalLabel=="CLASH-FREE")
			current = node(Integer.toString(getLastNodeID())).with(Label.markdown("CLASH-FREE")); 
		else if	(internalLabel=="BLOCKING")
			current = node(Integer.toString(getLastNodeID())).with(Label.markdown("BLOCKING")); 
		else
		current = createNode(null, internalLabel);
		nodes.add(current);
		lastNodeID+=1;
		return current;
	}
	
	/**
	 * @return l'id dell'ultimo nodo creato
	 */
	public int getLastNodeID() {
		return lastNodeID;
	}

	/**
	 * Crea un grafo con tutti i nodi ed i link creati tramite le apposite funzioni
	 * @return il grafo appena creato
	 */
	private MutableGraph createGraph() {
		for(Node n: nodes) {
			n.addTo(g);
		}
		return g;
	}
	
	/**
	 * Crea un file .SVG contenente il grafo. 
	 * Si noti che dopo la stampa, il grafo viene reinizializzato ed i nodi vengono cancellati.
	 * @param path - il path del file compreso del nome (ad. es 'folder/file')
	 * @throws IOException
	 */
	public void renderGraph(String path) throws IOException {
		createGraph();
		if(path==null)
			path = "example/tableaux";
		File f = new File(path+".svg");
		Graphviz.fromGraph(g).render(Format.SVG).toFile(f);
		System.out.println("Graph printed at " + f.getAbsolutePath());
		//Pulisco il grafo e la lista dei nodi
		g = mutGraph("tableaux").setDirected(true);
		nodes.clear();
		return;
	}
	
	/**
	 * Crea un link tra due nodi con label e con arco colorato
	 * @param childNode - nodo figlio
	 * @param parentNode - nodo padre
	 * @param label - label dell'arco
	 * @param color - colore dell'arco
	 */
	public void createLink2(Node childNode, Node parentNode, String label, Color color) {
		(parentNode.link(to(childNode).with(Label.of(label), color))).addTo(g);
	}
	/**
	 * Crea un link tra due nodi con label 
	 * @param childNode - nodo figlio
	 * @param parentNode - nodo padre
	 * @param label - label dell'arco
	 */
	public void createLink2(Node childNode, Node parentNode, String label) {
		(parentNode.link(to(childNode).with(Label.of(label)))).addTo(g);
	}
	
}
