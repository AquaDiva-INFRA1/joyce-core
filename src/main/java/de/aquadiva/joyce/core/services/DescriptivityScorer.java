package de.aquadiva.joyce.core.services;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.aquadiva.joyce.base.data.IOntology;
import de.aquadiva.joyce.base.data.IOntologySet;
import de.aquadiva.joyce.base.data.ScoreType;
import de.aquadiva.joyce.base.services.IConstantOntologyScorer;
import de.aquadiva.joyce.base.services.IOWLParsingService;

public class DescriptivityScorer implements IConstantOntologyScorer {

	private IOWLParsingService parsingService;

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Descriptivity {
		//
	}
	
	public DescriptivityScorer(IOWLParsingService parsingService) {
		this.parsingService = parsingService;
	}

	public void score(IOntology o) {
		// TODO Auto-generated method stub
		File sss = o.getFile();
		OWLOntologyManager manager = parsingService.getOwlOntologyManager();

		OWLOntology ow = o.getOwlOntology();
		if (null == ow) {
			try {
				parsingService.parse(o);
				ow = o.getOwlOntology();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			try {
//				
//				ow = manager.loadOntologyFromOntologyDocument(sss);
//			} catch (OWLOntologyCreationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		OWLDataFactory df = manager.getOWLDataFactory();
		Set<OWLClass> classes = ow.getClassesInSignature();
		int count = 0;
		for (OWLClass cls : ow.getClassesInSignature()) {
			// Get the annotations on the class that use the label/comment
			// property
			for (OWLAnnotation annotation : cls.getAnnotations(ow,
					df.getRDFSComment())) {
				if (annotation.getValue() instanceof OWLLiteral) {
					count++;

				}
			}
		}

		double score = count / (classes.size() * 1.0);
		// calculate and set the score
		o.setScore(ScoreType.CLASS_DESCRIPTIVITY,
				Double.valueOf((double) score));
		System.out.println("the descriptivity score---" + score);

	}

	@Override
	public void score(IOntologySet s) {
		// TODO Auto-generated method stub
		// the ontology set is not null
		if (s != null) {

			Double score = ScorerUtil.getSetScore(s.getOntologies(),
					ScoreType.CLASS_DESCRIPTIVITY, this);

			// score is not null
			if (score != null) {

				// calculate and set the score
				s.setScore(ScoreType.CLASS_DESCRIPTIVITY, Double.valueOf(score));

			}

		}
	}

	@Override
	public Double getScoreAdded(IOntologySet s, IOntology o) {

		if (s != null && o != null && s.getOntologies() != null) {

			// it's not worth the effort for this kind of score, just use
			// this.score(IOntologySet s)
			HashSet<IOntology> ontologies = new HashSet<IOntology>();
			ontologies.addAll(s.getOntologies());
			ontologies.add(o);

			return this.getSetScore(ontologies);

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

			return this.getSetScore(ontologies);
		} else {
			return null;
		}

	}

	private Double getSetScore(HashSet<IOntology> s) {
		double sumOfWeightedScores = 0.0;
		double sumOfWeights = 0.0;

		// set is not null and contains ontologies
		if (s != null && s.size() != 0) {

			for (IOntology o : s) {

				// there's no null ontology
				if (o != null) {

					if (!o.isSetScore(ScoreType.CLASS_DESCRIPTIVITY)) {
						this.score(o);
					}

					// there is no ontology having no classes assigned to it
					if (o.getClassIds() != null) {

						sumOfWeightedScores += o.getScore(
								ScoreType.CLASS_DESCRIPTIVITY).doubleValue()
								* (double) o.getClassIds().size();
						sumOfWeights += o.getClassIds().size();

					} else {
						return null;
					}

				} else {
					return null;
				}

			}

			return Double.valueOf(sumOfWeightedScores / sumOfWeights);

		} else {
			return null;
		}
	}

}
