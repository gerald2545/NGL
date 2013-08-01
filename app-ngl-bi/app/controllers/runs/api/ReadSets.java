package controllers.runs.api;

import java.util.List;

import com.mongodb.MongoException;

import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.DBUpdate.Builder;

import play.data.Form;
import static play.data.Form.form;
import play.libs.Json;
import play.mvc.Result;

import validation.BusinessValidationHelper;

import controllers.Constants;
import controllers.CommonController;

import fr.cea.ig.MongoDBDAO;

import play.Logger;





public class ReadSets extends CommonController{

	final static Form<ReadSet> readSetForm = form(ReadSet.class);
	
	public static Result save(String code, Integer laneNumber){
		
		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		
		if(!filledForm.hasErrors()) {
			Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", code).is("lanes.number", laneNumber));	
			if(null == run){
				return notFound();
			}
			ReadSet readsetValue = filledForm.get();			
			BusinessValidationHelper.validateReadSet(filledForm.errors(),run, laneNumber, readsetValue, Constants.RUN_ILLUMINA_COLL_NAME, null);
			
			if(!filledForm.hasErrors()) {
				for(int i = 0; i < run.lanes.size(); i++){
					Lane l = run.lanes.get(i);
					if(l.number.equals(laneNumber)){ 
						int j = 0;
						boolean isFind = false;
						for(; l.readsets != null && j < l.readsets.size() ; j++){
							ReadSet r = l.readsets.get(j);
							if(readsetValue.code.equals(r.code)){	
								MongoDBDAO.updateSet(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets."+j, readsetValue);
								isFind = true;
								break;
							}
						}
						if(!isFind){MongoDBDAO.updatePush(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets", readsetValue);}
						break;
						
					}
				}				
			}
		}
		
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(filledForm.get()));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	public static Result update(String readSetCode){
		
		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		if(!filledForm.hasErrors()){
			ReadSet readsetValue = filledForm.get();
		
			if(readsetValue.code.equals(readSetCode)){
				Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.code", readSetCode));
				
				if(null == run) {
					return notFound();
				} 
				
				BusinessValidationHelper.validateReadSet(filledForm.errors(), run,-1, readsetValue, Constants.RUN_ILLUMINA_COLL_NAME,null);
				
				boolean flagReadSet = false;
				if(!filledForm.hasErrors()) {
					for(int i=0; i<run.lanes.size() && !flagReadSet;i++){
						for(int j=0;j<run.lanes.get(i).readsets.size() && !flagReadSet;j++) {
							if(run.lanes.get(i).readsets.get(j).code.equals(readsetValue.code)){
								//ReadSet find
								flagReadSet = true;								
								MongoDBDAO.updateSet(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets."+j, readsetValue);
							}
						}
					}								
				}
			}
		}
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(filledForm.get()));			
		} else {
			return badRequest(filledForm.errorsAsJson());			
		}
	}
	
	public static Result get(String readSetCode){
		Query object = DBQuery.is("lanes.readsets.code", readSetCode);
		Run run =  MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, object);		
		if(run == null) {
			return notFound();
		} 		
		ReadSet readsetValue = null;
		if(run.lanes != null){
			for(int i=0; i<run.lanes.size();i++){
				List<ReadSet> readsets = run.lanes.get(i).readsets;
				for(int j=0;j<readsets.size();j++) {
					if(readsets.get(j).code.equals(readSetCode)){
						readsetValue = run.lanes.get(i).readsets.get(j);
						break;
					}
				}
			}
		}
		
		if(readsetValue != null){
			return ok(Json.toJson(readsetValue));					
		}else{
			return notFound();
		}		
	}
	

	
	public static Result delete(String readSetCode){
		//Logger.info("in delete "+readSetCode );
		Boolean bUpdate = false;
		Query object = DBQuery.is("lanes.readsets.code", readSetCode);
		Run run =  MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, object);	
		if(run==null){
			return badRequest();
		}
		String runCode = run.code;
		for(int i=0;run.lanes!=null && i<run.lanes.size();i++){
			for(int j=0;run.lanes.get(i).readsets != null && j<run.lanes.get(i).readsets.size();j++){
				// vide
				if (run.lanes.get(i).readsets.get(j).code.equals(readSetCode) ) {
					MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",runCode),DBUpdate.unset("lanes."+i+".readsets."+j));
					bUpdate = true;
				}
			}
			//supprime
			MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",runCode),DBUpdate.pull("lanes."+i+".readsets",null));	

		}		
		if (bUpdate) {
			return ok();
		}
		else {
			return notFound();
		}
	}
	
	
	
}
