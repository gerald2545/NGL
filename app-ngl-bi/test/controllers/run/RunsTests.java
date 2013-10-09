package controllers.run;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.Test;

import play.Logger;
import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import fr.cea.ig.MongoDBDAO;

public class RunsTests extends AbstractTests {
	
	@Test
	public void testRunSave() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
		    	   
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code","ReadSet00"));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}	
		
			Run run = RunMockHelper.newRun("YANN_TEST1");
			Lane lane = RunMockHelper.newLane(1);
 					
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			run.lanes = lanes;
	
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
			
	        
			ReadSet r = RunMockHelper.newReadSet("ReadSet00");		
			r.runCode = run.code;
			r.laneNumber = lane.number;
	        result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
	        assertThat(status(result)).isEqualTo(OK);
	        
		    //query for control
	        run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
	        assertThat(run.lanes.size()).isEqualTo(1);
	        assertThat(run.lanes.get(0).number).isEqualTo(1);
	        
	        r = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code",r.code));
	        assertThat(r.code).isEqualTo("ReadSet00");
	        assertThat(r.runCode).isEqualTo(run.code);
	        assertThat(r.laneNumber).isEqualTo(lane.number);
	        
	        
		}});
    }

	@Test
	public void testRunUpdate() {
		// change the run dispatch value : false to true
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {		
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
				
			Run run = RunMockHelper.newRun("YANN_TEST1");
			Lane lane = RunMockHelper.newLane(1);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			run.lanes = lanes;
			
			run.dispatch=false;
			
		 	Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		 	assertThat(status(result)).isEqualTo(OK);
	        
	        run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
	        assertThat(run.dispatch).isEqualTo(false);
	        
	        run.dispatch=true;
			result = callAction(controllers.runs.api.routes.ref.Runs.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		 	Logger.debug(contentAsString(result));
		 	assertThat(status(result)).isEqualTo(OK);
		 	
		 	//query for control
		 	run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
		 	assertThat(run.dispatch).isEqualTo(true);
		 	
		}}); 
	}
	
	@Test
	public void testRunSaveWithTwiceSameReadSet() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST2"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code","ReadSet2"));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}			
			
			Run run = RunMockHelper.newRun("YANN_TEST2");
			Lane lane = RunMockHelper.newLane(1);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			run.lanes = lanes;
		
			ReadSet r = RunMockHelper.newReadSet("ReadSet2");
			r.runCode = run.code;
			r.laneNumber = lane.number;
			
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
        
			result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
	        assertThat(status(result)).isEqualTo(OK);
	        
	        // update readSetCodes
			List<String> a = new ArrayList<String>();
			a.add(r.code);
			a.add(r.code);
			run.lanes.get(0).readSetCodes = a;
			
			//insert run with the readset r twice
			result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);			
	        
			result = callAction(controllers.runs.api.routes.ref.Runs.update(run.code),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
	        assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);	
	        
		 	//query for control
		 	run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		 	assertThat(run.lanes.get(0).readSetCodes.size()).isEqualTo(1);
		 	assertThat(run.lanes.get(0).readSetCodes.get(0)).isEqualTo(r.code);
	        
	        
		     }}); 
	}
	
	@Test
	public void testPropertyLaneUpdate() {
		// verify that the property "valid" of the lane is update to TRUE
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			Run run = RunMockHelper.newRun("YANN_TEST1");
			Lane lane = RunMockHelper.newLane(1);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			run.lanes = lanes;
			
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
	        
			lane.valid = TBoolean.TRUE;
			
			result = callAction(controllers.runs.api.routes.ref.Lanes.update(run.code, lane.number),fakeRequest().withJsonBody(RunMockHelper.getJsonLane(lane)));
			assertThat(status(result)).isEqualTo(OK);
			
			
		 	//query for control
		 	run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
		 	assertThat(lane.valid).isEqualTo(TBoolean.TRUE);
		 	
		     }}); 
	}
	

	@Test
	public void testPropertyReadSetUpdate() { 
		// verify that the property "dispatch" of the readSet is update to false
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
			Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
			if(runDelete!=null){
				MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
			}
			ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code","ReadSet01"));
			if(readSetDelete!=null){
				MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetDelete._id);
			}
			Run run = RunMockHelper.newRun("YANN_TEST1");
			Lane lane = RunMockHelper.newLane(1);
			List<Lane> lanes = new ArrayList<Lane>();
			lanes.add(lane);
			run.lanes = lanes;

			ReadSet r = RunMockHelper.newReadSet("ReadSet01");
			r.runCode = run.code;
			r.laneNumber = lane.number;
			
			Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
			assertThat(status(result)).isEqualTo(OK);
			
			result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
			assertThat(status(result)).isEqualTo(OK);
			
	        r = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code",r.code));
	        assertThat(r._id).isNotEqualTo(null);

	        r.dispatch = false;
			
			result = callAction(controllers.readsets.api.routes.ref.ReadSets.update(r.code),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(r)));
	        assertThat(status(result)).isEqualTo(OK);

		 	//query for control
	        r = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code",r.code));
	        assertThat(r.dispatch).isEqualTo(false);
		 	
		     }}); 
	}
	
	@Test
	public void testDeleteRun(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		     public void run() {
				Result result;
				Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","YANN_TEST1"));
				if(runDelete!=null){
					MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
				}
				Run run = RunMockHelper.newRun("YANN_TEST1");
				Lane lane = RunMockHelper.newLane(1);
				List<Lane> lanes = new ArrayList<Lane>();
				lanes.add(lane);
				run.lanes = lanes;
				
				result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
				assertThat(status(result)).isEqualTo(OK);
		
				result = callAction(controllers.runs.api.routes.ref.Runs.delete(run.code),fakeRequest());
				assertThat(status(result)).isEqualTo(OK);
				
			 	//query for control
			 	run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code",run.code));
			 	assertThat(run).isNull();
		     }}); 		
	}


}
