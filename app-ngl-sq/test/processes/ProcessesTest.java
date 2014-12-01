package processes;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;

import play.libs.Json;
import play.mvc.Result;
import scala.Array;
import utils.AbstractTests;
import utils.InitDataHelper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cea.ig.MongoDBDAO;

public class ProcessesTest extends AbstractTests{
	
	
	static String processCode="";
	
	@BeforeClass
	public static void initData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		InitDataHelper.initForProcessesTest();
	}
	
	@AfterClass
	public static void resetData(){
		InitDataHelper.endTest();
	}
	
	@Test
	public void save() throws JsonParseException, JsonMappingException, IOException{
		Process process = ProcessTestHelper.getFakeProcess("sequencing", "illumina-run");
		String supportCode = InitDataHelper.getSupportCodesInContext("flowcell-8").get(0);
		ContainerSupport cs = MongoDBDAO.findOne(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", supportCode));
		process.projectCode = cs.projectCodes.get(0);
		process.sampleCode = cs.sampleCodes.get(0);
		process.properties = new HashMap<String, PropertyValue>();
		process.properties.put("sequencingType", new PropertySingleValue("Hiseq 2000"));
		process.properties.put("readType", new PropertySingleValue("PE"));
		process.properties.put("sequencerType", new PropertySingleValue("HISEQ2000"));
		process.properties.put("readLength", new PropertySingleValue("100"));
		Result result = callAction(controllers.processes.api.routes.ref.Processes.saveSupport(supportCode),fakeRequest().withJsonBody(Json.toJson(process)));
		
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		ObjectMapper mapper = new ObjectMapper();
		Process processResult = mapper.readValue(play.test.Helpers.contentAsString(result), Process.class);
		assertThat(processResult).isNotNull();
		assertThat(processResult.state.code).isEqualTo("N");
		
		Process processFind =MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, processResult.code);
		assertThat(processResult.code).isEqualTo(processFind.code);
		
		Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, processResult.containerInputCode);
		assertThat(container.processTypeCode).isEqualTo(processResult.typeCode);
		assertThat(container.inputProcessCodes.get(0)).isEqualTo(processResult.code);
		assertThat(container.state.code).isEqualTo("A");
		assertThat(container.fromExperimentTypeCodes).isNotNull();
		ContainerSupport containerSupport=MongoDBDAO.findByCode(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, container.support.code);
		assertThat(containerSupport.state.code).isEqualTo("A");
		assertThat(containerSupport.fromExperimentTypeCodes).isNotNull();
		assertThat(container.inputProcessCodes.get(0)).isEqualTo(processResult.code);
		
		//result = callAction(controllers.processes.api.routes.ref.Processes.update(processResult.code),fakeRequest().withJsonBody(Json.toJson(processResult)));
		//assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
		
		result = callAction(controllers.processes.api.routes.ref.Processes.get(processResult.code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);

		result = callAction(controllers.processes.api.routes.ref.Processes.delete(processResult.code),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
	}
	
	@Test
	public void updateNotFound(){
		Result result = callAction(controllers.processes.api.routes.ref.Processes.update("not_found"),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
	}
	
	
	@Test
	public void deleteNotFound(){
		Result result = callAction(controllers.processes.api.routes.ref.Processes.delete("not_found"),fakeRequest());
		assertThat(status(result)).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
	}
	
	
}
