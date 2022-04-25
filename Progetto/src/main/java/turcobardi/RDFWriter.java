package turcobardi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class RDFWriter {
	File rdfFile = null;
	Model model = null;
	String IRI = null;
	List<Resource > res = null;

	public RDFWriter(String path, String fileName, String IRI) {
		this.rdfFile = new File(path.concat("\\").concat(fileName).concat(".rdf"));
		model = ModelFactory.createDefaultModel();
		model.createProperty(IRI.concat("label"));
		
		res = new ArrayList<>();
		this.IRI = IRI.concat("#");
	}
	
	//TODO
	public void addStatement(String subject, String predicate, String object) {
		Property p = model.createProperty(IRI.concat(predicate));
		Resource s = model.getResource(IRI.concat(subject));
		Resource o = model.getResource(IRI.concat(object));
		Statement statement = model.createStatement(s, p, o); 
		this.model = model.add(statement);
	}
	
	//TODO non serve
	public Resource editProperty(String URI, String propValue) {
		Resource editedRes = model.getResource(this.IRI.concat(URI));
		editedRes = editedRes.removeProperties();
		editedRes.addProperty(model.getProperty(IRI.concat("label")), propValue);
		return editedRes;
	}
	
	public Resource addResource(String URI,  String propValue) {
		System.out.println(this.IRI.concat(URI));
		Resource newRes = model.createResource(this.IRI.concat(URI));
		newRes.addProperty(model.getProperty(IRI.concat("label")), propValue);
		res.add(newRes);
		return newRes;
		
	}
	//TODO
	public Resource addResource(String URI) {
		Resource newRes = model.createResource(this.IRI.concat(URI));
		res.add(newRes);
		return newRes;
	}
	
	public void printModel() {
		model.write(System.out, "RDF/XML");
		//TODO bisogna pulire il modello 
		return;
	}
}
