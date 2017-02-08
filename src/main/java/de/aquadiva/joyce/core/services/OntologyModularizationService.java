package de.aquadiva.joyce.core.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;

/*import FUSION.OPT.algorithm.SeeCOnt.Findk.FindOptimalCluster;
import FUSION.OPT.algorithm.SeeCOnt.Cluster;
import FUSION.OPT.general.cc.Controller;*/
import fusion.oapt.algorithm.partitioner.SeeCOnt.Findk.FindOptimalCluster;
import fusion.oapt.general.cc.Controller;
import org.apache.jena.ontology.OntModel;

import de.aquadiva.joyce.base.data.Ontology;
import de.aquadiva.joyce.base.data.OntologyModule;
import de.aquadiva.joyce.util.OntologyModularizationException;

public class OntologyModularizationService implements
		IOntologyModularizationService {

	private ExecutorService executor;
	private Logger log;

	public OntologyModularizationService(Logger log, ExecutorService executor) {
		this.log = log;
		this.executor = executor;
	}

	@Override
	public List<OntologyModule> modularize(Ontology o)
			throws OntologyModularizationException {
		ModularizationWorker worker = new ModularizationWorker(o);
		Future<List<OntologyModule>> modulesFuture = executor.submit(worker);
		try {
			// TODO raise time and write out which ontology took how long
			return modulesFuture.get(120, TimeUnit.DAYS);
		} catch (InterruptedException | ExecutionException e) {
			throw new OntologyModularizationException(
					"Exception happened during modularization: "
							+ e.getMessage(), e);
		} catch (TimeoutException e) {
			log.debug(
					"Modularization of ontology {} timed out, no modules are created for this ontology.",
					o.getId());
			modulesFuture.cancel(true);
			throw new OntologyModularizationException(
					"Modularization of ontology "
							+ o.getId()
							+ " timed out, no modules are created for this ontology.",
					e);
		}
	}

	private class ModularizationWorker implements
			Callable<List<OntologyModule>> {

		private Ontology o;

		public ModularizationWorker(Ontology o) {
			this.o = o;
		}

		@Override
		public List<OntologyModule> call() throws Exception {
			long time = System.currentTimeMillis();
			log.debug("Modularizing ontology {} in thread {}", o.getId(), Thread.currentThread().getName());
			String sss = o.getFile().toString();
			;
			@SuppressWarnings("static-access")
			int n = FindOptimalCluster.FindOptimalClusterFunc(sss);
			log.debug("the number of modules\t{}", n);
			deleteFile();
			Controller cn = null;
			try {
				cn = new Controller(sss);
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
//				e1.printStackTrace();
				throw new OntologyModularizationException("Modularization of ontology " + o.getId() + " failed because the Controller could not be created.", e1);
			}
			int size=cn.getOntModel1().listClasses().toList().size();
			System.out.println("the number of concepts\t"+size);
			List<OntologyModule> list = null;
			if(size>0 && n > 0){
			cn.InitialRun(n, "SeeCOnt");
			//HashMap<Integer, Cluster> sc = cn.getClustes();
			//ArrayList<OntModel> models = cn.getModules();
			String path = "temp";
			File dir = new File(path);
			File[] files = dir.listFiles();
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			list = new ArrayList<OntologyModule>();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				try {
					OWLOntology ontology = manager
							.loadOntologyFromOntologyDocument(file);
					String ids = file.toString();
					log.debug(ids.substring(ids.lastIndexOf("\\") + 1)+"\t the file\t"+ids);
					byte[] moduleData = null;
					try (InputStream is = new FileInputStream(file)) {
						// convert the ontology data into GZIP format
						moduleData = IOUtils.toByteArray(is);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						GZIPOutputStream os = new GZIPOutputStream(baos);
						IOUtils.write(moduleData, os);
						moduleData = baos.toByteArray();
					}

					/*OntologyModule m = o.createModule(

					OntologyModule m = o.createStaticModule(

							ids.substring(ids.lastIndexOf("\\") + 1),
							moduleData);*/
					//m.setOwlOntology(ontology);
					 Ontology onto = new Ontology();
					 onto.setOwlOntology(ontology);
					 byte[] ondata = onto.getOntologyData();
					 OntologyModule m = new OntologyModule();

					 //database storage (at the very least).
					 m.setOwlOntology(onto.getOwlOntology());
					 m.setOntologyData(ondata);

					// the module ID
					 m.setId(ids.substring(ids.lastIndexOf("\\") + 1));
					 m.setFile(file);

					 m.setSourceOntology(o);
					 o.addModule(m);
					// System.out.println("---\t "+m.getOwlOntology());
					list.add(m);
				} catch (OWLOntologyCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			time = System.currentTimeMillis() - time;
			log.info("Modularization of ontology {}has been done! Took {}ms ({}s)", new Object[] {o.getId(), time, time / 1000});
			deleteFile();}
			return list;
		}

	}

	public void deleteFile() {
		String path = "temp";
		File dir = new File(path);
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				f.delete();
				// System.out.println(f.toString()+"successfully deleted");
			} else {
				System.out.println("cant delete a file due to open or error");
			}
		}
	}

	}


