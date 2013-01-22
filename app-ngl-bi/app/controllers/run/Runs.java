package controllers.run;

import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.Run;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import validation.BusinessValidationHelper;
import views.html.run.run;
import views.html.run.runs;
import controllers.utils.DataTableForm;
import fr.cea.ig.MongoDBDAO;
/**
 * Controller around Run object
 * @author galbini
 *
 */
public class Runs extends Controller {
	
	final static Form<Run> runForm = form(Run.class);
	final static Form<DataTableForm> datatableForm = form(DataTableForm.class);
	
	
	public static Result home() {
		return ok(runs.render(datatableForm, runForm));
	}
	
	public static Result list(){
		Form<DataTableForm> filledForm = datatableForm.bindFromRequest();
	
		List<Run> runs = MongoDBDAO.all(Constants.RUN_ILLUMINA_COLL_NAME, Run.class);
		ObjectNode result = Json.newObject();
		result.put("iTotalRecords", runs.size());
		result.put("iTotalDisplayRecords", runs.size());
		result.put("sEcho", filledForm.get().sEcho);
		result.put("aaData", Json.toJson(runs));
		
		return ok(Json.toJson(result));
	}
	
	public static Result add(){
		Form<Run> defaultForm = runForm.fill(new Run()); //put default value
		return ok(run.render(defaultForm, true));
	}
	
	public static Result show(String code, String format){
		Run runValue = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);		
		if(runValue != null){
			if("json".equals(format)){
				return ok(Json.toJson(runValue));
			}else{
				Form<Run> filledForm = runForm.fill(runValue);				
				return ok(run.render(filledForm, Boolean.FALSE));				
			}			
		}else{
			return notFound();
		}		
	}
	
	public static Result edit(String code, String format){
		Run runValue = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);		
		if(runValue != null){
			if("json".equals(format)){
				return ok(Json.toJson(runValue));
			}else{
				Form<Run> filledForm = runForm.fill(runValue);				
				return ok(run.render(filledForm, Boolean.TRUE));
				//return ok(Json.toJson(run));
			}			
		}else{
			return notFound();
		}	
	}
	
	
	public static Result createOrUpdate(String format){
		Form<Run> filledForm = getFilledForm(format);
		
		if(!filledForm.hasErrors()) {
			Run runValue = filledForm.get();
			if(null == runValue._id){
				runValue.traceInformation = new TraceInformation();
				runValue.traceInformation.setTraceInformation("ngsrg");				
			}else{
				runValue.traceInformation.setTraceInformation("ngsrg");
			}			
			BusinessValidationHelper.validateRun(filledForm.errors(), runValue, Constants.RUN_ILLUMINA_COLL_NAME);			
			if(!filledForm.hasErrors()) {
				runValue = MongoDBDAO.save(Constants.RUN_ILLUMINA_COLL_NAME, runValue);
				filledForm = filledForm.fill(runValue);
			}
		}
		
		if (!filledForm.hasErrors()) {
			if ("json".equals(format)) {
				return ok(Json.toJson(filledForm.get()));
			} else {
				return ok(run.render(filledForm, true));
			}
		} else {
			if ("json".equals(format)) {
				return badRequest(filledForm.errorsAsJson());
			} else {
				return badRequest(run.render(filledForm, true));
			}
		}
	}

	//necessite une double gestion avec et sans json pour pouvoir faire fonctionner les 2 ensemble
	//ceci est du à la gestion des Map qui est différente entre json et spring binder			
	private static Form<Run> getFilledForm(String format) {
		Form<Run> filledForm;
		if("json".equals(format)){
			JsonNode json = request().body().asJson();			
			Run runInput = Json.fromJson(json, Run.class);
			filledForm = runForm.fill(runInput);	//bindJson ne marche pas !			
		}else{
			filledForm = runForm.bindFromRequest();
		}
		return filledForm;
	}

}
