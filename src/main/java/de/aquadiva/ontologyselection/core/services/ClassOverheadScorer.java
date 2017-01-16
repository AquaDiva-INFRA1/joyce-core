package de.aquadiva.ontologyselection.core.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.common.base.Predicates;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import de.aquadiva.ontologyselection.base.data.IOntology;
import de.aquadiva.ontologyselection.base.data.IOntologySet;
import de.aquadiva.ontologyselection.base.data.InfoType;
import de.aquadiva.ontologyselection.base.data.ScoreType;
import de.aquadiva.ontologyselection.base.services.IVariableOntologyScorer;

/**
 * Calculates the Overhead Score for a single ontology/ or a set of ontologies, which is defined
 * as the percentage (w.r.t. the number of input terms) of classes that cannot be mapped to some input term.
 * 
 * @author friederike
 *
 */
public class ClassOverheadScorer implements IVariableOntologyScorer {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ClassOverhead {
		//
	}

	public void score(IOntology o, Multiset<String> classIds) {
		
		if (o != null && classIds != null) {
			
			// get the set of class ids of the ontology and put them into a Multiset
			Multiset<String> classes = HashMultiset.create(o.getClassIds().size());
			classes.addAll( o.getClassIds() );

			// the classes of the input ontology that cannot be mapped to some input term
			Multiset<String> nonCoveringClasses = Multisets.filter(classes, Predicates.not(Predicates.in(classIds)));
			
			// cache the set of non-covering classes
			o.addCacheInformation(InfoType.NON_COVERING_CLASSES, nonCoveringClasses);
			
			// the number of classes defined within the given ontology that cannot be mapped to some input term
			int numOfNonCoveringClasses = nonCoveringClasses.elementSet().size();

			// total number of input terms
			// TODO: assumes that an input term that cannot be mapped to any class is represented by a special class Id, e.g. NONE
			int numOfTerms = classIds.size();

			// calculate and set the score
			o.setScore(
					ScoreType.CLASS_OVERHEAD,
					-1.0 * Double.valueOf( (double) numOfNonCoveringClasses / (double) numOfTerms) );

		}

	}

	@Override
	public void score(IOntologySet s, Multiset<String> classIds) {
		
		if( s != null && classIds != null ) {
		
			// the classes defined within the given ontology set s that cannot be mapped to some input terms
			Multiset<String> overallNonCoveringClasses = null;
			
			// iteratively compute overallNonCoveringClasses
			for(IOntology o: s.getOntologies() ) {
				
				// get the set of class ids of the ontology and put them into a Multiset
				Multiset<String> classes = HashMultiset.create(o.getClassIds().size());
				classes.addAll( o.getClassIds() );
				
				// determine the set of classes defined within the considered ontology that cannot be mapped to some input term
				Multiset<String> nonCoveringClasses = Multisets.filter(classes, Predicates.not(Predicates.in(classIds)));
				
				// add these to the set of non-covering classes that have already been encountered
				if(overallNonCoveringClasses == null) {
					overallNonCoveringClasses = nonCoveringClasses;
				} else {
					overallNonCoveringClasses = Multisets.sum(overallNonCoveringClasses, nonCoveringClasses);
				}
				
			}
			
			// cache the set of non-covering classes
			s.addCacheInformation(InfoType.NON_COVERING_CLASSES, overallNonCoveringClasses);
			
			// the number of classes defined within the given set of ontologies that cannot be mapped to some input term
			int numOfNonCoveringTerms = overallNonCoveringClasses.elementSet().size();
			
			// calculate and set the score
			s.setScore(
					ScoreType.CLASS_OVERHEAD,
					-1.0 * Double.valueOf( (double) numOfNonCoveringTerms / (double) classIds.size()) );
			
		}				

	}

	@Override
	public Double getScoreAdded(IOntologySet s, IOntology o, Multiset<String> classIds) {
		
		if( s != null && classIds != null && o != null ) {
			
			// the classes defined within the given ontology set s that cannot be mapped to some input term (if it has been already computed, get it from the cache)
			Multiset<String> nonCoveringClassesOfS = s.getCachedInformation(InfoType.NON_COVERING_CLASSES);
			if( nonCoveringClassesOfS == null ) {
				this.score(s, classIds);
				nonCoveringClassesOfS = s.getCachedInformation(InfoType.NON_COVERING_CLASSES);
			}
			
			// the classes of the input ontology o that do not match to some input terms (if it has been already computed, get it from the cache)
			Multiset<String> nonCoveringClassesOfO = o.getCachedInformation(InfoType.NON_COVERING_CLASSES);
			if( nonCoveringClassesOfO == null ) {
				this.score(o, classIds);
				nonCoveringClassesOfO = o.getCachedInformation(InfoType.NON_COVERING_CLASSES);
			}
			
			//determine the set of non-covering classes of s and o
			Multiset<String> overallNonCoveringClasses = Multisets.sum(nonCoveringClassesOfS, nonCoveringClassesOfO);
			
			// the number of classes defined within s and o that cannot be mapped to some input term
			int numOfNonCoveringClasses = overallNonCoveringClasses.elementSet().size();
			
			// calculate and return the score
			return -1.0 * Double.valueOf( (double) numOfNonCoveringClasses / (double) classIds.size() );
		
		} else {
			return null;
		}		
		
	}

	@Override
	public Double getScoreRemoved(IOntologySet s, IOntology o, Multiset<String> classIds) {

		if( s != null && classIds != null && o != null ) {
			
			// the classes defined within the given ontology set s that cannot be mapped to some input term (if it has been already computed, get it from the cache)
			Multiset<String> nonCoveringClassesOfS = s.getCachedInformation(InfoType.NON_COVERING_CLASSES);
			if( nonCoveringClassesOfS == null ) {
				this.score(s, classIds);
				nonCoveringClassesOfS = s.getCachedInformation(InfoType.NON_COVERING_CLASSES);
			}
			
			// the classes of the input ontology o that do not match to some input terms (if it has been already computed, get it from the cache)
			Multiset<String> nonCoveringClassesOfO = o.getCachedInformation(InfoType.NON_COVERING_CLASSES);
			if( nonCoveringClassesOfO == null ) {
				this.score(o, classIds);
				nonCoveringClassesOfO = o.getCachedInformation(InfoType.NON_COVERING_CLASSES);
			}
			
			//determine the set of non-covering classes of s without o
			Multiset<String> overallNonCoveringClasses = Multisets.difference(nonCoveringClassesOfS, nonCoveringClassesOfO);
			
			// the number of classes defined within s without o that cannot be mapped to some input term
			int numOfNonCoveringClasses = overallNonCoveringClasses.elementSet().size();
			
			// calculate and return the score
			return -1.0 * Double.valueOf( (double) numOfNonCoveringClasses / (double) classIds.size() );
		
		} else {
			return null;
		}		
	
	
	}

}
