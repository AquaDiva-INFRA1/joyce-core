package de.aquadiva.ontologyselection.core.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;

import de.aquadiva.ontologyselection.base.data.IOntology;
import de.aquadiva.ontologyselection.base.data.IOntologySet;
import de.aquadiva.ontologyselection.base.data.ScoreType;
import de.aquadiva.ontologyselection.base.services.IConstantOntologyScorer;

/**
 * Calculates the PopularityScore for a single ontology/module or set of ontologies, which is defined
 * as the fraction of projects that are listed on BioPortal that reference the considered ontology.
 * 
 * @author friederike
 *
 */
// TODO might be extended by considering more information such as the view statistics provided by BioPortal
public class PopularityScorer implements IConstantOntologyScorer {
	private static final int PROJECTS_TOTAL = 307; //the total number of projects listed on BioPortal
	//TODO this number may change over time, we can dynamically extract this information from BioPortal
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Popularity {
		//
	}
	
	public void score(IOntology o) {
		if(o != null && o.getNumberOfReferencingProjects() != null) {
						
			// calculate and set the score
			o.setScore(
					ScoreType.POPULARITY,
					Double.valueOf( (double) o.getNumberOfReferencingProjects().intValue() / (double) PROJECTS_TOTAL ) );
			
		}
	}

	@Override
	public void score(IOntologySet s) {
		
		// the ontology set is not null
		if( s!=null ) {
			
			Double score = ScorerUtil.getSetScore(s.getOntologies(), ScoreType.POPULARITY, this );
			
			// score is not null
			if( score!=null ) {
				
				// calculate and set the score
				s.setScore(
						ScoreType.POPULARITY,
						Double.valueOf( score ) );
				
			}
			
		}
		
	}

	@Override
	public Double getScoreAdded(IOntologySet s, IOntology o) {
		
		if( s!=null && o!=null && s.getOntologies()!=null ) {
			
			// it's not worth the effort for this kind of score, just use this.score(IOntologySet s)
			HashSet<IOntology> ontologies = new HashSet<IOntology>();
			ontologies.addAll(s.getOntologies());
			ontologies.add(o);
			
			return ScorerUtil.getSetScore(ontologies, ScoreType.POPULARITY, this );
			
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
			
			return ScorerUtil.getSetScore(ontologies, ScoreType.POPULARITY, this );
			
		} else {
			return null;
		}
		
	}

}
