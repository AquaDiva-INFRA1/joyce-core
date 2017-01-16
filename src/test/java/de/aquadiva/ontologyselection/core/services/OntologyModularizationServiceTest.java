package de.aquadiva.ontologyselection.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.aquadiva.ontologyselection.base.data.Ontology;
import de.aquadiva.ontologyselection.base.data.OntologyModule;
import de.aquadiva.ontologyselection.base.services.IOWLParsingService;
import de.aquadiva.ontologyselection.util.OntologyModularizationException;

public class OntologyModularizationServiceTest {

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
	public void testModularizeDirectory() throws OntologyModularizationException {
		IOntologyModularizationService service = registry.getService(IOntologyModularizationService.class);
		File dir = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "owl");
		File[] list = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".DS_Store");
			}
		});
		for (File owlFile : list) {
			try {
				// TODO the modularization creates a lot of files right into the
				// project root directory. Please don't do this
				Ontology o = new Ontology();
				o.setFile(owlFile.getAbsoluteFile());
				o.setId(owlFile.getName());
				List<OntologyModule> modules = service.modularize(o);
				System.out.println(modules.size());
			} catch (Exception e) {
				System.out.println(owlFile.getName() + " " + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	@Test
	public void testModularizeSingleFile() throws Exception {
		IOntologyModularizationService service = registry.getService(IOntologyModularizationService.class);
		File owlFile = new File("src/test/resources/owl/ENVO.owl.gz");
		Ontology o = new Ontology();
		o.setFile(owlFile);
		o.setId(owlFile.getName());
		List<OntologyModule> modules = service.modularize(o);
		System.out.println(modules.size());

	}

}
