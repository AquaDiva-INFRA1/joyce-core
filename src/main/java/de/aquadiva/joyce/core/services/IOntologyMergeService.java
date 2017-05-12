package de.aquadiva.joyce.core.services;

//import java.util.List;

import de.aquadiva.joyce.base.data.Ontology;
//import de.aquadiva.joyce.base.data.OntologyModule;

public interface IOntologyMergeService {
	Ontology merge(Ontology o1, Ontology o2);
	Ontology merge(String path);

}
