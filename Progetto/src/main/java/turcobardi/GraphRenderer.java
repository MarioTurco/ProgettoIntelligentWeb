package turcobardi;

import guru.nidi.graphviz.model.Link;
import static guru.nidi.graphviz.model.Factory.*;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

public class GraphRenderer {
	private MutableGraph g = mutGraph("tableaux").setDirected(true);
	private List<Node> nodes = new ArrayList<>();
	//TODO nextNodeID non serve più e può essere levato
	private int nextNodeID = 0; //identificativo univoco del nodo che serve per aggiungere gli archi
	private int lastParent = -2; //Identificativo dell'ultimo padre conosciuto 
	 
	/*
	public int createNode(int id, String externalLabel, String internalLabel) {
		if(externalLabel==null) 
			externalLabel="";
		if(internalLabel==null) 
			internalLabel="";
		nodes.add(node(Integer.toString(id)).with(Label.html(externalLabel).external(), Label.html(internalLabel)));
		nextNodeID+=1;
		lastParent+=1;
		return id;
	}*/
	
	public Node editNodeLabel(Node currentNode, String internal, String external) {
		Node editedNode = currentNode.with(Label.raw(external).external(), Label.raw(internal));
		return editedNode;
	}
	
	public Node createNode2(int id, String externalLabel, String internalLabel) {
		if(externalLabel==null) 
			externalLabel="";
		if(internalLabel==null) 
			internalLabel="";
		Node current =node(Integer.toString(id)).with(Label.raw(externalLabel).external(), Label.raw(internalLabel)); 
		nodes.add(current);
		nextNodeID+=1;
		lastParent+=1;
		return current;
	}
	
	public int getLastParent() {
		return lastParent;
	}
	public void setLastParent(int value) {
		lastParent=value;
	}
	public void decrementLastParent() {
		lastParent-=1;
	}
	public int getNextNodeID() {
		return nextNodeID;
	}

	public MutableGraph createGraph() {
		for(Node n: nodes) {
			n.addTo(g);
		}
		return g;
	}
	
	public void renderGraph(String path) throws IOException {
		if(path==null)
			path = "example/tableaux3";
		
		//Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(path+".png"));
		Graphviz.fromGraph(g).render(Format.SVG).toFile(new File(path+".svg"));
		System.out.println("Graph printed at '/" + path + "' in SVG and PNG formats");
	}


	public void createLink(int childID, int parentID, String label) {
		Node parentNode = nodes.get(parentID);
		Node childNode = nodes.get(childID);
		//this.g.addLink((parentNode).linkTo(childNode).with(Label.of(label)));
		(parentNode.link(to(childNode).with(Label.of(label)))).addTo(g);
	}
	
	public void createLink2(Node childNode, Node parentNode, String label) {
		//this.g.addLink((parentNode).linkTo(childNode).with(Label.of(label)));
		(parentNode.link(to(childNode).with(Label.of(label)))).addTo(g);
	}
	
}
