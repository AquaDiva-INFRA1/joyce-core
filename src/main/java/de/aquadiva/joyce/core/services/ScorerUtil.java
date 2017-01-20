package de.aquadiva.joyce.core.services;

import java.util.HashSet;

import de.aquadiva.joyce.base.data.IOntology;
import de.aquadiva.joyce.base.data.ScoreType;
import de.aquadiva.joyce.base.services.IConstantOntologyScorer;

/**
 * A class providing functionality that is used by several scorers.
 * 
 * @author friederike
 *
 */
public class ScorerUtil {
	/**
	 * 
	 * @param s
	 * @return the Up-To-Dateness Score of the given set of ontologies
	 */
	/**
	 * Calculates the score of the given set of ontologies as the weighted average of the single ontologie's scores.
	 * An ontologie's weight is given by the share of classes it defines w.r.t. the total number of classes defined
	 * within the ontologies of the input set.
	 * 
	 * @param s	the set of ontologies
	 * @param scoreType the type of score to calculate
	 * @param scorer a scorer object of the considered type
	 * @return the score of the given set of ontologies
	 */
	public static Double getSetScore(HashSet<IOntology> s, ScoreType scoreType, IConstantOntologyScorer scorer) {
		double sumOfWeightedScores = 0.0;
		double sumOfWeights = 0.0;
		
		// set is not null and contains ontologies
		if( s!= null && s.size()!=0 ) {
			
			for( IOntology o : s ) {
				
				// there's no null ontology
				if( o != null ) {
					
					if( !o.isSetScore(scoreType) ) { scorer.score(o); }
					
					// there is no ontology having no classes assigned to it
					if( o.getClassIds() != null ) {
						
						sumOfWeightedScores += o.getScore(scoreType).doubleValue() * (double) o.getClassIds().size();
						sumOfWeights += o.getClassIds().size();
						
					} else {
						return null;
					}
					
				} else {
					return null;
				}
				
			}
			
			return Double.valueOf( sumOfWeightedScores / sumOfWeights );
			
		} else {
			return null;
		}
	}
}
