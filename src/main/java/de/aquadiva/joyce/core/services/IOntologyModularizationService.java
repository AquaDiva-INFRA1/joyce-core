package de.aquadiva.joyce.core.services;

import java.util.List;

import de.aquadiva.joyce.base.data.Ontology;
import de.aquadiva.joyce.base.data.OntologyModule;
import de.aquadiva.joyce.util.OntologyModularizationException;

public interface IOntologyModularizationService {
	List<OntologyModule> modularize(Ontology o) throws OntologyModularizationException;
}
