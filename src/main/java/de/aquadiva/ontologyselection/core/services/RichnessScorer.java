package de.aquadiva.ontologyselection.core.services;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;

import de.aquadiva.ontologyselection.base.data.IOntology;
import de.aquadiva.ontologyselection.base.data.IOntologySet;
import de.aquadiva.ontologyselection.base.data.ScoreType;
import de.aquadiva.ontologyselection.base.services.IConstantOntologyScorer;
import de.aquadiva.ontologyselection.base.services.IOWLParsingService;

public class RichnessScorer implements IConstantOntologyScorer {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Richness {
		//
	}

	private Logger log;
	private IOWLParsingService parsingService;

	public RichnessScorer(Logger log, IOWLParsingService parsingService) {
		this.log = log;
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
		}
		// o.getOWLOntology();
		OWLDataFactory df = manager.getOWLDataFactory();
		double score = computeScore(ow, df);
		// calculate and set the score
		o.setScore(ScoreType.OBJECT_PROPERTY_RICHNESS,
				Double.valueOf((double) score));
		log.trace("the richness score---" + score);

	}

	private double computeScore(OWLOntology ow, OWLDataFactory df) {
		int NumSC = 0, NumDS = 0, NumES = 0, NumID = 0, AP = 0;
		// OWLReasonerFactory reasonerFactory=new
		// Reasoner.ReasonerFactory();//new OWLReasonerFactory();
		// OWLReasoner r = reasonerFactory.createReasoner(ow);
		Set<OWLObjectProperty> op = ow.getObjectPropertiesInSignature();
		Set<OWLClass> oc = ow.getClassesInSignature();
		Set<OWLNamedIndividual> oi = ow.getIndividualsInSignature();
		for (OWLClass cls : ow.getClassesInSignature()) {
			Set<OWLClassExpression> cset = cls.getSubClasses(ow);
			if (cset != null)
				NumSC += cset.size();
			Set<OWLClassExpression> dset = cls.getDisjointClasses(ow);
			if (dset != null)
				NumDS += dset.size();
			Set<OWLClassExpression> eset = cls.getDisjointClasses(ow);
			if (eset != null)
				NumES += eset.size();
			Set<OWLClassAssertionAxiom> iset = ow.getClassAssertionAxioms(cls);
			if (iset.size() > 0) {
				NumID++;
				AP += iset.size();
			}
		}
		double RR = (NumDS + NumES) / (NumDS + NumES + NumSC * 1.0);
		double AR = op.size() / (oc.size() * 1.0);
		double CR = NumID / (oc.size() * 1.0);
		double APR = AP / (oc.size() * 1.0);
		// System.out.println(RR+
		// "---"+AR+"---the number of subclasses---"+CR+"---"+APR);
		double score = 0.25 * RR + 0.25 * AR + 0.25 * CR + 0.25 * APR;
		return score;
	}

	@Override
	public void score(IOntologySet s) {
		// TODO Auto-generated method stub

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

					if (!o.isSetScore(ScoreType.OBJECT_PROPERTY_RICHNESS)) {
						this.score(o);
					}

					// there is no ontology having no classes assigned to it
					if (o.getClassIds() != null) {

						sumOfWeightedScores += o.getScore(
								ScoreType.OBJECT_PROPERTY_RICHNESS)
								.doubleValue()
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
