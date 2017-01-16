package de.aquadiva.ontologyselection.core.services;

import java.util.List;

import de.aquadiva.ontologyselection.base.data.Ontology;
import de.aquadiva.ontologyselection.base.data.OntologyModule;
import de.aquadiva.ontologyselection.util.OntologyModularizationException;

public interface IOntologyModularizationService {
	List<OntologyModule> modularize(Ontology o) throws OntologyModularizationException;
}
