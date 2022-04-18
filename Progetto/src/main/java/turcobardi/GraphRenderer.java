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
	private int lastNodeID = 0; //identificativo univoco del nodo che serve per aggiungere gli archi
	private int lastIndividual = 0;
	private int lastParent = -1; //Identificativo dell'ultimo padre conosciuto -1 -> nessuno
	
	public void createNode(int id, String externalLabel, String internalLabel) {
		if(externalLabel==null) 
			externalLabel="";
		if(internalLabel==null) 
			internalLabel="";
		nodes.add(node(Integer.toString(id)).with(Label.html(externalLabel).external(), Label.html(internalLabel)));
		lastNodeID+=1;
		lastParent+=1;
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
	public int getLastNodeID() {
		return lastNodeID;
	}
	
	public int getLastIndividual() {
		return lastIndividual;
	}
	
	public MutableGraph createGraph() {
		for(Node n: nodes) {
			n.addTo(g);
		}
		return g;
	}
	
	public void renderGraph(String path) throws IOException {
		if(path==null)
			path = "example/tableaux2";
		
		Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(path+".png"));
		Graphviz.fromGraph(g).render(Format.SVG).toFile(new File(path+".svg"));
		System.out.println("Graph printed at '/" + path + "' in SVG and PNG formats");
	}


	public void createLink(int child, int parent, String label) {
		Node parentNode = nodes.get(parent);
		Node childNode = nodes.get(child);
	
		//this.g.addLink((parentNode).linkTo(childNode).with(Label.of(label)));
		(parentNode.link(to(childNode).with(Label.of(label)))).addTo(g);
	
	}
	
	
	//TODO cancellami servo solo per testare delle cose
	public void linkprova2() {
		
		Node parentNode = (node(Integer.toString(123213321)).with(Label.raw("0"), Label.of("AOO")));
		Node childNode = (node(Integer.toString(134543254)).with(Label.raw("1")));
		
		parentNode.asLinkSource().linkTo(childNode.asLinkTarget()).with(Label.of("AAA"));
		parentNode.link(to(childNode)).addTo(g);
		parentNode.addTo(g);
		childNode.addTo(g);
	}

	public void decrementLastIndividual() {
		lastIndividual--;
		
	}
}
