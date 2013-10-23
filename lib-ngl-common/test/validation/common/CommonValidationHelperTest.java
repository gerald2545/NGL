package validation.common;

import static org.fest.assertions.Assertions.assertThat;
import models.laboratory.common.description.State;
import models.laboratory.sample.instance.Sample;
import models.utils.dao.DAOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.Helpers;
import utils.AbstractTests;
import validation.ContextValidation;
import validation.DescriptionValidationHelper;
import validation.common.instance.CommonValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class CommonValidationHelperTest extends AbstractTests {
	
	
	private static final String COLLECTION_NAME = "test";

	@BeforeClass
	public static void startTest() throws InstantiationException, IllegalAccessException, ClassNotFoundException, DAOException{
		app = getFakeApplication();
		Helpers.start(app);
		initData();
	}

	@AfterClass
	public static void endTest(){
		app = getFakeApplication();
		deleteData();
		Helpers.stop(app);
	}


	public static void initData() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		
	}

	private static void deleteData() {
			MongoDBDAO.getCollection(COLLECTION_NAME, Sample.class).drop();
	}
	
	
	/***
	 * 
	 * Unit test CommonValidationHelper.validateId method
	 * @throws DAOException
	 */
	
	@Test
	public void validateIdCreationMode() throws DAOException {
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setCreationMode();
		Sample sample=new Sample();
    	CommonValidationHelper.validateId(sample, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public void validateIdCreationModeError() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setCreationMode();
		Sample sample=saveDBOject(Sample.class, COLLECTION_NAME, "validateIdCreationModeError");
    	CommonValidationHelper.validateId(sample, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}

	@Test
	public void validateIdUpdateMode() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setUpdateMode();
		Sample sample=saveDBOject(Sample.class, COLLECTION_NAME, "validateIdUpdateMode");
    	CommonValidationHelper.validateId(sample, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public void validateIdUpdateModeError() throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setUpdateMode();
		Sample sample=new Sample();
    	CommonValidationHelper.validateId(sample, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(1);
	}

	/**
	 * 
	 *  Unit test CommonValidationHelper.validateCode 
	 */
	@Test
	public void validateCodeRequired(){
		ContextValidation contextValidation=new ContextValidation();
		Sample sample=new Sample();
    	CommonValidationHelper.validateCode(sample, COLLECTION_NAME, contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	
	@Test
	public void validateUniqueCode() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setCreationMode();
		Sample sample=saveDBOject(Sample.class,COLLECTION_NAME, "validateUniqueCode");
    	CommonValidationHelper.validateCode(sample, COLLECTION_NAME, contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}

	
	@Test
	public void validateExistCode() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setUpdateMode();
		Sample sample=new Sample();
		sample.code="validateExistCode";
    	CommonValidationHelper.validateCode(sample, COLLECTION_NAME, contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);

		contextValidation.errors.clear();
		sample=saveDBOject(Sample.class,COLLECTION_NAME, "validateExistCode");
		CommonValidationHelper.validateCode(sample, COLLECTION_NAME, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);

	}
	
	/**
	 *  State Code
	 * @throws DAOException 
	 */
	
	@Test
	public void validationStateCode() throws DAOException {
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateStateCode(State.find.findAll().get(0).code, contextValidation);
		assertThat(contextValidation.errors.size()).isEqualTo(0);
	}
	
	@Test
	public void validationStateRequired() {
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateStateCode(null, contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	
	@Test
	public void validationStateNotExist() {
		ContextValidation contextValidation=new ContextValidation();
		CommonValidationHelper.validateStateCode("notexist", contextValidation);
		assertThat(contextValidation.errors.size()).isNotEqualTo(0);
	}
	
/* TODO
 * 	
		CommonValidationHelper.validateStateCode(this.stateCode, contextValidation);

		CommonValidationHelper.validateResolution(this.resolutionCode,contextValidation);
		CommonValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
	CommonValidationHelper.validateProjectCodes(projectCodes, contextValidation);
	CommonValidationHelper.validateSampleCodes(sampleCodes, contextValidation);

*/
}
