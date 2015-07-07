package controllers.submissions.api;

import static play.data.Form.form;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mail.MailServiceException;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.util.SraException;
import models.utils.InstanceConstants;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import services.FileAcServices;
import services.SubmissionServices;
import services.XmlServices;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Submissions extends DocumentController<Submission>{

	public Submissions() {
		super(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class);
	}

	final static Form<Submission> submissionForm = form(Submission.class);
	final static Form<File> pathForm = form(File.class);
	// declaration d'une instance submissionCreationForm qui permet de recuperer les
	// données du formulaire initSubmission pour realiser la creation de la soumission => utilisee dans save()
	final static Form<SubmissionsCreationForm> submissionsCreationForm = form(SubmissionsCreationForm.class);
	// declaration d'une instance submissionSearchForm qui permet de recuperer la liste des soumissions => utilisee dans list()
	final static Form<SubmissionsSearchForm> submissionsSearchForm = form(SubmissionsSearchForm.class);


	// methode appelee avec url suivante :
	// http://localhost:9000/api/submissions?datatable=true&paginationMode=local&projCode=BCZ&state=new
	// url construite dans services.js 
	//search : function(){
	//	this.datatable.search({projCode:this.form.projCode, state:'new'});
	//},
	
	public Result list(){	
		Form<SubmissionsSearchForm> submissionsSearchFilledForm = filledFormQueryString(submissionsSearchForm, SubmissionsSearchForm.class);
		SubmissionsSearchForm submissionsSearchForm = submissionsSearchFilledForm.get();
		Logger.debug(submissionsSearchForm.state);
		Query query = getQuery(submissionsSearchForm);
		MongoDBResult<Submission> results = mongoDBFinder(submissionsSearchForm, query);				
		List<Submission> submissionsList = results.toList();
		if(submissionsSearchForm.datatable){
			return ok(Json.toJson(new DatatableResponse<Submission>(submissionsList, submissionsList.size())));
		}else{
			return ok(Json.toJson(submissionsList));
		}
	}

	
	private Query getQuery(SubmissionsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.projCode)) { //all
			queries.add(DBQuery.in("projectCode", form.projCode));
		}
		if (StringUtils.isNotBlank(form.state)) { //all
			queries.add(DBQuery.in("state.code", form.state));
		}	
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}

	public Result update(String code)
	{
		//Get Submission from DB 
		Submission submission = getSubmission(code);
		if (submission == null) {
			return badRequest("Submission with code "+code+" not exist");
		}
		Form<Submission> filledForm = getFilledForm(submissionForm, Submission.class);
		Submission submissionInput = filledForm.get();
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
		if (code.equals(submissionInput.code)) {
			
			ctxVal.setUpdateMode();
			ctxVal.getContextObjects().put("type","sra");
			submissionInput.traceInformation.setTraceInformation(getCurrentUser());
			submissionInput.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				Logger.info("Update submission state "+submissionInput.state.code);
				MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submissionInput);
				return ok(Json.toJson(submissionInput));
			}else {
				return badRequest(filledForm.errorsAsJson());
			}
		}else{
			return badRequest("submission code are not the same");
		}	
	}


	public Result createXml(String code)
	{
		//Get Submission from DB 
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		if (submission == null) {
			return badRequest("Submission with code "+code+" not exist");
		}
		try {
			submission = XmlServices.writeAllXml(code);
		} catch (IOException e) {
			return badRequest(e.getMessage());
		} catch (SraException e) {
			return badRequest(e.getMessage());
		}
		return ok(Json.toJson(submission));
	}


	public Result treatmentAc(String code)
	{
		//Get Submission from DB 
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		if (submission == null) {
			return badRequest("Submission with code "+code+" not exist");
		}
		Form<File> filledForm = getFilledForm(pathForm, File.class);
		Logger.debug("filledForm "+filledForm);
		File ebiFileAc = filledForm.get();
		try {
			submission = FileAcServices.traitementFileAC(code, ebiFileAc);
		} catch (IOException e) {
			return badRequest(e.getMessage());
		} catch (SraException e) {
			return badRequest(e.getMessage());
		} catch (MailServiceException e) {
			return badRequest(e.getMessage());
		}
		return ok(Json.toJson(submission));
	}
	
	private Submission getSubmission(String code)
	{
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		return submission;
	}


	public Result save() throws SraException, IOException
	{
		Form<SubmissionsCreationForm> filledForm = getFilledForm(submissionsCreationForm, SubmissionsCreationForm.class);
		Logger.debug("filledForm "+filledForm);
		SubmissionsCreationForm submissionsCreationForm = filledForm.get();
		Logger.debug("readsets "+submissionsCreationForm.readSetCodes);

		List<String> readSetCodes = submissionsCreationForm.readSetCodes;

		//String codeReadSet1 = "BCZ_BGOSW_2_H9M6KADXX.IND15"; 
		//String codeReadSet2 = "BCZ_BIOSW_2_H9M6KADXX.IND19"; 

		String user = getCurrentUser();

		SubmissionServices submissionServices = new SubmissionServices();
		String submissionCode;
		ContextValidation contextValidation = new ContextValidation(user, filledForm.errors());
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		try {
			submissionCode = submissionServices.initNewSubmission(submissionsCreationForm.projCode, readSetCodes, submissionsCreationForm.studyCode, submissionsCreationForm.configurationCode, user, contextValidation);
			if (contextValidation.hasErrors()){
				contextValidation.displayErrors(Logger.of("SRA"));
				return badRequest(filledForm.errorsAsJson());
				
			}
		} catch (SraException e) {
			if (contextValidation.hasErrors()){
				return badRequest(filledForm.errorsAsJson());
			}else{
				return badRequest("{Error : {\"exception\" : \""+e.getMessage()+"\"}}");
			}
		}
		return ok(Json.toJson(submissionCode));
	}

	
	public Result activate(String submissionCode) throws SraException, IOException
	{
		SubmissionServices submissionServices = new SubmissionServices();
		Submission submission = null;
		Form<Submission> filledForm = getFilledForm(submissionForm, Submission.class);
		//Solution avec contexte validation
		//ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		try {
			submissionServices.activateSubmission(submissionCode);			
			submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		} catch (SraException e) {
			filledForm.reject("Submission "+submissionCode, e.getMessage());
			//ctxVal.addErrors("Submission "+submissionCode, e.getMessage());
			Logger.debug("filled form "+filledForm.errorsAsJson());
			//return badRequest(Json.toJson(e.getMessage()));
			return badRequest(filledForm.errorsAsJson());
		}
		return ok(Json.toJson(submission));
	}

}

