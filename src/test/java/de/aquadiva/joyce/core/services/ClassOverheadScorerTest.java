package de.aquadiva.joyce.core.services;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.HashMultiset;

import de.aquadiva.joyce.base.data.OntologyModule;
import de.aquadiva.joyce.base.data.OntologySet;
import de.aquadiva.joyce.base.data.ScoreType;
import de.aquadiva.joyce.core.services.ClassOverheadScorer;

public class ClassOverheadScorerTest {
	static HashMultiset<String> inputClassIds;
	static HashMultiset<String> inputClassIdsNULL;
	static OntologyModule moduleNULL;
	static OntologyModule module1;
	static OntologyModule module2;
	static OntologySet sNULL;
	static OntologySet s1;
	static OntologySet s2;
	
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
		// ONTOLOGY MODULE 1: a module containing the classes [class 1, class 2 x 2, class 6 x 2]
//		HashMultiset<String> moduleClassIds = HashMultiset.create(3);
//		moduleClassIds.add("class 1", 1);
//		moduleClassIds.add("class 2", 2);
//		moduleClassIds.add("class 6", 2);
//		module1 = new OntologyModule();
//		module1.setClassIds(moduleClassIds);
		
		// ONTOLOGY MODULE 1: a module containing the classes [class 1, class 2, class 6]
		HashSet<String> moduleClassIds = new HashSet<String>();
		moduleClassIds.add("class 1");
		moduleClassIds.add("class 2");
		moduleClassIds.add("class 6");
		module1 = new OntologyModule();
		module1.setClassIds(moduleClassIds);	
		
		// uncomment this, if you want to allow for multiple occurences of the same class in one ontology/module
		// ONTOLOGY MODULE 2: a module containing the classes [class 1, class 3 x 2, class 4, class 7]
//		HashMultiset<String> moduleClassIds2 = HashMultiset.create(4);
//		moduleClassIds2.add("class 1", 1);
//		moduleClassIds2.add("class 3", 2);
//		moduleClassIds2.add("class 4", 1);
//		moduleClassIds2.add("class 7", 1);
//		module2 = new OntologyModule();
//		module2.setClassIds(moduleClassIds2);

		// ONTOLOGY MODULE 2: a module containing the classes [class 1, class 3, class 4, class 7]
		HashSet<String> moduleClassIds2 = new HashSet<String>();
		moduleClassIds2.add("class 1");
		moduleClassIds2.add("class 3");
		moduleClassIds2.add("class 4");
		moduleClassIds2.add("class 7");
		module2 = new OntologyModule();
		module2.setClassIds(moduleClassIds2);
		
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
		
		// test correctness of the result for a valid input
		(new ClassOverheadScorer()).score(module1, inputClassIds);
		assertEquals( -0.1, module1.getScore(ScoreType.CLASS_OVERHEAD).doubleValue(), 0.0 );

	}
	
	@Test
	public void testScoreSingleOntologyWithInvalidInputs() {
		
		// for invalid inputs
		(new ClassOverheadScorer()).score(module1, inputClassIdsNULL);
		assertEquals( 0.0,  module1.getScore(ScoreType.CLASS_OVERHEAD), 0.0 );
		(new ClassOverheadScorer()).score(moduleNULL, inputClassIds);
		assertEquals( 0.0,  module1.getScore(ScoreType.CLASS_OVERHEAD), 0.0 );
		(new ClassOverheadScorer()).score(moduleNULL, inputClassIdsNULL);
		assertEquals( 0.0,  module1.getScore(ScoreType.CLASS_OVERHEAD), 0.0 );

	}
	
	@Test
	public void testScoreOntologySetFromScratchWithValidInputs() {
		
		//test correctness of the result for a valid input 
		(new ClassOverheadScorer()).score(s2, inputClassIds);
		assertEquals( -0.2, s2.getScore(ScoreType.CLASS_OVERHEAD).doubleValue(), 0.0 );
		
	}
	
	@Test
	public void testScoreOntologySetFromScratchWithInvalidInputs() {
		
		//test for invalid inputs
		(new ClassOverheadScorer()).score(s2, inputClassIdsNULL);
		assertEquals( 0.0,  s2.getScore(ScoreType.CLASS_OVERHEAD), 0.0 );
		(new ClassOverheadScorer()).score(moduleNULL, inputClassIds);
		assertEquals( 0.0,  s2.getScore(ScoreType.TERM_COVERAGE), 0.0 );
		(new ClassOverheadScorer()).score(moduleNULL, inputClassIdsNULL);
		assertEquals( 0.0,  s2.getScore(ScoreType.TERM_COVERAGE), 0.0 );

	}
	
	@Test
	public void testGetScoreAddedWithValidInputs() {
		
		//test correctness of the result for a valid input 
		double score = (new ClassOverheadScorer()).getScoreAdded(s1, module2, inputClassIds).doubleValue();
		assertEquals( -0.2, score, 0.0 );
		
		//after the second run, the score for s1 should have been already cached
		score = (new ClassOverheadScorer()).getScoreAdded(s1, module2, inputClassIds).doubleValue();
		assertEquals( -0.2, score, 0.0 );
		
	}
	
	@Test
	public void testGetScoreAddedWithInvalidInputs() {
		
		//test for invalid inputs
		Double score = (new ClassOverheadScorer()).getScoreAdded(sNULL, module2, inputClassIds);
		assertNull( score );
		score = (new ClassOverheadScorer()).getScoreAdded(s1, moduleNULL, inputClassIds);
		assertNull( score );
		score = (new ClassOverheadScorer()).getScoreAdded(s1, module2, inputClassIdsNULL);
		assertNull( score );
		score = (new ClassOverheadScorer()).getScoreAdded(sNULL, moduleNULL, inputClassIdsNULL);
		assertNull( score );

	}

	@Test
	public void testGetScoreRemovedWithValidInputs() {
		
		//test correctness of the result for a valid input 
		double score = (new ClassOverheadScorer()).getScoreRemoved(s2, module2, inputClassIds).doubleValue();
		assertEquals( -0.1, score, 0.0 );
		
		//after the second run, the score for 2 should have been already cached
		score = (new ClassOverheadScorer()).getScoreRemoved(s2, module2, inputClassIds).doubleValue();
		assertEquals( -0.1, score, 0.0 );
		
	}
	
	@Test
	public void testGetScoreRemovedWithInvalidInputs() {
		
		//test for invalid inputs
		Double score = (new ClassOverheadScorer()).getScoreRemoved(sNULL, module2, inputClassIds);
		assertNull( score );
		score = (new ClassOverheadScorer()).getScoreRemoved(s2, moduleNULL, inputClassIds);
		assertNull( score );
		score = (new ClassOverheadScorer()).getScoreRemoved(s2, module2, inputClassIdsNULL);
		assertNull( score );
		score = (new ClassOverheadScorer()).getScoreRemoved(sNULL, moduleNULL, inputClassIdsNULL);
		assertNull( score );

	}	
	
}
