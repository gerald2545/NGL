package models.instances.validation;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.Test;

import play.mvc.Result;
import utils.AbstractTests;
import utils.RunMockHelper;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import static validation.utils.ValidationConstants.*;


public class TreatmentValidationTest extends AbstractTests {
	
	
	private void deleteRdCode() {
		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","DIDIER_TESTFORTRT"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,readSetDelete._id);
		}
	}	
	
	private Treatment getNewTreatmentForReadSet() {
		Treatment t = new Treatment();
		t.code =  "ngsrg";		
		t.typeCode = "ngsrg-illumina";
		t.categoryCode = "ngsrg";
		//define map of single property values
		Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
		m.put("nbCluster", new PropertySingleValue(100)); // valeur simple
		m.put("nbBases", new PropertySingleValue(100));
		m.put("fraction", new PropertySingleValue(100));
		m.put("Q30", new PropertySingleValue(100));
		m.put("qualityScore", new PropertySingleValue(100));
		m.put("nbReadIllumina", new PropertySingleValue(100));
		t.set("read1", m);
		
		return t;
	}
	
	
	@Test
	 public void testValidateTreatmentCreationOK() {

		Treatment t = getNewTreatmentForReadSet();
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(0);
	}
	 
	@Test
	 public void testValidateTreatmentUpdatedOK() {
		    	   
		Treatment t = getNewTreatmentForReadSet();
		
		// create treatment
		createSameTrt();
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setUpdateMode();
		
		ReadSet readSetExists = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetExists!=null){
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(0);
			assertThat(t.results().get("read1").get("nbReadIllumina").value.toString()).contains("1");
		}
		else {
			System.out.println("Missing readSet rdCode !");
		}

	}
	 
	
	
	@Test
	 public void testValidateTreatmentErrorMissingLevel() {

		Boolean b = false;
		String msgErreur = "";
		
		Treatment t = getNewTreatmentForReadSet();
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		//Level.CODE levelCode = Level.CODE.ReadSet; 
		//ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		try {
			t.validate(ctxVal);
		}
		catch(IllegalArgumentException e) {
			System.out.println(e.toString());
			b = true;
			msgErreur = "missing level parameter";
		}		
		assertThat(b).isEqualTo(true);
		assertThat(msgErreur).isEqualTo("missing level parameter");
	}
	
	@Test
	 public void testValidationTreatmentErrorMissingCode() {	
		
		Boolean b = false;
		String msgErreur = "";
		
		Treatment t = getNewTreatmentForReadSet();
		t.code =  null;	// NO CODE!	
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		try {
			t.validate(ctxVal);
		}
		catch(NullPointerException e) {
			System.out.println(e.toString());
			b = true;
			msgErreur = "Null Pointer Exception";
		}
		
		assertThat(b).isEqualTo(true);
		assertThat(msgErreur).isEqualTo("Null Pointer Exception");
	}
	
	
	
	@Test
	 public void testValidateTreatmentErrorCodeRequired() {	
		Treatment t = getNewTreatmentForReadSet();
		t.code =  ""; //empty!		
		
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(7);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("code");
	}
	
	
	
	@Test
	 public void testValidationTreatmentErrorTypeCodeRequired() {	
		
		deleteRdCode();
		
		Treatment t = getNewTreatmentForReadSet();	
		t.typeCode = ""; //vide!
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(1);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("typeCode");
	}
	
	
	
	@Test
	 public void testValidateTreatmentErrorCategoryCodeRequired() {
		
		deleteRdCode(); 
		
		Treatment t = getNewTreatmentForReadSet();
		t.categoryCode = ""; //vide!
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(1);
		assertThat(ctxVal.errors.toString()).contains(ERROR_REQUIRED_MSG);
		assertThat(ctxVal.errors.toString()).contains("categoryCode");
	}
	 
	 
	
	@Test
	 public void testValidateTreatmentErrorCodeNotUnique() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {		    	   
		Treatment t = getNewTreatmentForReadSet();
		
		// create treatment
		createSameTrt();
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		ReadSet readSetExists = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetExists!=null){
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains(ERROR_CODE_NOTUNIQUE_MSG);
		}
		
		}});
	}
	
	private void createSameTrt() {

		Run runDelete = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME,Run.class,DBQuery.is("code","DIDIER_TESTFORTRT"));
		if(runDelete!=null){
			MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runDelete._id);
		}
		ReadSet readSetDelete = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetDelete!=null){
			MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,readSetDelete._id);
		}
		Run run = RunMockHelper.newRun("DIDIER_TESTFORTRT");
		run.dispatch = true; // For the archive test
		Lane lane = RunMockHelper.newLane(1);
		Lane lane2 = RunMockHelper.newLane(2);
		List<Lane> lanes = new ArrayList<Lane>();
		
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		readset.runCode = run.code;
		
		// add t1 to the readset
		Treatment t1 = new Treatment();
		t1.code =  "ngsrg";	
		t1.typeCode = "ngsrg-illumina";
		t1.categoryCode = "ngsrg";
		//define map of single property values
		Map<String,PropertyValue> m = new HashMap<String,PropertyValue>();
		m.put("nbCluster", new PropertySingleValue(10));
		m.put("nbBases", new PropertySingleValue(100));
		m.put("fraction", new PropertySingleValue(33.33));
		m.put("Q30", new PropertySingleValue(33.33));
		m.put("qualityScore", new PropertySingleValue(33.33));
		m.put("nbReadIllumina", new PropertySingleValue(10));
		t1.set("read1", m);
				
		Map<String, Treatment> mT = new HashMap<String, Treatment>();  
		mT.put("ngsrg", t1);
		
		readset.treatments = mT; 
		
		lanes.add(lane);
		lanes.add(lane2);
		run.lanes = lanes;
		
		Result result = callAction(controllers.runs.api.routes.ref.Runs.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonRun(run)));
		assertThat(status(result)).isEqualTo(OK);
		
		result = callAction(controllers.readsets.api.routes.ref.ReadSets.save(),fakeRequest().withJsonBody(RunMockHelper.getJsonReadSet(readset)));
        assertThat(status(result)).isEqualTo(OK);
		
	}
	
	
	
	@Test
	 public void testValidateTreatmentErrorCodeNotExists() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {		    	   
		Treatment t = getNewTreatmentForReadSet();
		
		// create treatment
		deleteRdCode();
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		//in this case, we must be in update mode
		ctxVal.setUpdateMode();
		
		ReadSet readSetExists = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME,ReadSet.class,DBQuery.is("code","rdCode"));
		if(readSetExists==null){
			
			t.validate(ctxVal);
			
			assertThat(ctxVal.errors).hasSize(1);
			assertThat(ctxVal.errors.toString()).contains(ERROR_CODE_NOTEXISTS_MSG);
		}
		else {
			System.out.println("method deleteRdCode() doesn't run normally !");
		}
		
		       }});

	}
	


	@Test
	 public void testValidateTreatmentErrorValueNotDefined() {
		
		Treatment t = getNewTreatmentForReadSet();

		t.results().get("read1").put("bad", new PropertySingleValue("Ouh la la"));
		
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(1);
		assertThat(ctxVal.errors.toString()).contains(ERROR_NOTDEFINED_MSG);
		
	}
	
	
	
	
	@Test
	 public void testValidateTreatmentErrorBadTypeValue() {
		
		Treatment t = getNewTreatmentForReadSet();
		
		t.results().get("read1").remove("nbReadIllumina");
		//must generate a error (because of a bad value)
		t.results().get("read1").put("nbReadIllumina", new PropertySingleValue("un"));
				
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(1);
		assertThat(ctxVal.errors.toString()).contains(ERROR_BADTYPE_MSG);
		
	}
	
	
	
	@Test
	 public void testValidateTreatmentErrorBadContext() {
		
		Treatment t = getNewTreatmentForReadSet();
		
		// new bad context
		Map<String,PropertyValue> m3 = new HashMap<String,PropertyValue>();
		m3.put("nbCluster", new PropertySingleValue(10));
		m3.put("nbBases", new PropertySingleValue(100));
		m3.put("fraction", new PropertySingleValue(33.33));
		m3.put("Q30", new PropertySingleValue(33.33));
		m3.put("qualityScore", new PropertySingleValue(33.33));
		m3.put("nbReadIllumina", new PropertySingleValue(1));

		t.set("read3", m3);
		
		ContextValidation ctxVal = new ContextValidation(); 
		
		Level.CODE levelCode = Level.CODE.ReadSet; 
		ctxVal.putObject("level", levelCode);
		
		//add readset to ctxVal
		ReadSet readset = RunMockHelper.newReadSet("rdCode");
		ctxVal.putObject("readSet", readset);
		
		ctxVal.setCreationMode();
		
		t.validate(ctxVal);
		
		assertThat(ctxVal.errors).hasSize(1);
		assertThat(ctxVal.errors.toString()).contains(ERROR_VALUENOTAUTHORIZED_MSG);	
	}
	 
}
