package turcobardi;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 OWLOntologyManager man =  OWLManager.createOWLOntologyManager();
			File file = new File("C:\\Users\\turco\\OneDrive\\Desktop\\ProgettoIntelligentWeb\\pizza.owl.xml");
			OWLOntology o;
			try {
				o = man.loadOntologyFromOntologyDocument(file);
				System.out.println(o);
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
