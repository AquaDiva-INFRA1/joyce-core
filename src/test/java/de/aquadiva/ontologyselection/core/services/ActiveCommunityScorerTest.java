package de.aquadiva.ontologyselection.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.aquadiva.ontologyselection.base.data.OntologyModule;
import de.aquadiva.ontologyselection.base.data.OntologySet;
import de.aquadiva.ontologyselection.base.data.ScoreType;

public class ActiveCommunityScorerTest {
	static OntologyModule moduleNULL;
	static OntologyModule module1;
	static OntologyModule module2;
	static OntologyModule module3;
	static OntologyModule module4;
	static OntologySet sNULL;
	static OntologySet s1;
	static OntologySet s2;
	static OntologySet s3;
	static OntologySet s4;
	static OntologySet s5;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	//TODO: test with real input ontologies
	
	private static final Logger scorerLogger = LoggerFactory.getLogger(ActiveCommunityScorer.class);
	
	@Before
	public void setup() {
				
			// ONTOLOGY MODULE NULL: a null module
			
			// ONTOLOGY MODULE 1: 20 submissions per year since 1970, existing homepage and documentation page, status="production"
			module1 = new OntologyModule();
			module1.setHomepage(("http://www.google.de/"));
			module1.setDocumentationPage(("http://www.google.de/"));
			HashMap<Integer, Integer> submissions1 = new HashMap<Integer, Integer>();
			for( int year = 1970; year < 2016; year++ ) {
					submissions1.put(year, 20);
			}
			module1.setSubmissions(submissions1);
			module1.setStatus("production");
						
			// uncomment this, if you want to allow for multiple occurences of the same class in one ontology/module
			// ONTOLOGY MODULE 2: latest release from today
//		HashMultiset<String> moduleClassIds2 = HashMultiset.create(2);
//		moduleClassIds2.add("class 1", 1);
//		moduleClassIds2.add("class 3", 2);
//		module2 = new OntologyModule();
//		module2.setClassIds(moduleClassIds2);
//		Date d2 = new Date();
//		module2.setLatestReleaseDate(d2);
			
			// ONTOLOGY MODULE 2: no submissions, no homepage, no documentation page, status different from "production"
			HashSet<String> moduleClassIds2 = new HashSet<String>();
			moduleClassIds2.add("class 1");
			moduleClassIds2.add("class 3");
			module2 = new OntologyModule();
			module2.setClassIds(moduleClassIds2);
			module2.setHomepage(("http://www.non-existing-url.de/"));
			module2.setDocumentationPage(("http://www.non-existing-url.de/"));
			HashMap<Integer, Integer> submissions2 = new HashMap<Integer, Integer>();
			module2.setSubmissions(submissions2);
			module2.setStatus("");

			// uncomment this, if you want to allow for multiple occurences of the same class in one ontology/module
			// ONTOLOGY MODULE 3: latest release from 1969/01/01 00:00:00 (before 1970/01/01 00:00:00)
//		HashMultiset<String> moduleClassIds3 = HashMultiset.create(2);
//		moduleClassIds3.add("class 1", 1);
//		moduleClassIds3.add("class 2", 2);
//		module3 = new OntologyModule();
//		module3.setClassIds(moduleClassIds3);
//		Date d3 = null;
//		try {
//			d3 = sdf.parse("1969/01/01 00:00:00");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		module3.setLatestReleaseDate(d3);

			// ONTOLOGY MODULE 3: 20 submissions per year since 1970, existing homepage and documentation page, status="production"
			HashSet<String> moduleClassIds3 = new HashSet<String>();
			moduleClassIds3.add("class 1");
			moduleClassIds3.add("class 2");
			module3 = new OntologyModule();
			module3.setClassIds(moduleClassIds3);
			module3.setHomepage(("http://www.google.de/"));
			module3.setDocumentationPage(("http://www.google.de/"));
			HashMap<Integer, Integer> submissions3 = new HashMap<Integer, Integer>();
			for( int year = 1970; year < 2016; year++ ) {
				submissions3.put(year, 20);
			}
			module3.setSubmissions(submissions3);
			module3.setStatus("production");
			
			// ONTOLOGY MODULE 4: empty module
			module4 = new OntologyModule();
					
			// ONTOLOGY SET NULL: a null ontology set
			
			// ONTOLOGY SET 1: comprising of no modules
			s1 = new OntologySet();
			
			// ONTOLOGY SET 2: comprising of module 2 and 3
			s2 = new OntologySet();
			s2.addOntology(module2);
			s2.addOntology(module3);
			
			// ONTOLOGY SET 3: comprising of module 2, 3 and moduleNULL
			s3 = new OntologySet();
			s3.addOntology(module2);
			s3.addOntology(module3);
			s3.addOntology(moduleNULL);
			
			// ONTOLOGY SET 4: comprising of module 1, 2 and 3
			s4 = new OntologySet();
			s4.addOntology(module1);
			s4.addOntology(module2);
			s4.addOntology(module3);
			
			// ONTOLOGY SET 5: comprising of module 2
			s5 = new OntologySet();
			s5.addOntology(module2);

	}
	
	@Test
	public void testScoreSingleOntologyWithValidInputs() {
		
		// test correctness of the result for a valid inputs
		
		// no submissions, no homepage, no documentation page, status different from "production" -> score 0
		(new ActiveCommunityScorer(scorerLogger)).score(module2);
		assertEquals( 0.0, module2.getScore(ScoreType.ACTIVE_COMMUNITY).doubleValue(), 0.0 );
		
		// 20 submissions per year since 1970, existing homepage and documentation page, status="production" -> score 1
		(new ActiveCommunityScorer(scorerLogger)).score(module3);
		assertEquals( 1.0, module3.getScore(ScoreType.ACTIVE_COMMUNITY).doubleValue(), 0.0 );

	}
	
	@Test
	public void testScoreSingleOntologyWithInvalidInputs() {
		
		// test for invalid inputs
		
		// input ontology NULL
		(new ActiveCommunityScorer(scorerLogger)).score(moduleNULL);
		
		// empty module
		(new ActiveCommunityScorer(scorerLogger)).score(module4);
		assertEquals( 0.0, module4.getScore(ScoreType.ACTIVE_COMMUNITY).doubleValue(), 0.0 );

	}
	
	@Test
	public void testScoreOntologySetFromScratchWithValidInputs() {
		
		// test correctness of the result for a valid input 
		
		(new ActiveCommunityScorer(scorerLogger)).score(s2);
		assertEquals( 0.5 , s2.getScore(ScoreType.ACTIVE_COMMUNITY).doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testScoreOntologySetFromScratchWithInvalidInputs() {

		// set is null
		(new ActiveCommunityScorer(scorerLogger)).score(sNULL);
		
		// set contains no ontologies
		(new ActiveCommunityScorer(scorerLogger)).score(s1);
		assertEquals( 0.0 , s1.getScore(ScoreType.ACTIVE_COMMUNITY).doubleValue(), 0.0 );
		
		// an ontology is null
		(new ActiveCommunityScorer(scorerLogger)).score(s3);
		assertEquals( 0.0 , s3.getScore(ScoreType.ACTIVE_COMMUNITY).doubleValue(), 0.0 );
		
		// an ontology has no classes assigned to it
		(new ActiveCommunityScorer(scorerLogger)).score(s4);
		assertEquals( 0.0 , s4.getScore(ScoreType.ACTIVE_COMMUNITY).doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testGetScoreAddedWithValidInputs() {
		
		// test correctness of the result for a valid input 
		
		Double score = (new ActiveCommunityScorer(scorerLogger)).getScoreAdded(s5, module3);
		assertEquals( 0.5 , score.doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testGetScoreAddedWithInvalidInputs() {
		
		// test for invalid inputs
		
		// set is null
		Double score = (new ActiveCommunityScorer(scorerLogger)).getScoreAdded(sNULL, module3);
		assertNull( score );
		
		// set contains no ontologies
		score = (new ActiveCommunityScorer(scorerLogger)).getScoreAdded(s1, module3);
		assertNull( score );
		
		// an ontology is null
		score = (new ActiveCommunityScorer(scorerLogger)).getScoreAdded(s5, moduleNULL);
		assertNull( score );
		
		// an ontology has no classes assigned to it
		score = (new ActiveCommunityScorer(scorerLogger)).getScoreAdded(s4, module3);
		assertNull( score );

	}

	@Test
	public void testGetScoreRemovedWithValidInputs() {
		
		// test correctness of the result for a valid input 
		
		Double score = (new ActiveCommunityScorer(scorerLogger)).getScoreRemoved(s2, module3);
		assertEquals( 0.0 , score.doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testGetScoreRemovedWithInvalidInputs() {
		
		// test for invalid inputs
		
		// set is null
		Double score = (new ActiveCommunityScorer(scorerLogger)).getScoreRemoved(sNULL, module3);
		assertNull( score );
		
		// set contains no ontologies
		score = (new ActiveCommunityScorer(scorerLogger)).getScoreRemoved(s1, module3);
		assertNull( score );
		
		// an ontology is null
		score = (new ActiveCommunityScorer(scorerLogger)).getScoreRemoved(s5, moduleNULL);
		assertNull( score );
		
		// an ontology has no classes assigned to it
		score = (new ActiveCommunityScorer(scorerLogger)).getScoreRemoved(s4, module3);
		assertNull( score );
		
	}	
	
}
