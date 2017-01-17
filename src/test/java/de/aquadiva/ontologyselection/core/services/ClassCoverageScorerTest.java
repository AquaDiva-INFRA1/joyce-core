package de.aquadiva.ontologyselection.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultiset;

import de.aquadiva.ontologyselection.base.data.OntologyModule;
import de.aquadiva.ontologyselection.base.data.OntologySet;
import de.aquadiva.ontologyselection.base.data.ScoreType;

public class ClassCoverageScorerTest {
	static HashMultiset<String> inputClassIds;
	static HashMultiset<String> inputClassIdsNULL;
	static OntologyModule moduleNULL;
	static OntologyModule module1;
	static OntologyModule module2;
	static OntologySet sNULL;
	static OntologySet s1;
	static OntologySet s2;
	
	private static final Logger coverageLogger = LoggerFactory.getLogger(ClassCoverageScorer.class);
	
	@Before
	public void setup() {
		// INPUT NULL: a null input
		
		// INPUT classes to cover
		// [class 1 x 3, class 2, class 3 x 2, class 4 x 2, class 5, NONE]
		inputClassIds = HashMultiset.create(6);
		inputClassIds.add("class 1", 3);
		inputClassIds.add("class 2", 1);
		inputClassIds.add("class 3", 2);
		inputClassIds.add("class 4", 2);
		inputClassIds.add("class 5", 1);
		inputClassIds.add("NONE", 1);
		
		// ONTOLOGY MODULE NULL: a null module
		
		// uncomment this, if you want to allow for multiple occurences of the same class in one ontology/module
		// ONTOLOGY MODULE 1: a module containing the classes [class 1, class 2 x 2]
//		HashMultiset<String> moduleClassIds = HashMultiset.create(2);
//		moduleClassIds.add("class 1", 1);
//		moduleClassIds.add("class 2", 2);
//		module1 = new OntologyModule();
//		module1.setClassIds(moduleClassIds);
		
		// ONTOLOGY MODULE 1: a module containing the classes [class 1, class 2]
		HashSet<String> moduleClassIds = new HashSet<String>();
		moduleClassIds.add("class 1");
		moduleClassIds.add("class 2");
		module1 = new OntologyModule();
		module1.setClassIds(moduleClassIds);
		module1.setId("module1");
		
		// uncomment this, if you want to allow for multiple occurences of the same class in one ontology/module
		// ONTOLOGY MODULE 2: a module containing the classes [class 1, class 3 x 2, class 4]
//		HashMultiset<String> moduleClassIds2 = HashMultiset.create(3);
//		moduleClassIds2.add("class 1", 1);
//		moduleClassIds2.add("class 3", 2);
//		moduleClassIds2.add("class 4", 1);
//		module2 = new OntologyModule();
//		module2.setClassIds(moduleClassIds2);
		
		// ONTOLOGY MODULE 2: a module containing the classes [class 1, class 3, class 4]
		HashSet<String> moduleClassIds2 = new HashSet<String>();
		moduleClassIds2.add("class 1");
		moduleClassIds2.add("class 3");
		moduleClassIds2.add("class 4");
		module2 = new OntologyModule();
		module2.setClassIds(moduleClassIds2);
		module2.setId("module2");
		
		// ONTOLOGY SET NULL: a null ontology set
		
		// ONTOLOGY SET 1: comprising of module 1
		s1 = new OntologySet();
		s1.addOntology(module1);
		
		// ONTOLOGY SET 2: comprising of module 1 and 2
		s2 = new OntologySet();
		s2.addOntology(module1);
		s2.addOntology(module2);
	}
	
	@Test
	public void testScoreSingleOntologyWithValidInputs() {
		
		//test correctness of the result for a valid input
		(new ClassCoverageScorer(coverageLogger)).score(module1, inputClassIds);
		assertEquals( 0.4, module1.getScore(ScoreType.TERM_COVERAGE).doubleValue(), 0.0 );

	}
	
	@Test
	public void testScoreSingleOntologyWithInvalidInputs() {
		
		//test for invalid inputs
		(new ClassCoverageScorer(coverageLogger)).score(module1, inputClassIdsNULL);
		assertEquals( 0.0,  module1.getScore(ScoreType.TERM_COVERAGE), 0.0 );
		(new ClassCoverageScorer(coverageLogger)).score(moduleNULL, inputClassIds);
		assertEquals( 0.0,  module1.getScore(ScoreType.TERM_COVERAGE), 0.0 );
		(new ClassCoverageScorer(coverageLogger)).score(moduleNULL, inputClassIdsNULL);
		assertEquals( 0.0,  module1.getScore(ScoreType.TERM_COVERAGE), 0.0 );

	}
	
	@Test
	public void testScoreOntologySetFromScratchWithValidInputs() {
		
		//test correctness of the result for a valid input 
		(new ClassCoverageScorer(coverageLogger)).score(s2, inputClassIds);
		assertEquals( 0.8, s2.getScore(ScoreType.TERM_COVERAGE).doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testScoreOntologySetFromScratchWithInvalidInputs() {
		
		//test for invalid inputs
		(new ClassCoverageScorer(coverageLogger)).score(s2, inputClassIdsNULL);
		assertEquals( 0.0,  s2.getScore(ScoreType.TERM_COVERAGE), 0.0 );
		(new ClassCoverageScorer(coverageLogger)).score(moduleNULL, inputClassIds);
		assertEquals( 0.0,  s2.getScore(ScoreType.TERM_COVERAGE), 0.0 );
		(new ClassCoverageScorer(coverageLogger)).score(moduleNULL, inputClassIdsNULL);
		assertEquals( 0.0,  s2.getScore(ScoreType.TERM_COVERAGE), 0.0 );

	}
	
	@Test
	public void testGetScoreAddedWithValidInputs() {
		
		//test correctness of the result for a valid input 
		double score = (new ClassCoverageScorer(coverageLogger)).getScoreAdded(s1, module2, inputClassIds).doubleValue();
		assertEquals( 0.8, score, 0.0 );
		
		//after the second run, the score for s1 should have been already cached
		score = (new ClassCoverageScorer(coverageLogger)).getScoreAdded(s1, module2, inputClassIds).doubleValue();
		assertEquals( 0.8, score, 0.0 );
		
	}
	
	@Test
	public void testGetScoreAddedWithInvalidInputs() {
		
		//test for invalid inputs
		Double score = (new ClassCoverageScorer(coverageLogger)).getScoreAdded(sNULL, module2, inputClassIds);
		assertNull( score );
		score = (new ClassCoverageScorer(coverageLogger)).getScoreAdded(s1, moduleNULL, inputClassIds);
		assertNull( score );
		score = (new ClassCoverageScorer(coverageLogger)).getScoreAdded(s1, module2, inputClassIdsNULL);
		assertNull( score );
		score = (new ClassCoverageScorer(coverageLogger)).getScoreAdded(sNULL, moduleNULL, inputClassIdsNULL);
		assertNull( score );

	}

	@Test
	public void testGetScoreRemovedWithValidInputs() {
		
		//test correctness of the result for a valid input 
		double score = (new ClassCoverageScorer(coverageLogger)).getScoreRemoved(s2, module2, inputClassIds).doubleValue();
		assertEquals( 0.4, score, 0.0 );
		
		//after the second run, the score for 2 should have been already cached
		score = (new ClassCoverageScorer(coverageLogger)).getScoreRemoved(s2, module2, inputClassIds).doubleValue();
		assertEquals( 0.4, score, 0.0 );
		
	}
	
	@Test
	public void testGetScoreRemovedWithInvalidInputs() {
		
		//test for invalid inputs
		Double score = (new ClassCoverageScorer(coverageLogger)).getScoreRemoved(sNULL, module2, inputClassIds);
		assertNull( score );
		score = (new ClassCoverageScorer(coverageLogger)).getScoreRemoved(s2, moduleNULL, inputClassIds);
		assertNull( score );
		score = (new ClassCoverageScorer(coverageLogger)).getScoreRemoved(s2, module2, inputClassIdsNULL);
		assertNull( score );
		score = (new ClassCoverageScorer(coverageLogger)).getScoreRemoved(sNULL, moduleNULL, inputClassIdsNULL);
		assertNull( score );

	}	
	
}
