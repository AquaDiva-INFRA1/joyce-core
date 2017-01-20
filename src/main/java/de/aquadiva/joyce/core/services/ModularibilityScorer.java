package de.aquadiva.joyce.core.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.common.collect.Multiset;

import de.aquadiva.joyce.base.data.IOntology;
import de.aquadiva.joyce.base.data.IOntologySet;
import de.aquadiva.joyce.base.services.IVariableOntologyScorer;

/**
 * @deprecated We no longer aim at scoring ontologies regarding their modularizability but pre-modularize the ontologies
 *             and then directly work on the modules. So this scorer isn't required any more.
 * @author faessler
 * 
 */
@Deprecated
public class ModularibilityScorer implements IVariableOntologyScorer {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Modularibility {
		//
	}

	public void score(IOntology o, Multiset<String> classIds) {
		// TODO Auto-generated method stub
	}

	@Override
	public void score(IOntologySet o, Multiset<String> classIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Double getScoreAdded(IOntologySet s, IOntology o,
			Multiset<String> classIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getScoreRemoved(IOntologySet s, IOntology o,
			Multiset<String> classIds) {
		// TODO Auto-generated method stub
		return null;
	}

}
