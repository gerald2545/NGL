package sampleload;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import models.laboratory.container.instance.Container;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.mvc.Result;
import play.test.Helpers;
import utils.AbstractTests;
import fr.cea.ig.MongoDBDAO;

public class SampleLoadTest extends AbstractTests {

	public static SampleType sampleType;
	public static ImportType importType;
	public static String fileName;
	
	@BeforeClass
	public static void initData() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		 app = getFakeApplication();
		 Helpers.start(app);
		
		sampleType=SampleType.find.findByCode("BAC");
		importType=ImportType.find.findByCode("library");		
		
		fileName="./app-ngl-sq/test/sampleload/dataFile/sample_type.csv";		
	}
	
	@AfterClass
	public static void removeData() throws DAOException{
		 app = getFakeApplication();

		FileUtils.deleteQuietly(new File(fileName));
		
		MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, "A")); 
		MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME,MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, "AAA_A1") );
		
		Helpers.stop(app);
	}
	
	//@Test
	public void updaLoadDataFromCSVFileNotFound(){

		Map<String, String> data = new HashMap<String, String>();
		data.put("sampleType", sampleType.code);
		data.put("importType", importType.code);
		data.put("fileName", "./test_file");

		Result result = callAction(controllers.dataload.routes.ref.SampleLoad.uploadDataFromCSVFile(),fakeRequest().withFormUrlEncodedBody(data));

		assertThat(status(result)).isEqualTo(BAD_REQUEST);
		
	}


	//@Test
	public void importTypeNotFound(){

		Map<String, String> data = new HashMap<String, String>();
		data.put("sampleType", sampleType.code);
		data.put("importType", "");
		data.put("fileName", fileName);

		Result result = callAction(controllers.dataload.routes.ref.SampleLoad.uploadDataFromCSVFile(),fakeRequest().withFormUrlEncodedBody(data));

		//System.out.println(contentAsString(result));
		assertThat(status(result)).isEqualTo(BAD_REQUEST);
	}



	//@Test
	public void sampleTypeNotFound(){

		Map<String, String> data = new HashMap<String, String>();
		data.put("sampleType", "");
		data.put("importType", "importBanqueSolexa");
		data.put("fileName", fileName);

		Result result = callAction(controllers.dataload.routes.ref.SampleLoad.uploadDataFromCSVFile(),fakeRequest().withFormUrlEncodedBody(data));

		assertThat(status(result)).isEqualTo(BAD_REQUEST);
		
		result = callAction(controllers.dataload.routes.ref.SampleLoad.createTemplateFile(),fakeRequest().withFormUrlEncodedBody(data));
		assertThat(status(result)).isEqualTo(BAD_REQUEST);
	}


	//@Test
	public void createTemplateCSV(){

		Map<String, String> data = new HashMap<String, String>();
		data.put("sampleType",sampleType.code);
		data.put("importType", importType.code);	
		data.put("fileName", fileName);

		//generer le fichier fileName
		Result result = callAction(controllers.dataload.routes.ref.SampleLoad.createTemplateFile(),fakeRequest().withFormUrlEncodedBody(data));

		File file=new File(data.get("fileName"));
		
		assertThat(file.exists()).isEqualTo(true);
		assertThat(status(result)).isEqualTo(OK);
		try {
			assertThat(FileUtils.readLines(file).size()).isEqualTo(1);
		} catch (IOException e) {
		}
		
	}

	//@Test
	public void uploadDataFromCSVFile() throws IOException{
		Map<String, String> data = new HashMap<String, String>();
		data.put("sampleType", sampleType.code);
		data.put("importType", importType.code);
		data.put("fileName", fileName);

		//TODO
		String line="A;AAA;Sample Test;ref collab;1;Comment sample;1000;;;;AAA_A1;TUBE;comment container;VIDE;1;1;123456;IND1;1.1;1.2;1.3;01/01/2000";

		FileWriter fw = new FileWriter(fileName,true);
		
        BufferedWriter fbw = new BufferedWriter(fw);
        fbw.newLine();
        fbw.write(line);
        fbw.close();
		
		Result result = callAction(controllers.dataload.routes.ref.SampleLoad.uploadDataFromCSVFile(),fakeRequest().withFormUrlEncodedBody(data));
		assertThat(status(result)).isEqualTo(OK);
		assertThat(MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, "AAA_A1")).isNotNull();
		Sample sample =MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, "A");
		assertThat(sample).isNotNull();
		assertThat(sample.projectCodes.get(0)).isEqualTo("AAA");

	}
	

}
