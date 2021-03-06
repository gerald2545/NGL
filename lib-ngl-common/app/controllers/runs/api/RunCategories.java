package controllers.runs.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import controllers.CommonController;
import controllers.authorisation.Permission;
import models.laboratory.run.description.RunCategory;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;

public class RunCategories extends CommonController{

	final static Form<RunCategoriesSearchForm> runCategoriesForm = form(RunCategoriesSearchForm.class);
	
	@Permission(value={"reading"})
	public static Result list(){
		Form<RunCategoriesSearchForm> runCategoryFilledForm = filledFormQueryString(runCategoriesForm,RunCategoriesSearchForm.class);
		RunCategoriesSearchForm runCategoriesSearch = runCategoryFilledForm.get();
		
		List<RunCategory> runCategories;
		
		try{		
			runCategories = RunCategory.find.findAll();
			
			if(runCategoriesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<RunCategory>(runCategories, runCategories.size()))); 
			}else if(runCategoriesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(RunCategory et:runCategories){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(runCategories));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
