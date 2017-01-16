package de.aquadiva.ontologyselection.core.services;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;

import de.aquadiva.ontologyselection.base.services.IConstantOntologyScorer;
import de.aquadiva.ontologyselection.base.services.IVariableOntologyScorer;
import de.aquadiva.ontologyselection.base.services.JoyceBaseModule;
import de.aquadiva.ontologyselection.core.services.ActiveCommunityScorer.ActiveCommunity;
import de.aquadiva.ontologyselection.core.services.ClassCoverageScorer.ClassCoverage;
import de.aquadiva.ontologyselection.core.services.ClassOverheadScorer.ClassOverhead;
import de.aquadiva.ontologyselection.core.services.ClassOverlapScorer.ClassOverlap;
import de.aquadiva.ontologyselection.core.services.DescriptivityScorer.Descriptivity;
import de.aquadiva.ontologyselection.core.services.PopularityScorer.Popularity;
import de.aquadiva.ontologyselection.core.services.RichnessScorer.Richness;
import de.aquadiva.ontologyselection.core.services.UpToDateScorer.UpToDate;

@SubModule(value = { JoyceBaseModule.class })
public class JoyceCoreModule {
	@SuppressWarnings("unchecked")
	public static void bind(ServiceBinder binder) {
		binder.bind(IConceptTaggingService.class, ConceptTaggingService.class);
//		binder.bind(IConceptDictionaryAdaptionService.class, ConceptDictionaryAdaptionService.class);
		binder.bind(IOntologyModularizationService.class, OntologyModularizationService.class);
		
		/*
		 * Scorers
		 */
		binder.bind(IConstantOntologyScorer.class, PopularityScorer.class).withMarker(Popularity.class).withSimpleId();
		binder.bind(IConstantOntologyScorer.class, RichnessScorer.class).withMarker(Richness.class).withSimpleId();
		binder.bind(IConstantOntologyScorer.class, UpToDateScorer.class).withMarker(UpToDate.class).withSimpleId();
		binder.bind(IConstantOntologyScorer.class, DescriptivityScorer.class).withMarker(Descriptivity.class).withSimpleId();
		binder.bind(IConstantOntologyScorer.class, ActiveCommunityScorer.class).withMarker(ActiveCommunity.class).withSimpleId();
		
		binder.bind(IVariableOntologyScorer.class, ClassCoverageScorer.class).withMarker(ClassCoverage.class).withSimpleId();
		binder.bind(IVariableOntologyScorer.class, ClassOverheadScorer.class).withMarker(ClassOverhead.class).withSimpleId();
		binder.bind(IVariableOntologyScorer.class, ClassOverlapScorer.class).withMarker(ClassOverlap.class).withSimpleId();
	}
}
