package de.aquadiva.joyce.core.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.zip.GZIPInputStream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;

import de.aquadiva.joyce.base.data.Ontology;
import fusion.oapt.algorithm.merger.genericMerge.MergeEvaluation;
import fusion.oapt.algorithm.merger.genericMerge.mappingMerge;
import fusion.oapt.algorithm.merger.genericMerge.simpleMerge;
import fusion.oapt.algorithm.merger.graphMerge.GraphMerge;
import fusion.oapt.algorithm.merger.matchingMerge.MatchingMerge;


public class OntologyMergeService  implements IOntologyMergeService{
	
	private ExecutorService executor;
	private Logger log;
	private List<File> filesInFolder;
	public OntologyMergeService()
	{
		filesInFolder=new ArrayList<File>();
	}
	public OntologyMergeService(Logger log, ExecutorService executor) {
		this.log = log;
		this.executor = executor;
		filesInFolder=new ArrayList<File>();
	}
	
	public Ontology merge(Ontology o1, Ontology o2)
	{
		return null;
	}
	public Ontology merge(String path)
	{
		try {
			readFiles(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		OWLOntology ontology=null;;
		try {
			ontology = run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Ontology onto = new Ontology();
		 onto.setOwlOntology(ontology);
		return onto;
	}


	
	public   void readFiles(String path) throws IOException  
	{
		File directory = new File(path);
	    File[] fList = directory.listFiles();
		for (File file : fList)
		{
		   if (file.isFile() && file.getName().endsWith(".owl")){
		     	filesInFolder.add(file);
		     	  }
		  }
    System.out.println("the total number number of owl files:\t"+filesInFolder.size());
	}
	
	
	public OWLOntology run() throws Exception
	{
		int k=2;
		String loc="temp/merge.owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology owl=manager.createOntology();
		MergeEvaluation ME;
		switch(k)
		{
		case 1:
			simpleMerge SM=new simpleMerge();
			owl=SM.merge(filesInFolder,loc);
			saveOntology(owl,loc);
			break;
		case 2:
			 //execute_parallel();
			  GraphMerge GM=new GraphMerge(filesInFolder);
			  String model=GM.SetMerge(); 
			  owl=saveOntologyModel(model,loc);
			  ME=new MergeEvaluation(filesInFolder,owl);
			  ME.computeCoverage_New();
			  int map=GM.getNoMaps();
			  ME.computeRedudency(map);
			  break;
		case 3:
			 mappingMerge MM=new mappingMerge(filesInFolder);
			 owl=MM.run();
			 saveOntology(owl,loc);
			break;
		case 4:
			MatchingMerge mm=new MatchingMerge(filesInFolder);
			owl=mm.run();
			saveOntology(owl,loc);
			break;
		default:
			System.out.println("please enter a value between 1 and 4");
		}
		/* File file=new File("resources/merge/temp");
			for(File f: file.listFiles())
			        f.delete();*/
		  File file1=new File("resources/mergeTemp");
			for(File f: file1.listFiles())
			        f.delete();
	return owl;
	}
	public  void saveOntology(OWLOntology ontology, String loc) throws Exception {
	       // Get hold of an ontology manager
	       OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	       File file = new File(loc);
	       RDFXMLOntologyFormat owlxmlFormat = new RDFXMLOntologyFormat();
	       manager.saveOntology(ontology, owlxmlFormat, IRI.create(file.toURI()));
	      
	   }
	
	public OWLOntology saveOntologyModel(String model, String loc) throws IOException, OWLOntologyCreationException
	{
		InputStream in=null;
    	InputStream fileStream=null;
    	if(model.endsWith(".rdf"))  return null;
    	if(model.endsWith(".owl"))
    	 		in = new FileInputStream(model);
    	else if(model.endsWith(".gz"))
    		{
    			fileStream = new FileInputStream(new File(model));
    			in= new GZIPInputStream(fileStream);
    		}
    	 OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    	 OWLOntology owl;
		 owl = manager.loadOntologyFromOntologyDocument(in);
		System.out.println(owl.getAxiomCount()+"\t The OWL ontology \t " +model);
		return owl;
	}


}
