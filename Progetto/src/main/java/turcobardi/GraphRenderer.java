package turcobardi;

import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.Node;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.mutGraph;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

public class GraphRenderer {
	private MutableGraph g = mutGraph("tableaux").setDirected(true);
	private Set<Node> nodes = new HashSet<>();
	
	public void createNode(String name, String label) {
		if(label==null) 
			label="";
		nodes.add(node(name).with(Label.raw(label)));
	}
	public MutableGraph createGraph() {
		for(Node n: nodes) {
			n.addTo(g);
		}
		return g;
	}
	
	public String renderGraph(String path) throws IOException {
		if(path==null)
			path = "example/tableaux";
		Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(path+".png"));
		Graphviz.fromGraph(g).render(Format.SVG).toFile(new File(path+".svg"));
		return "Graph printed at '" + path + "' in SVG and PNG formats";
	}
}
