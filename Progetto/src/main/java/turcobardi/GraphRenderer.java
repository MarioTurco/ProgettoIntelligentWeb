package turcobardi;

import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.Node;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.mutGraph;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

public class GraphRenderer {
	public Node createNode(String name, String label) {
		if(label==null) 
			label="";
		Node n = node(name).with(Label.raw(label));
		return n;
	}
	public MutableGraph createGraph(Set<Node> nodes) {
		MutableGraph g = mutGraph("tableaux").setDirected(true);
		for(Node n: nodes) {
			n.addTo(g);
		}
		return g;
	}
	
	public void renderGraph(MutableGraph g) throws IOException {
		Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("example/tableaux.png"));
		Graphviz.fromGraph(g).render(Format.SVG).toFile(new File("example/tableaux.svg"));
		return;
	}
}
