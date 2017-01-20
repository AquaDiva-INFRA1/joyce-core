package de.aquadiva.joyce.core.services;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Multiset;

import de.aquadiva.joyce.JoyceSymbolConstants;
import de.aquadiva.joyce.core.services.IConceptTaggingService;
import de.aquadiva.joyce.core.services.JoyceCoreModule;

public class ConceptTaggingServiceTest {

	private static Registry registry;

	@BeforeClass
	public static void setup() {
		registry = RegistryBuilder.buildAndStartupRegistry(JoyceCoreModule.class);
	}

	@AfterClass
	public static void shutdown() {
		registry.shutdown();
	}

	@Test
	public void testConceptTagging() {
		// Set the test configuration for the gazetteer so we may work with a
		// small test dictionary
		System.setProperty(JoyceSymbolConstants.GAZETTEER_CONFIG, "bioportal.gazetteer.test.properties");
		IConceptTaggingService service = registry.getService(IConceptTaggingService.class);

		String input = "I have no idea what an accessory lung might be. Perhaps it has some connection with accessory mamillas or accessory mammary tissue? But perhaps an accessory lung is something else.";
		Multiset<String> foundConceptIds = service.findConcepts(input);
		System.out.println(input + "the number of concepts:\t" + foundConceptIds.size());
		Iterator<String> it = foundConceptIds.iterator();
		while (it.hasNext()) {
			String term = (String) it.next();
			System.out.println("the concepts:\t" + term);

		}
		assertEquals(8, foundConceptIds.size());
		// IDs for "accessory lung"; all have been found twice because the
		// concept is mentioned twice in the test
		// string.
		assertEquals(2, foundConceptIds.count("http://purl.bioontology.org/ontology/RCD/P86y0"));
		assertEquals(2, foundConceptIds.count("http://purl.bioontology.org/ontology/SNMI/D4-28400"));
		assertEquals(2, foundConceptIds.count("http://purl.bioontology.org/ontology/SNOMEDCT/52579008"));
		// IDs for "acessory mamilla(s)
		assertEquals(1, foundConceptIds.count("http://purl.obolibrary.org/obo/HP_0002558"));
		// IDs for "acessory mammary tissue
		assertEquals(1, foundConceptIds.count("http://purl.obolibrary.org/obo/DERMO_0003491"));
	}

}
