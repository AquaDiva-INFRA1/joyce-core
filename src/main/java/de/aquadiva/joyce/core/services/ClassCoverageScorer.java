package de.aquadiva.joyce.core.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.base.Predicates;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Sets;

import de.aquadiva.joyce.base.data.IOntology;
import de.aquadiva.joyce.base.data.IOntologySet;
import de.aquadiva.joyce.base.data.InfoType;
import de.aquadiva.joyce.base.data.ScoreType;
import de.aquadiva.joyce.base.services.IVariableOntologyScorer;

/**
 * Calculates the Coverage Score for a single ontology/module or set of
 * ontologies, which is defined as the percentage of input terms that can be
 * mapped to some concept in the input ontology/module.
 * 
 * @author friederike
 * 
 */
public class ClassCoverageScorer implements IVariableOntologyScorer {

	private Logger log;

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ClassCoverage {
		//
	}

	public ClassCoverageScorer(Logger log) {
		this.log = log;
	}

	public void score(IOntology o, Multiset<String> classIds) {

		if (o != null && classIds != null) {

			// get the set of class ids of the ontology and put them into a
			// Multiset
			Multiset<String> classes = HashMultiset.create(o.getClassIds().size());
			classes.addAll(o.getClassIds());

			// the classes of the input ontology that cover the input terms
			Multiset<String> coveringClasses = Multisets.filter(classes, Predicates.in(classIds));

			// cache the set of covering classes
			o.addCacheInformation(InfoType.COVERING_CLASSES, coveringClasses);

			// the number of input terms covered by the classes defined within
			// the given ontology
			Multiset<String> coveredTerms = Multisets.filter(classIds, Predicates.in(coveringClasses));
			int numOfCoveredTerms = coveredTerms.size();

			// total number of input terms
			// TODO: assumes that an input term that cannot be mapped to any
			// class is represented by a special class Id, e.g. NONE
			int numOfTerms = classIds.size();

			// calculate and set the score
			o.setScore(ScoreType.TERM_COVERAGE, Double.valueOf((double) numOfCoveredTerms / (double) numOfTerms));

		}

	}

	@Override
	public void score(IOntologySet s, Multiset<String> classIds) {

		if (s != null && classIds != null) {

			// the classes defined within the given ontology set s that cover
			// the given input terms
			Multiset<String> overallCoveringClasses = null;

			// iteratively compute overallCoveringClasses
			for (IOntology o : s.getOntologies()) {

				// get the set of class ids of the ontology and put them into a
				// Multiset
				Multiset<String> classes = HashMultiset.create(o.getClassIds().size());
				classes.addAll(o.getClassIds());
				if (classes.isEmpty())
					log.warn("No class IDs found for ontology {}", o.getId());

				// determine the set of terms covered by the considered ontology
				Multiset<String> coveringClasses = Multisets.filter(classes, Predicates.in(classIds));

				// add these to the set of covering classes that have already
				// been encountered
				log.debug("coveringClasses for {}: {}", o.getId(),
						null == coveringClasses ? 0 : coveringClasses.size());
				if (overallCoveringClasses == null) {
					overallCoveringClasses = coveringClasses;
				} else {
					overallCoveringClasses = Multisets.sum(overallCoveringClasses, coveringClasses);
					log.debug("final overallCoveringClasses for {}: {}", o.getId(), overallCoveringClasses.size());
				}

			}
			log.debug("Final covering for set: {}", overallCoveringClasses.size());

			Set<String> missingClasses = Sets.filter(classIds.elementSet(),
					Predicates.not(Predicates.in(overallCoveringClasses)));

			// cache the set of covering classes
			s.addCacheInformation(InfoType.COVERING_CLASSES, overallCoveringClasses);
			s.addCacheInformation(InfoType.MISSING_CLASSES, HashMultiset.create(missingClasses));

			// the number of input terms covered by the classes defined within
			// the given ontology
			Multiset<String> coveredTerms = Multisets.filter(classIds, Predicates.in(overallCoveringClasses));
			int numOfCoveredTerms = coveredTerms.size();

			// calculate and set the score
			s.setScore(ScoreType.TERM_COVERAGE, Double.valueOf((double) numOfCoveredTerms / (double) classIds.size()));
			log.debug("Covering score: {}", s.getScore(ScoreType.TERM_COVERAGE));
		} else {
			log.warn("Could not compute a class coverage score for an ontology set. OntologySet: {}, classIds: {}", s,
					classIds);
		}

	}

	@Override
	public Double getScoreAdded(IOntologySet s, IOntology o, Multiset<String> classIds) {

		if (s != null && classIds != null && o != null) {

			// the classes defined within the given ontology set s that cover
			// the given input terms (if it has been already computed, get it
			// from the cache)
			Multiset<String> coveringClassesOfS = s.getCachedInformation(InfoType.COVERING_CLASSES);
			if (coveringClassesOfS == null) {
				this.score(s, classIds);
				coveringClassesOfS = s.getCachedInformation(InfoType.COVERING_CLASSES);
			}

			// the classes of the input ontology o that cover the input terms
			// (if it has been already computed, get it from the cache)
			Multiset<String> coveringClassesOfO = o.getCachedInformation(InfoType.COVERING_CLASSES);
			if (coveringClassesOfO == null) {
				this.score(o, classIds);
				coveringClassesOfO = o.getCachedInformation(InfoType.COVERING_CLASSES);
			}

			// determine the set of classes covered by s and o
			Multiset<String> overallCoveringClasses = Multisets.sum(coveringClassesOfS, coveringClassesOfO);

			// the number of input terms covered by the classes defined within s
			// and o
			Multiset<String> coveredTerms = Multisets.filter(classIds, Predicates.in(overallCoveringClasses));
			int numOfCoveredTerms = coveredTerms.size();

			// calculate and return the score
			return Double.valueOf((double) numOfCoveredTerms / (double) classIds.size());

		} else {
			return null;
		}

	}

	@Override
	public Double getScoreRemoved(IOntologySet s, IOntology o, Multiset<String> classIds) {

		if (s != null && classIds != null && o != null) {

			// the classes defined within the given ontology set s that cover
			// the given input terms (if it has been already computed, get it
			// from the cache)
			Multiset<String> coveringClassesOfS = s.getCachedInformation(InfoType.COVERING_CLASSES);
			if (coveringClassesOfS == null) {
				this.score(s, classIds);
				coveringClassesOfS = s.getCachedInformation(InfoType.COVERING_CLASSES);
			}

			// the classes of the input ontology o that cover the input terms
			// (if it has been already computed, get it from the cache)
			Multiset<String> coveringClassesOfO = o.getCachedInformation(InfoType.COVERING_CLASSES);
			if (coveringClassesOfO == null) {
				this.score(o, classIds);
				coveringClassesOfO = o.getCachedInformation(InfoType.COVERING_CLASSES);
			}

			// determine the set of classes covered by s without o
			Multiset<String> overallCoveringClasses = Multisets.difference(coveringClassesOfS, coveringClassesOfO);

			// the number of input terms covered by the classes defined within s
			// without o
			Multiset<String> coveredTerms = Multisets.filter(classIds, Predicates.in(overallCoveringClasses));
			int numOfCoveredTerms = coveredTerms.size();

			// calculate and return the score
			return Double.valueOf((double) numOfCoveredTerms / (double) classIds.size());

		} else {
			return null;
		}

	}

}
