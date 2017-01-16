package de.aquadiva.ontologyselection.core.services;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.aquadiva.ontologyselection.base.data.Ontology;

public class RichnessScorerTest {
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
	public void testRichness() {
		Ontology o = new Ontology();
		o.setFile(new File("src/test/resources/obi.owl")); 
		String s="src/test/resources/obi.owl";
		assertTrue(o.getFile().exists());
		System.out.println("the ontology name---"+s);
		RichnessScorer DS=new RichnessScorer();
		DS.score(o);
		
		
	}

}
