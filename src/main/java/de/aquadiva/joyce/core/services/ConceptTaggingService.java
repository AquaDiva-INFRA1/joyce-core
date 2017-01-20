package de.aquadiva.joyce.core.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ExternalResourceDescription;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import de.aquadiva.joyce.JoyceSymbolConstants;
import de.julielab.jcore.ae.lingpipegazetteer.chunking.ChunkerProviderImplAlt;
import de.julielab.jcore.ae.lingpipegazetteer.uima.GazetteerAnnotator;
import de.julielab.jcore.types.OntClassMention;

public class ConceptTaggingService implements IConceptTaggingService {

	private AnalysisEngine bioPortalGazetteerAE;
	private JCas jCas;

	public ConceptTaggingService(@Symbol(JoyceSymbolConstants.GAZETTEER_CONFIG) String gazetteerConfigFile) {
		try {

			ExternalResourceDescription extDesc = ExternalResourceFactory
					.createExternalResourceDescription(
							ChunkerProviderImplAlt.class,
							gazetteerConfigFile);
			bioPortalGazetteerAE = AnalysisEngineFactory.createEngine(
					GazetteerAnnotator.class,
					GazetteerAnnotator.PARAM_OUTPUT_TYPE,
					"de.julielab.jcore.types.OntClassMention",
					GazetteerAnnotator.PARAM_CHECK_ACRONYMS, true,
					GazetteerAnnotator.CHUNKER_RESOURCE_NAME, extDesc);

			jCas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-all-types");
		} catch (UIMAException e) {
			throw new RuntimeException(e);
		} 

	}

	@Override
	public synchronized Multiset<String> findConcepts(String text) {
		try {
			jCas.reset();
			jCas.setDocumentText(text);
			CAS cas = jCas.getCas();

			bioPortalGazetteerAE.process(cas);

			Multiset<String> conceptIds = HashMultiset.create();
			FSIterator<Annotation> it = jCas.getAnnotationIndex(
					OntClassMention.type).iterator();
			while (it.hasNext()) {
				OntClassMention ontClassMention = (OntClassMention) it.next();
				conceptIds.add(ontClassMention.getSpecificType());
			}
			return conceptIds;
		} catch (UIMAException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Multiset<String> findCoveredTerms(String text) {
		try {
			jCas.reset();
			jCas.setDocumentText(text);
			CAS cas = jCas.getCas();

			bioPortalGazetteerAE.process(cas);

			Multiset<String> coveredTerms = HashMultiset.create();
			FSIterator<Annotation> it = jCas.getAnnotationIndex(
					OntClassMention.type).iterator();
			while (it.hasNext()) {
				OntClassMention ontClassMention = (OntClassMention) it.next();
				coveredTerms.add(ontClassMention.getCoveredText());
			}
			return coveredTerms;
		} catch (UIMAException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Multiset<String> findCoveredTermsAndConcepts(String text) {
		try {
			jCas.reset();
			jCas.setDocumentText(text);
			CAS cas = jCas.getCas();

			bioPortalGazetteerAE.process(cas);

			Multiset<String> coveredTerms = HashMultiset.create();
			FSIterator<Annotation> it = jCas.getAnnotationIndex(
					OntClassMention.type).iterator();
			int begin = -1;
			int end = -1;
			while (it.hasNext()) {
				OntClassMention ontClassMention = (OntClassMention) it.next();
				String term = null;
				List<String> ids = null;
				if (begin != ontClassMention.getBegin()
						|| end != ontClassMention.getEnd()) {
					begin = ontClassMention.getBegin();
					end = ontClassMention.getEnd();
					List<OntClassMention> allClassesAtPosition = JCasUtil
							.selectCovered(jCas, OntClassMention.class,
									ontClassMention);
					if (null != allClassesAtPosition
							&& !allClassesAtPosition.isEmpty()) {
						ids = new ArrayList<>();
						for (OntClassMention mention : allClassesAtPosition) {
							if (term == null)
								term = mention.getCoveredText();
							ids.add(mention.getSpecificType());
						}
					}
				}
				if (term != null && ids != null) {
					coveredTerms.add(term + "\t" + StringUtils.join(ids, "||"));
				}
			}
			return coveredTerms;
		} catch (UIMAException e) {
			e.printStackTrace();
		}
		return null;
	}

}
