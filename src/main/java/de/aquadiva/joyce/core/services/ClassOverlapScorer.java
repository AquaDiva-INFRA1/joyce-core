package de.aquadiva.joyce.core.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import de.aquadiva.joyce.base.data.IOntology;
import de.aquadiva.joyce.base.data.IOntologySet;
import de.aquadiva.joyce.base.data.InfoType;
import de.aquadiva.joyce.base.data.ScoreType;
import de.aquadiva.joyce.base.services.IVariableOntologyScorer;

/**
 * Calculates the Overlap Score for a single ontology/ or a set of ontologies, which is defined
 * as the percentage (w.r.t. the number of input terms) of classes that are defined within several ontologies.
 * The number of occurances of a redundant class is taken into account. Note, that the overlap of a single ontology
 * can be different from 0, if this ontology contains redundant classes.
 * 
 * @author friederike
 *
 */
public class ClassOverlapScorer implements IVariableOntologyScorer {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ClassOverlap {
		//
	}

	public void score(IOntology o, Multiset<String> classIds) {
		
		if (o != null && classIds != null) {
			
			// get the set of class ids of the ontology and put them into a Multiset
			Multiset<String> classes = HashMultiset.create(o.getClassIds().size());
			classes.addAll( o.getClassIds() );
			
			// cache the set of non-covering classes
			o.addCacheInformation(InfoType.ALL_CLASSES, classes);

			// the number of classes that are defined in at least 2 ontologies
			int numOverlappingClasses = countOverlappingClasses(classes);
			
			// calculate and set the score
			o.setScore(
					ScoreType.CLASS_OVERLAP,
					-1.0 * Double.valueOf( (double) numOverlappingClasses / (double) classIds.size()) );
						
		}

	}

	@Override
	public void score(IOntologySet s, Multiset<String> classIds) {
		
		if( s != null && classIds != null ) {
		
			// the classes defined within the given ontology set s
			Multiset<String> allClasses = null;
			
			// iteratively compute allClasses
			for(IOntology o: s.getOntologies() ) {
				
				// get the set of class ids of the ontology and put them into a Multiset
				Multiset<String> classes = HashMultiset.create(o.getClassIds().size());
				classes.addAll( o.getClassIds() );
				
				// add the classes of the considered ontology to those that have already been encountered
				if(allClasses == null) {
					allClasses = classes;
				} else {
					allClasses = Multisets.sum(allClasses, classes);
				}
				
			}
			
			// cache allClasses
			s.addCacheInformation(InfoType.ALL_CLASSES, allClasses);
			
			// the number of classes that are defined in at least 2 ontologies
			int numOverlappingClasses = countOverlappingClasses(allClasses);
			
			// calculate and set the score
			s.setScore(
					ScoreType.CLASS_OVERLAP,
					-1.0 * Double.valueOf( (double) numOverlappingClasses / (double) classIds.size()) );
			
		}				

	}

	@Override
	public Double getScoreAdded(IOntologySet s, IOntology o, Multiset<String> classIds) {
		
		if( s != null && classIds != null && o != null ) {
			
			// the classes defined within the given ontology set s
			Multiset<String> allClassesOfS = s.getCachedInformation(InfoType.ALL_CLASSES);
			if( allClassesOfS == null ) {
				this.score(s, classIds);
				allClassesOfS = s.getCachedInformation(InfoType.ALL_CLASSES);
			}
			
			// the classes defined within the given ontology o
			Multiset<String> allClassesOfO = o.getCachedInformation(InfoType.ALL_CLASSES);
			if( allClassesOfO == null ) {
				this.score(o, classIds);
				allClassesOfO = o.getCachedInformation(InfoType.ALL_CLASSES);
			}
			
			//determine the set of all classes of s and o
			Multiset<String> allClasses = Multisets.sum(allClassesOfS, allClassesOfO);
			
			// the number of classes that are defined in at least 2 ontologies of s and o
			int numOverlappingClasses = countOverlappingClasses(allClasses);
			
			// calculate and return the score
			return -1.0 * Double.valueOf( (double) numOverlappingClasses / (double) classIds.size() );
		
		} else {
			return null;
		}		
		
	}

	@Override
	public Double getScoreRemoved(IOntologySet s, IOntology o, Multiset<String> classIds) {

		if( s != null && classIds != null && o != null ) {
			
			// the classes defined within the given ontology set s
			Multiset<String> allClassesOfS = s.getCachedInformation(InfoType.ALL_CLASSES);
			if( allClassesOfS == null ) {
				this.score(s, classIds);
				allClassesOfS = s.getCachedInformation(InfoType.ALL_CLASSES);
			}
			
			// the classes defined within the given ontology o
			Multiset<String> allClassesOfO = o.getCachedInformation(InfoType.ALL_CLASSES);
			if( allClassesOfO == null ) {
				this.score(o, classIds);
				allClassesOfO = o.getCachedInformation(InfoType.ALL_CLASSES);
			}
			
			//determine the set of all classes of s without o
			Multiset<String> allClasses = Multisets.difference(allClassesOfS, allClassesOfO);
			
			// the number of classes that are defined in at least 2 ontologies of s without o
			int numOverlappingClasses = countOverlappingClasses(allClasses);
			
			// calculate and return the score
			return -1.0 * Double.valueOf( (double) numOverlappingClasses / (double) classIds.size() );
		
		} else {
			return null;
		}		
	
	
	}
	
	/**
	 * Counts the number of overlapping classes, i.e. sums up all counts-1 of those classes with a count > 1.
	 * 
	 * @param allClasses
	 * @return
	 */
	private int countOverlappingClasses(Multiset<String> allClasses) {

		int numOverlappingClasses = 0;
		for(String el : allClasses.elementSet()) {
			int count = allClasses.count(el);
			if(count>1) { numOverlappingClasses+= (count-1); }
		}
		return numOverlappingClasses;
	}

}
