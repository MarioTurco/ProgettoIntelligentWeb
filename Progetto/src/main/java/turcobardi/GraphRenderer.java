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

public class GraphRenderer {
	private MutableGraph g = mutGraph("tableaux").setDirected(true);
	private List<Node> nodes = new ArrayList<>();
	//TODO nextNodeID non serve più e può essere levato
	private int nextNodeID = 0; //identificativo univoco del nodo che serve per aggiungere gli archi
	private int lastParent = -2; //Identificativo dell'ultimo padre conosciuto 
	 
	
	public Node editNodeLabel(Node nodeToEdit, String internal, String external) {
		System.out.println("External: "+external);
		Node newNode = nodeToEdit.with(Label.html(external).external(), Label.markdown("x"+"__"+internal+"__"));
		nodes.remove(nodes.indexOf(nodeToEdit));
		nodes.add(newNode);
		return newNode;
	}
	public void printLabel(String label, String nodeName, String pathSubfolder){
		String path = pathSubfolder + "\\" + nodeName + ".txt";
		System.out.println(path);
		File file = new File(path);
		try {
			File folder = new File(pathSubfolder);
			folder.mkdir();
			file.createNewFile();
			FileWriter myWriter = new FileWriter(file);				
			myWriter.append(label);
			myWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	public Node createNode2(int id, String externalLabel, String internalLabel) {
		if(externalLabel==null) 
			externalLabel="";
		if(internalLabel==null) 
			internalLabel="";
		Node current = node(Integer.toString(id)).with(Label.html(externalLabel).external(), Label.markdown("x"+"__"+internalLabel+"__")); 
		nodes.add(current);
		nextNodeID+=1;
		lastParent+=1;
		return current;
	}
	public Node createNode2(int id, String internalLabel) {
		Node current = null;
		if(internalLabel=="CLASH")
			current = node(Integer.toString(id)).with(Label.markdown("CLASH")); 
		else if	(internalLabel=="CLASH-FREE")
			current = node(Integer.toString(id)).with(Label.markdown("CLASH-FREE")); 
		else
		current = createNode2(id, internalLabel, null);
		nodes.add(current);
		nextNodeID+=1;
		return current;
	}
	
	public int getLastParent() {
		return lastParent;
	}
	//TODO da cancellare
	public void setLastParent(int value) {
		lastParent=value;
	}
	//TODO da cancellare
	public void decrementLastParent() {
		lastParent-=1;
	}
	public int getNextNodeID() {
		return nextNodeID;
	}

	private MutableGraph createGraph() {
		for(Node n: nodes) {
			n.addTo(g);
		}
		return g;
	}
	
	public void renderGraph(String path) throws IOException {
		createGraph();
		if(path==null)
			path = "example/tableaux3";
		Graphviz.fromGraph(g).render(Format.SVG).toFile(new File(path+".svg"));
		System.out.println("Graph printed at '/" + path + "' in SVG format");
		//Pulisco il grafo e la lista dei nodi
		g = mutGraph("tableaux").setDirected(true);
		nodes.clear();
		return;
	}


	public void createLink(int childID, int parentID, String label) {
		Node parentNode = nodes.get(parentID);
		Node childNode = nodes.get(childID);
		(parentNode.link(to(childNode).with(Label.of(label)))).addTo(g);
	}
	
	public void createLink2(Node childNode, Node parentNode, String label, Color color) {
		(parentNode.link(to(childNode).with(Label.of(label), color))).addTo(g);
	}
	public void createLink2(Node childNode, Node parentNode, String label) {
		(parentNode.link(to(childNode).with(Label.of(label)))).addTo(g);
	}
	
}
