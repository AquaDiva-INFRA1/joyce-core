# JOYCE Core

For a general overview over the JOYCE project and instructions how to install and run JOYCE, please refer to the README.md file of the joyce repository.

The JOYCE Core is a rather small project and heavily relies on JOYCE Base. The Core contains services for the modularization of ontologies and the scoring of ontologies and modules.

It also contains the concept tagging service that is explained below.

## de.aquadiva.joyce.core.services.ConceptTaggingService

The concept tagging service relies on the [jcore-lingpipegazetteer-ae](https://github.com/JULIELab/jcore-base/tree/master/jcore-lingpipegazetteer-ae/) UIMA component to recognize ontology classes in an input text. This is used to determine the classes for which modules should be selected when running JOYCE.
The JCoRe component requires a configuration file that is automatically created during the SetupService phase of JOYCE. The path to the configuration is given by the `joyce.ontologies.concepts.gazetteer.conffile` configuration symbol. However, there are default values that don't need to be changed most of the time.
The configuration file also points to the concept name dictionary which is used to find concept mentions in text.
