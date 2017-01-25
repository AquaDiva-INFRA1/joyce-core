package de.aquadiva.joyce.core.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;

import de.aquadiva.joyce.base.data.IOntology;
import de.aquadiva.joyce.base.data.IOntologySet;
import de.aquadiva.joyce.base.data.ScoreType;
import de.aquadiva.joyce.base.services.IConstantOntologyScorer;

/**
 * Calculates the ActiveCommunityScore for a single ontology/module or set of
 * ontologies, which is supposed to measure how active the development community
 * around a given ontology is. It is determined based on the frequency of new
 * releases (submissions to BioPortal, average frequency per year weighted with
 * some decay), on the question whether there is a homepage and a documentation
 * page and the status of the ontology (e.g. "production"). All information can
 * be acquired from BioPortal.
 * 
 * @author friederike
 * 
 */
// TODO might be extended by considering more information such as the view
// statistics provided by BioPortal
public class ActiveCommunityScorer implements IConstantOntologyScorer {
	private static final int MAX_NUM_OF_SUBMS_PER_YEAR = 20; // largest number
																// of
																// submissions
																// per year
																// listed on
																// BioPortal
	private Logger log;

	// TODO this number may change over time, we can dynamically extract this
	// information from BioPortal

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ActiveCommunity {
		//
	}

	public ActiveCommunityScorer(Logger log) {
		this.log = log;
	}

	public void score(IOntology o) {

		if (o != null) {

			URL homepage = null;
			URL docpage = null;
			String homepageUrlString = o.getHomepage();
			String docpageUrlString = o.getDocumentationPage();
			if (homepageUrlString != null && docpageUrlString != null) {
				try {
					homepage = new URL(homepageUrlString);
					docpage = new URL(docpageUrlString);
				} catch (MalformedURLException e) {
					log.warn(
							"Ontology specifies invalid URL for homepage or documentation page. The exception was: ",
							e);
				}
			}

			Map<Integer, Integer> submissions = o.getSubmissions();

			if (homepage != null && docpage != null && submissions != null) {

				/**
				 * score homepage
				 */
				// if a homepage is existing, the score is 1, else 0
				double scoreHomepage = 0.0;
				if (this.checkURL(homepage)) {
					scoreHomepage = 1.0;
				}

				/**
				 * score documentation
				 */
				// if a documentation page is existing, the score is 1, else 0
				double scoreDocumentation = 0.0;
				if (this.checkURL(docpage)) {
					scoreDocumentation = 1.0;
				}

				/**
				 * score frequency of updates
				 */
				// get current year
				Date currentDate = new Date();
				String format = new SimpleDateFormat("yyyy")
						.format(currentDate);
				int currentYear = Integer.parseInt(format);

				int totalNumberOfYears = currentYear - 1970;
				double totalWeights = 2.0 - (1 / Math.pow(2.0,
						(double) totalNumberOfYears));
				double sum = 0.0;

				// calculate weighted average frequency using the decay f(n) = 1
				// / 2^n and n = current year - submission year
				for (Map.Entry<Integer, Integer> entry : o.getSubmissions()
						.entrySet()) {
					sum += entry.getValue()
							* (1 / Math.pow(2.0, (double) currentYear
									- (double) entry.getKey()));
				}

				double scoreUpdate = sum / totalWeights
						/ (double) MAX_NUM_OF_SUBMS_PER_YEAR;

				/**
				 * score status
				 */
				// if the status is "production", the score is 1, else 0
				// TODO can be improved, for now I do not know about the
				// possible values of status on BioPortal
				double scoreStatus = 0.0;
				if (o.getStatus().equalsIgnoreCase("production")) {
					scoreStatus = 1.0;
				}

				// calculate and set the score
				o.setScore(
						ScoreType.ACTIVE_COMMUNITY,
						Double.valueOf((scoreHomepage + scoreDocumentation
								+ scoreUpdate + scoreStatus) / 4.0));

			}

		}
	}

	@Override
	public void score(IOntologySet s) {

		// the ontology set is not null
		if (s != null) {

			Double score = ScorerUtil.getSetScore(s.getOntologies(),
					ScoreType.ACTIVE_COMMUNITY, this);

			// score is not null
			if (score != null) {

				// calculate and set the score
				s.setScore(ScoreType.ACTIVE_COMMUNITY, Double.valueOf(score));

			}

		}

	}

	@Override
	public Double getScoreAdded(IOntologySet s, IOntology o) {

		if (s != null && o != null && s.getOntologies() != null && !s.getOntologies().isEmpty()) {

			// it's not worth the effort for this kind of score, just use
			// this.score(IOntologySet s)
			HashSet<IOntology> ontologies = new HashSet<IOntology>();
			ontologies.addAll(s.getOntologies());
			ontologies.add(o);

			return ScorerUtil.getSetScore(ontologies,
					ScoreType.ACTIVE_COMMUNITY, this);

		} else {
			return null;
		}

	}

	@Override
	public Double getScoreRemoved(IOntologySet s, IOntology o) {

		if (s != null && o != null && s.getOntologies() != null) {
			// it's not worth the effort for this kind of score, just use
			// this.score(IOntologySet s)
			HashSet<IOntology> ontologies = new HashSet<IOntology>();
			ontologies.addAll(s.getOntologies());
			ontologies.remove(o);

			return ScorerUtil.getSetScore(ontologies,
					ScoreType.ACTIVE_COMMUNITY, this);

		} else {
			return null;
		}

	}

	/**
	 * Checks whether the given URL exists or not.
	 * 
	 * @param urlToCheck
	 * @return
	 */
	private boolean checkURL(URL urlToCheck) {

		HttpURLConnection conn;
		try {

			conn = (HttpURLConnection) urlToCheck.openConnection();
			conn.setRequestMethod("HEAD");

			return (conn.getResponseCode() == HttpURLConnection.HTTP_OK);

		} catch (Exception e) {

			return false;

		}

	}

}
