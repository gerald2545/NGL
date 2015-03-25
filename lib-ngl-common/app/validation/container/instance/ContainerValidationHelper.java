package validation.container.instance;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.ObjectType.CODE;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import workflows.Workflows;

public class ContainerValidationHelper extends CommonValidationHelper{

	public static void validateContainerCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ContainerCategory.find,false);

	}

	public static void validateProcessTypeCode(String typeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation, typeCode,"processTypeCode", ProcessType.find);

	}

	public static void validateExperimentCode(String experimentCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, experimentCode, "fromPurifingCode", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}
	
	public static void validateContents(List<Content> contents, ContextValidation contextValidation) {
		
		if(ValidationHelper.required(contextValidation, contents, "contents")){

			for(int i=0;i<contents.size();i++){
				    contextValidation.addKeyToRootKeyName("contents."+i);
					contents.get(i).validate(contextValidation);
					contextValidation.removeKeyFromRootKeyName("contents."+i);
					Logger.debug("==> content." + i);
			}		

			validateContentPercentageSum(contents, contextValidation);
		}
	}
	
	//Check the sum of percentage of contents is 100
	public static void validateContentPercentageSum(List<Content> contents, ContextValidation contextValidation){
		Double percentageSum = 0.00;
		for(Content t:contents){			
			if(t.percentage!=null){
				percentageSum = percentageSum + t.percentage;
			}							
		}
		if(!(Math.abs(100.00-percentageSum)<=0.40)){
			contextValidation.addKeyToRootKeyName("contents");
			contextValidation.addErrors("percentageSum", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, percentageSum);
			contextValidation.addKeyToRootKeyName("contents");			
		}
	}

	public static void validateContainerSupport(LocationOnContainerSupport support,
			ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, support, "support")) {
			contextValidation.addKeyToRootKeyName("support");
			support.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("support");
		}		
	}

	public static void validateProcessCodes(List<String> inputProcessCodes, ContextValidation contextValidation) {
		if(inputProcessCodes!=null && inputProcessCodes.size() > 0){
			for(int i = 0; i < inputProcessCodes.size(); i++){
				BusinessValidationHelper.validateExistInstanceCode(contextValidation, inputProcessCodes.get(i), "inputProcessCodes."+i, Process.class, InstanceConstants.PROCESS_COLL_NAME); 
			}
		}
	}

	
	public static void validateStateCode(Container container,ContextValidation contextValidation) {
		
		boolean workflow=false;
		if(contextValidation.getObject("workflow")!=null){
			workflow=true;
		}
//		if(( CollectionUtils.isEmpty(container.inputProcessCodes) || !container.state.historical.get(container.state.historical.size() - 2).code.equals("UA")) && container.state.code.equals("A") ){
		if( CollectionUtils.isEmpty(container.inputProcessCodes) && container.state.code.equals("A") && !workflow ){
			contextValidation.addErrors("state.code",ValidationConstants.ERROR_BADSTATE_MSG,container.code );
		}
		if(CollectionUtils.isNotEmpty(container.inputProcessCodes) && container.state.code.equals("IW-P") && !workflow){
			contextValidation.addErrors("state.code",ValidationConstants.ERROR_BADSTATE_MSG,container.code );
		}
		contextValidation.addKeyToRootKeyName("state");
		CommonValidationHelper.validateStateCode(container.state.code,ObjectType.CODE.Container, contextValidation);
		contextValidation.removeKeyFromRootKeyName("state");

	}


}
