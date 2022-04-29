package turcobardi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * Classe che si occupa di creare il file .rdf corrispondente al Tableaux e stamparlo su file o terminale
 *
 */
public class RDFWriter {
	File rdfFile = null;
	Model model = null;
	String IRI = null; //Viene utilizzato per costruire l'URI delle risorse RDF
	String filePath = null;
	
	
	/**	Costruttore del RDFWriter 
	 * @param path  percorso nel quale stampare il file rdf
	 * @param IRI  IRI da usare come parte dell'URI delle risorse RDF
	 */
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
	
	
	/**
	 * Crea un arco tra due Risorse RDF (da 'subject' ad 'object') 
	 * con proprietà 'predicate' 
	 * @param subject  (sorgente arco)
	 * @param predicate
	 * @param object  (destinazione arco)
	 */
	public void addStatement(String subject, String predicate, String object) {
		Property p = model.getProperty(IRI.concat(predicate));
		//Property p = model.createProperty(IRI.concat(predicate));
		Resource s = model.getResource(IRI.concat(subject));
		Resource o = model.getResource(IRI.concat(object));
		Statement statement = model.createStatement(s, p, o); 
		this.model = model.add(statement);
	}
	
	/**
	 * Modifica la proprietà 'label' di una risorsa e ritorna la risorsa modificata
	 * @param URI  URI della risorsa 
	 * @param newPropValue  Nuovo valore della prorietà 'label'
	 * @return
	 */
	public Resource editLabelProperty(String URI, String newPropValue) {
		Resource editedRes = model.getResource(this.IRI.concat(URI));
		editedRes = editedRes.removeProperties();
		editedRes.addProperty(model.getProperty(IRI.concat("label")), newPropValue);
		return editedRes;
	}
	
	/** Crea una nuova risorsa con proprietà 'label' di valore 'propValue'
	 * @param URI  URI della nuova risorsa
	 * @param propValue  valore da asseganre alla proprietà label
	 * @return
	 */
	public Resource addResource(String URI,  String propValue) {
		System.out.println(this.IRI.concat(URI));
		Resource newRes = model.createResource(this.IRI.concat(URI));
		newRes.addProperty(model.getProperty(IRI.concat("label")), propValue);
//		res.add(newRes);
		return newRes;
	}
	
	
	/** Crea una nuova risorsa RDF
	 * @param URI  URI della risorsa
	 * @return
	 */
	public Resource addResource(String URI) {
		Resource newRes = model.createResource(this.IRI.concat(URI));
		return newRes;
	}
	
	/**
	 * Crea un nuovo modello RDF (eliminando il vecchio) così da poter 
	 * riutilizzare la classe per generare un nuovo file RDF
	 */
	private void clearModel() {
		this.model= ModelFactory.createDefaultModel();
		this.model.createProperty(IRI.concat("label"));
	}
	
	
	/**
	 * Stampa il modello RDF su file e pulisce il modello 
	 * @see RDFWriter.clearModel()
	 * @param fileName  il nome del file da creare. 
	 * @param printToConsole  Se true, stampa l'RDF anche sulla console
	 * 
	 */
	public void printAndClearModel(String fileName, boolean printToConsole) {
		try {
			this.rdfFile = new File(filePath.concat("\\").concat(fileName).concat(".rdf"));
			model.write(new FileWriter(rdfFile), "RDF/XML");
			System.out.println("RDF file created at: " + rdfFile.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Cannot print to file\n");
		}
		if(printToConsole) {
			System.out.println("\n##################RDF#######################");
			model.write(System.out, "RDF/XML");
			System.out.println("############################################\n");			
		}
		clearModel();
		return;
	}
}
