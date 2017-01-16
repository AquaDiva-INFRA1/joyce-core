package de.aquadiva.ontologyselection.core.services;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import de.aquadiva.ontologyselection.base.data.OntologyModule;
import de.aquadiva.ontologyselection.base.data.OntologySet;
import de.aquadiva.ontologyselection.base.data.ScoreType;

public class PopularityScorerTest {
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
	
	//TODO: test with real input ontologies
	
	@Before
	public void setup() {
				
		// ONTOLOGY MODULE NULL: a null module
		
		// ONTOLOGY MODULE 1: 0 referencing projects
		module1 = new OntologyModule();
		module1.setNumberOfReferencingProjects(0);
		
		// uncomment this, if you want to allow for multiple occurences of the same class in one ontology/module
		// ONTOLOGY MODULE 2: latest release from today
//		HashMultiset<String> moduleClassIds2 = HashMultiset.create(2);
//		moduleClassIds2.add("class 1", 1);
//		moduleClassIds2.add("class 3", 2);
//		module2 = new OntologyModule();
//		module2.setClassIds(moduleClassIds2);
//		Date d2 = new Date();
//		module2.setLatestReleaseDate(d2);
		
		// ONTOLOGY MODULE 2: 0 referencing projects
		HashSet<String> moduleClassIds2 = new HashSet<String>();
		moduleClassIds2.add("class 1");
		moduleClassIds2.add("class 3");
		module2 = new OntologyModule();
		module2.setClassIds(moduleClassIds2);
		module2.setNumberOfReferencingProjects(0);

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

		// ONTOLOGY MODULE 3: 307 referencing projects (currently all)
		HashSet<String> moduleClassIds3 = new HashSet<String>();
		moduleClassIds3.add("class 1");
		moduleClassIds3.add("class 2");
		module3 = new OntologyModule();
		module3.setClassIds(moduleClassIds3);
		module3.setNumberOfReferencingProjects(307);
		
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
		
		// 0 references
		(new PopularityScorer()).score(module2);
		assertEquals( 0.0, module2.getScore(ScoreType.POPULARITY).doubleValue(), 0.0 );
		
		// 307 references
		(new PopularityScorer()).score(module3);
		assertEquals( 1.0, module3.getScore(ScoreType.POPULARITY).doubleValue(), 0.0 );

	}
	
	@Test
	public void testScoreSingleOntologyWithInvalidInputs() {
		
		// test for invalid inputs
		
		// input ontology NULL
		(new PopularityScorer()).score(moduleNULL);
		
		// latest release date null
		(new PopularityScorer()).score(module4);
		assertEquals( 0.0, module4.getScore(ScoreType.POPULARITY).doubleValue(), 0.0 );

	}
	
	@Test
	public void testScoreOntologySetFromScratchWithValidInputs() {
		
		// test correctness of the result for a valid input 
		
		(new PopularityScorer()).score(s2);
		assertEquals( 0.5 , s2.getScore(ScoreType.POPULARITY).doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testScoreOntologySetFromScratchWithInvalidInputs() {

		// set is null
		(new PopularityScorer()).score(sNULL);
		
		// set contains no ontologies
		(new PopularityScorer()).score(s1);
		assertEquals( 0.0 , s1.getScore(ScoreType.POPULARITY).doubleValue(), 0.0 );
		
		// an ontology is null
		(new PopularityScorer()).score(s3);
		assertEquals( 0.0 , s3.getScore(ScoreType.POPULARITY).doubleValue(), 0.0 );
		
		// an ontology has no classes assigned to it
		(new PopularityScorer()).score(s4);
		assertEquals( 0.0 , s4.getScore(ScoreType.POPULARITY).doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testGetScoreAddedWithValidInputs() {
		
		// test correctness of the result for a valid input 
		
		Double score = (new PopularityScorer()).getScoreAdded(s5, module3);
		assertEquals( 0.5 , score.doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testGetScoreAddedWithInvalidInputs() {
		
		// test for invalid inputs
		
		// set is null
		Double score = (new PopularityScorer()).getScoreAdded(sNULL, module3);
		assertNull( score );
		
		// set contains no ontologies
		score = (new PopularityScorer()).getScoreAdded(s1, module3);
		assertNull( score );
		
		// an ontology is null
		score = (new PopularityScorer()).getScoreAdded(s5, moduleNULL);
		assertNull( score );
		
		// an ontology has no classes assigned to it
		score = (new PopularityScorer()).getScoreAdded(s4, module3);
		assertNull( score );

	}

	@Test
	public void testGetScoreRemovedWithValidInputs() {
		
		// test correctness of the result for a valid input 
		
		Double score = (new PopularityScorer()).getScoreRemoved(s2, module3);
		assertEquals( 0.0 , score.doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testGetScoreRemovedWithInvalidInputs() {
		
		// test for invalid inputs
		
		// set is null
		Double score = (new PopularityScorer()).getScoreRemoved(sNULL, module3);
		assertNull( score );
		
		// set contains no ontologies
		score = (new PopularityScorer()).getScoreRemoved(s1, module3);
		assertNull( score );
		
		// an ontology is null
		score = (new PopularityScorer()).getScoreRemoved(s5, moduleNULL);
		assertNull( score );
		
		// an ontology has no classes assigned to it
		score = (new PopularityScorer()).getScoreRemoved(s4, module3);
		assertNull( score );
		
	}	
	
}
