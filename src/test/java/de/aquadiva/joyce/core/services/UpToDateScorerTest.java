package de.aquadiva.joyce.core.services;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.aquadiva.joyce.base.data.OntologyModule;
import de.aquadiva.joyce.base.data.OntologySet;
import de.aquadiva.joyce.base.data.ScoreType;
import de.aquadiva.joyce.core.services.UpToDateScorer;

public class UpToDateScorerTest {
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
	static Date currentDate;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	//TODO: test with real input ontologies
	
	@Before
	public void setup() {
				
		// ONTOLOGY MODULE NULL: a null module
		
		// ONTOLOGY MODULE 1: latest release from 2014/10/15 10:15:45
		module1 = new OntologyModule();
		Date d1 = null;
		try {
			d1 = sdf.parse("2014/10/15 10:15:45");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		module1.setLatestReleaseDate(d1);
		
		// uncomment this, if you want to allow for multiple occurences of the same class in one ontology/module
		// ONTOLOGY MODULE 2: latest release from today
//		HashMultiset<String> moduleClassIds2 = HashMultiset.create(2);
//		moduleClassIds2.add("class 1", 1);
//		moduleClassIds2.add("class 3", 2);
//		module2 = new OntologyModule();
//		module2.setClassIds(moduleClassIds2);
//		Date d2 = new Date();
//		module2.setLatestReleaseDate(d2);
		
		// ONTOLOGY MODULE 2: latest release from today
		HashSet<String> moduleClassIds2 = new HashSet<String>();
		moduleClassIds2.add("class 1");
		moduleClassIds2.add("class 3");
		module2 = new OntologyModule();
		module2.setClassIds(moduleClassIds2);
		Date d2 = new Date();
		module2.setLatestReleaseDate(d2);

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

		// ONTOLOGY MODULE 3: latest release from 1969/01/01 00:00:00 (before 1970/01/01 00:00:00)
		HashSet<String> moduleClassIds3 = new HashSet<String>();
		moduleClassIds3.add("class 1");
		moduleClassIds3.add("class 2");
		module3 = new OntologyModule();
		module3.setClassIds(moduleClassIds3);
		Date d3 = null;
		try {
			d3 = sdf.parse("1969/01/01 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		module3.setLatestReleaseDate(d3);		
		
		// ONTOLOGY MODULE 4: latest release date null
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
		
		// date after 1970/01/01 00:00:00
		(new UpToDateScorer()).score(module2);
		assertEquals( 1.0, module2.getScore(ScoreType.UP_TO_DATE).doubleValue(), 0.0 );
		
		// date before 1970/01/01 00:00:00
		(new UpToDateScorer()).score(module2);
		assertEquals( 0.0, module3.getScore(ScoreType.UP_TO_DATE).doubleValue(), 0.0 );

	}
	
	@Test
	public void testScoreSingleOntologyWithInvalidInputs() {
		
		// test for invalid inputs
		
		// input ontology NULL
		(new UpToDateScorer()).score(moduleNULL);
		
		// latest release date null
		(new UpToDateScorer()).score(module4);
		assertEquals( 0.0, module4.getScore(ScoreType.UP_TO_DATE).doubleValue(), 0.0 );

	}
	
	@Test
	public void testScoreOntologySetFromScratchWithInvalidInputs() {

		// set is null
		(new UpToDateScorer()).score(sNULL);
		
		// set contains no ontologies
		(new UpToDateScorer()).score(s1);
		assertEquals( 0.0 , s1.getScore(ScoreType.UP_TO_DATE).doubleValue(), 0.0 );
		
		// an ontology is null
		(new UpToDateScorer()).score(s3);
		assertEquals( 0.0 , s3.getScore(ScoreType.UP_TO_DATE).doubleValue(), 0.0 );
		
		// an ontology has no classes assigned to it
		(new UpToDateScorer()).score(s4);
		assertEquals( 0.0 , s4.getScore(ScoreType.UP_TO_DATE).doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testGetScoreAddedWithInvalidInputs() {
		
		// test for invalid inputs
		
		// set is null
		Double score = (new UpToDateScorer()).getScoreAdded(sNULL, module3);
		assertNull( score );
		
		// set contains no ontologies
		score = (new UpToDateScorer()).getScoreAdded(s1, module3);
		assertNull( score );
		
		// an ontology is null
		score = (new UpToDateScorer()).getScoreAdded(s5, moduleNULL);
		assertNull( score );
		
		// an ontology has no classes assigned to it
		score = (new UpToDateScorer()).getScoreAdded(s4, module3);
		assertNull( score );

	}

	@Test
	public void testGetScoreRemovedWithInvalidInputs() {
		
		// test for invalid inputs
		
		// set is null
		Double score = (new UpToDateScorer()).getScoreRemoved(sNULL, module3);
		assertNull( score );
		
		// set contains no ontologies
		score = (new UpToDateScorer()).getScoreRemoved(s1, module3);
		assertNull( score );
		
		// an ontology is null
		score = (new UpToDateScorer()).getScoreRemoved(s5, moduleNULL);
		assertNull( score );
		
		// an ontology has no classes assigned to it
		score = (new UpToDateScorer()).getScoreRemoved(s4, module3);
		assertNull( score );
		
	}	
	
}
