package de.aquadiva.joyce.core.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.HashSet;

import de.aquadiva.joyce.base.data.IOntology;
import de.aquadiva.joyce.base.data.IOntologySet;
import de.aquadiva.joyce.base.data.ScoreType;
import de.aquadiva.joyce.base.services.IConstantOntologyScorer;

/**
 * Calculates the UpToDate Score for a single ontology/module or set of ontologies, which is defined
 * as the percentage of time elapsed from January 1, 1970 00:00:00 to the latest release of the ontology
 * compared to time elapsed from January 1, 1970 00:00:00 till today (= 100%). Hence, the more recent
 * the latest release of the input ontology/module, the higher the score. For a set of ontologies,
 * the UpToDate Score is computed as the average of the UpToDate Score of the ontologies it comprises
 * weighted by the share of classes they contribute to the overall number of classes defined within
 * the set of ontologies.
 * 
 * @author friederike
 *
 */
public class UpToDateScorer implements IConstantOntologyScorer {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface UpToDate {
		//
	}

	public void score(IOntology o) {
		
		if(o != null && o.getLatestReleaseDate() != null) {
			// get the latest release date of the input ontology and the current date
			Date latestRelease = o.getLatestReleaseDate();
			Date currentDate = new Date();
			
			// calculate the score (precision 1 day)
			double score = Math.ceil( (double) latestRelease.getTime() / 86400.0 ) / Math.ceil( (double) currentDate.getTime() / 86400.0 );
			if(score<0) { score = (long) 0; }
						
			// calculate and set the score
			o.setScore(
					ScoreType.UP_TO_DATE,
					Double.valueOf( (double) score ) );
		}
		
	}

	@Override
	public void score(IOntologySet s) {
		
		// the ontology set is not null
		if( s!=null ) {
			
			Double score = ScorerUtil.getSetScore(s.getOntologies(), ScoreType.UP_TO_DATE, this );
			
			// score is not null
			if( score!=null ) {
				
				// calculate and set the score
				s.setScore(
						ScoreType.UP_TO_DATE,
						Double.valueOf( score ) );
				
			}
			
		}
					
	}

	@Override
	public Double getScoreAdded(IOntologySet s, IOntology o) {
		
		if( s!=null && o!=null && s.getOntologies()!=null && !s.getOntologies().isEmpty()) {
			
			// it's not worth the effort for this kind of score, just use this.score(IOntologySet s)
			HashSet<IOntology> ontologies = new HashSet<IOntology>();
			ontologies.addAll(s.getOntologies());
			ontologies.add(o);
			
			return ScorerUtil.getSetScore(ontologies, ScoreType.UP_TO_DATE, this );
			
		} else {
			return null;
		}
		
	}

	@Override
	public Double getScoreRemoved(IOntologySet s, IOntology o) {
		
		if( s!=null && o!=null && s.getOntologies()!=null ) {
			// it's not worth the effort for this kind of score, just use this.score(IOntologySet s)
			HashSet<IOntology> ontologies = new HashSet<IOntology>();
			ontologies.addAll(s.getOntologies());
			ontologies.remove(o);
			
			return ScorerUtil.getSetScore(ontologies, ScoreType.UP_TO_DATE, this );
			
		} else {
			return null;
		}
		
	}

}
