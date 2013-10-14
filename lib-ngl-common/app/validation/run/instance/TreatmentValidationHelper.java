package validation.run.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.run.description.ReadSetType;
import models.laboratory.run.description.RunType;
import models.laboratory.run.description.TreatmentCategory;
import models.laboratory.run.description.TreatmentContext;
import models.laboratory.run.description.TreatmentType;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;



public class TreatmentValidationHelper extends CommonValidationHelper {
	private static Level.CODE getLevelFromContext(ContextValidation contextValidation) {
		return getObjectFromContext("level", Level.CODE.class, contextValidation);
	}
	
	public static void validationTreatments(Map<String, Treatment> treatments, ContextValidation contextValidation) {
		if(null != treatments){
			List<String> trNames = new ArrayList<String>();
			contextValidation.addKeyToRootKeyName("treatments");
			for(Treatment t:treatments.values()){
				contextValidation.addKeyToRootKeyName(t.code);
				if(!trNames.contains(t.code) && treatments.containsKey(t.code)){										
					trNames.add(t.code);
					t.validate(contextValidation);					
				}else if(trNames.contains(t.code)){
					contextValidation.addErrors("code", ValidationConstants.ERROR_NOTUNIQUE_MSG, t.code);
				} else{
					contextValidation.addErrors("code", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, t.code);
				}
				contextValidation.removeKeyFromRootKeyName(t.code);
			}
			contextValidation.removeKeyFromRootKeyName("treatments");
		}
		
	}
	
	

	private static void validateCode(TreatmentType treatmentType, String code, ContextValidation contextValidation) {
	 if(ValidationHelper.required(contextValidation, code, "code")){
		if (contextValidation.isCreationMode() && isTreatmentExist(code, contextValidation)) {
	    	contextValidation.addErrors("code",ValidationConstants.ERROR_CODE_NOTUNIQUE_MSG, code);		    	
		}else if (contextValidation.isUpdateMode() && !isTreatmentExist(code, contextValidation)){
			contextValidation.addErrors("code",ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, code);
		}
		
		if(!treatmentType.names.contains(code)){
			contextValidation.addErrors("code",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, code);
		}		
	}
		
	}
	
	private static boolean isTreatmentExist(String code, ContextValidation contextValidation) {
		Level.CODE levelCode = getLevelFromContext(contextValidation);
		
		if(Level.CODE.ReadSet.equals(levelCode)){
			ReadSet readSet = (ReadSet) contextValidation.getObject("readSet");
			return MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.exists("treatments."+code)));
		}else if(Level.CODE.Lane.equals(levelCode)){
			Run run = (Run) contextValidation.getObject("run");
			Lane lane = (Lane) contextValidation.getObject("lane");
			
			return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", run.code), 
							DBQuery.elemMatch("lanes", 
									DBQuery.and(
											DBQuery.is("number", lane.number),
											DBQuery.exists("treatments."+code)))));
							
		}else if(Level.CODE.Run.equals(levelCode)){
			Run run = (Run) contextValidation.getObject("run");
			return MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", run.code),DBQuery.exists("treatments."+code)));			
		}
		return false;
	}

	private static void validateResults(TreatmentType treatmentType, Map<String, Map<String, PropertyValue>> results, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, results, "results")){
			Level.CODE levelCode = getLevelFromContext(contextValidation);
			for(Map.Entry<String, Map<String, PropertyValue>> entry : results.entrySet()){
				TreatmentContext context = getTreatmentContext(entry);
				if(!treatmentType.contexts.contains(context)){
					contextValidation.addErrors(entry.getKey(),ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, entry.getKey());
				}
				contextValidation.addKeyToRootKeyName(context.code);
				ValidationHelper.validateProperties(contextValidation, entry.getValue(), treatmentType.getPropertyDefinitionByLevel(Level.CODE.valueOf(context.name), levelCode));
				contextValidation.removeKeyFromRootKeyName(context.code);
			}
		}
		
	}

	private static TreatmentContext getTreatmentContext(Map.Entry<String, Map<String, PropertyValue>> entry) {
		try {
			return TreatmentContext.find.findByCode(entry.getKey());
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void validateTreatmentType(String code, String typeCode, Map<String, Map<String, PropertyValue>> results, ContextValidation contextValidation) {
		TreatmentType treatmentType = validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", TreatmentType.find,true);
		if(null != treatmentType){
			validateCode(treatmentType, code, contextValidation);
			validateResults(treatmentType, results, contextValidation);						
		}		
	}

	public static void validateTreatmentCategoryCode(String categoryCode, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, categoryCode, "categoryCode")){
			validateExistDescriptionCode(contextValidation, categoryCode, "categoryCode", TreatmentCategory.find);			
		}
		
	}

}
