package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import models.laboratory.container.instance.Content;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import validation.ContextValidation;

public class ContentsFieldConfiguration extends ObjectFieldConfiguration<Content> {
	
	public Boolean pool = Boolean.FALSE;
	
	public ContentsFieldConfiguration() {
		super(AbstractFieldConfiguration.contentsType);		
	}

	@Override
	public void populateField(Field field, Object dbObject,
			Map<Integer, String> rowMap, ContextValidation contextValidation, Action action)
			throws Exception {
		
		if(Action.save.equals(action)){
			if(!pool){
				Content content = new Content();
				populateSubFields(content, rowMap, contextValidation, action);
				populateField(field, dbObject, Collections.singletonList(content));	
			}else{
				throw new RuntimeException("Pool not managed for save");
			}
		}else if(Action.update.equals(action)) {
			//in case on update we need to retrieve the good content, we used the sampleCode for that so the sampleCode must be declared in content configs
			if(!pool){
				List<Content> contents = (List<Content>) field.get(dbObject);
				if(contents.size() > 0){
					Content content = contents.get(0);
					populateSubFields(content, rowMap, contextValidation, action);
				}else{
					contextValidation.addErrors("Error", "no contents found");
				}
				
			}else if(pool && configs.containsKey("sampleCode")){
				List<Content> contents = (List<Content>) field.get(dbObject);
				
				//1 Get sampleCode
				//2 find content for the sample code
				//3 update : populateSubFields(content, rowMap, contextValidation, action);
				
				throw new RuntimeException("Pool not managed for update");
				
			} else{
				contextValidation.addErrors("Error", "contents configuration must contain a sampleCode property to update the good content in case of pool");
			}
			
		}					
	}

}
