package controllers.sra.samples.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Submission;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import models.laboratory.common.instance.State;

public class Samples extends DocumentController<Sample>{

	
	final static Form<SamplesSearchForm> samplesSearchForm = form(SamplesSearchForm.class);
	final static Form<Sample> sampleForm = form(Sample.class);

	public Samples() {
		super(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class);
	}

	public Result list()
	{
		SamplesSearchForm form = filledFormQueryString(SamplesSearchForm.class);
		Query query = getQuery(form);
		MongoDBResult<Sample> results = mongoDBFinder(form, query);							
		List<Sample> list = results.toList();
		return ok(Json.toJson(list));
	}
	
	public Result update(String code)
	{
		//Get Submission from DB 
		Sample sample = getSample(code);
		Form<Sample> filledForm = getFilledForm(sampleForm, Sample.class);
		if (sample == null) {
			filledForm.reject("Sample " +  code, "not exist in database");  // si solution filledForm.reject
			return badRequest(filledForm.errorsAsJson());
		}

		Sample sampleInput = filledForm.get();

		if (code.equals(sampleInput.code)) {
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ctxVal.setUpdateMode();
			ctxVal.getContextObjects().put("type", "sra");
			sampleInput.traceInformation.setTraceInformation(getCurrentUser());
			sampleInput.validate(ctxVal);	
			if (!ctxVal.hasErrors()) {
				Logger.info("Update sample "+sample.code);
				MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, sampleInput);
				return ok(Json.toJson(sampleInput));
			}else {
				return badRequest(filledForm.errorsAsJson());
			}
		}else{
			filledForm.reject("sample code " + code + " and sampleInput.code " + sampleInput.code , " are not the same");
			return badRequest(filledForm.errorsAsJson());
		}	
	}
	
	private Sample getSample(String code)
	{
		Sample sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, code);
		return sample;
	}

	
	private Query getQuery(SamplesSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if (CollectionUtils.isNotEmpty(form.listSampleCodes)) { //all
			queries.add(DBQuery.in("code", form.listSampleCodes));
		}
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
}