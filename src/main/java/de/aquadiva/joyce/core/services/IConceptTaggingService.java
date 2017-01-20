package de.aquadiva.joyce.core.services;

import com.google.common.collect.Multiset;

public interface IConceptTaggingService {
	/**
	 * Finds BioPortal concept mentions in the given <tt>text</tt>. For this purpose, NLP components built by the JULIE Lab are used. 
	 * @param text
	 * @return
	 */
	Multiset<String> findConcepts(String text);
	
	/**
	 * Finds the terms within the given <tt>text</tt> which can be mapped to BioPortal concepts. For this purpose, NLP components built by the JULIE Lab are used. 
	 * @param text
	 * @return
	 */
	Multiset<String> findCoveredTerms(String text);

	Multiset<String> findCoveredTermsAndConcepts(String text);
}
