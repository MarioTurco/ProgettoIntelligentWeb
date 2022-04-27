package turcobardi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class RDFWriter {
	File rdfFile = null;
	Model model = null;
	String IRI = null;
	String filePath = null;
	public RDFWriter(String path, String IRI) {
		this.filePath = path;
		model = ModelFactory.createDefaultModel();
		model.createProperty(IRI.concat("label"));
		model.createProperty(IRI.concat("Esiste"));
		model.createProperty(IRI.concat("Union"));
		model.createProperty(IRI.concat("Per ogni"));
		model.createProperty(IRI.concat("Clash"));
		model.createProperty(IRI.concat("Clash-Free"));
		this.IRI = IRI.concat("#");
	}
	
	//TODO
	public void addStatement(String subject, String predicate, String object) {
		Property p = model.getProperty(IRI.concat(predicate));
		//Property p = model.createProperty(IRI.concat(predicate));
		Resource s = model.getResource(IRI.concat(subject));
		Resource o = model.getResource(IRI.concat(object));
		Statement statement = model.createStatement(s, p, o); 
		this.model = model.add(statement);
	}
	
	public Resource editLabelProperty(String URI, String newPropValue) {
		Resource editedRes = model.getResource(this.IRI.concat(URI));
		editedRes = editedRes.removeProperties();
		editedRes.addProperty(model.getProperty(IRI.concat("label")), newPropValue);
		return editedRes;
	}
	
	public Resource addResource(String URI,  String propValue) {
		System.out.println(this.IRI.concat(URI));
		Resource newRes = model.createResource(this.IRI.concat(URI));
		newRes.addProperty(model.getProperty(IRI.concat("label")), propValue);
//		res.add(newRes);
		return newRes;
	}
	
	//TODO
	public Resource addResource(String URI) {
		Resource newRes = model.createResource(this.IRI.concat(URI));
		return newRes;
	}
	private void clearModel() {
		this.model= ModelFactory.createDefaultModel();
		this.model.createProperty(IRI.concat("label"));
	}
	public void printAndClearModel(String fileName) {
		try {
			this.rdfFile = new File(filePath.concat("\\").concat(fileName));
			model.write(new FileWriter(rdfFile), "RDF/XML");
			System.out.println("RDF file created at: " + rdfFile.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Cannot print to file\n");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\n##################RDF#######################");
		model.write(System.out, "RDF/XML");
		System.out.println("############################################\n");
		clearModel();
		//TODO bisogna pulire il modello 
		return;
	}
}
