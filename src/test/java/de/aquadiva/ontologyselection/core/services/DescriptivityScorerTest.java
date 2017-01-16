package de.aquadiva.ontologyselection.core.services;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.aquadiva.ontologyselection.base.data.Ontology;
import de.aquadiva.ontologyselection.base.data.OntologyModule;

public class DescriptivityScorerTest {

	private static Registry registry;

	@BeforeClass
	public static void setup() {
		registry = RegistryBuilder.buildAndStartupRegistry(OSCoreModule.class);
	}

	@AfterClass
	public static void shutdown() {
		registry.shutdown();
	}
	
	@Test
	public void testDescripitivity() {
		Ontology o = new Ontology();
		o.setFile(new File("src/test/resources/obi.owl")); 
		String s="src/test/resources/obi.owl";
		assertTrue(o.getFile().exists());
		System.out.println("the ontology name---"+s);
		DescriptivityScorer DS=new DescriptivityScorer();
		DS.score(o);
		
		
	}

}
