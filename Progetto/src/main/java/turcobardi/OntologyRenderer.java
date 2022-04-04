package turcobardi;

import javax.annotation.Nonnull;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OntologyRenderer{
    public void renderGraph(@Nonnull OWLOntology ontology) {
    	IRI iri = ontology.getOntologyID().getOntologyIRI().get();
    	ontology.signature().forEach(s -> System.out.println(s.toString().replace(iri.toString(), "")));
    	return;
    }
}
