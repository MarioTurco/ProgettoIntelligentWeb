package turcobardi;
//esiste Madre.Persona contenuto in Persona
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IRI IOR = IRI.create("http://owl.api.tutorial");
		OWLOntologyManager man =  OWLManager.createOWLOntologyManager();
		OWLOntology o;
		try {
			o=man.createOntology();
			OWLDataFactory df = o.getOWLOntologyManager().getOWLDataFactory();
			//Aggiungiamo un assioma
			OWLClass Person = df.getOWLClass(IOR+"#Person");
			OWLDeclarationAxiom da = df.getOWLDeclarationAxiom(Person);
			o.add(da);
			
			//Creiamo due individui
			OWLIndividual mario = df.getOWLNamedIndividual(IOR + "#mario");
			OWLIndividual rosario = df.getOWLNamedIndividual(IOR + "#rosario");
			
			//Creiamo una relazione
			OWLObjectProperty hasFather = df.getOWLObjectProperty(IOR+"#hasFather");
			OWLAxiom assertion = df.getOWLObjectPropertyAssertionAxiom(hasFather, mario, rosario);
			
			//Aggiungiamo l'assioma e salviamo
			AddAxiom addAxiomChange = new AddAxiom(o, assertion);
			o.applyChange(addAxiomChange);
			
			//Creiamo una restrizione esistenziale
			//Ogni persona ha una relazione "hasFather" con un'altra persona
			OWLClassExpression hasFatherSomePerson = df.getOWLObjectSomeValuesFrom(hasFather, Person);
			OWLSubClassOfAxiom ax =df.getOWLSubClassOfAxiom(Person, hasFatherSomePerson);
			
			o.applyChange(new AddAxiom(o,ax));
			System.out.println(o);
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
