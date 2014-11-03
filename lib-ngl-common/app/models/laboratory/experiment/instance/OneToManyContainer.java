package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.Logger;
import validation.ContextValidation;
import validation.utils.ValidationConstants;

public class OneToManyContainer extends AtomicTransfertMethod {

	public int outputNumber;
	public ContainerUsed inputContainerUsed;
	public List<ContainerUsed> outputContainerUseds;
	
	public OneToManyContainer(){
		super();
	}
	
	@Override
	public ContextValidation createOutputContainerUsed(Experiment experiment,ContextValidation contextValidation) {
		Logger.error("Not implemented");
		contextValidation.addErrors("locationOnContainerSupport",ValidationConstants.ERROR_NOTDEFINED_MSG);
		return contextValidation;
	}
	
	@Override
	public ContextValidation saveOutputContainers(Experiment experiment, ContextValidation contextValidation) {
		contextValidation.addErrors("locationOnContainerSupport",ValidationConstants.ERROR_NOTDEFINED_MSG);
		Logger.error("Not implemented");
		return contextValidation;
	}


	@Override
	public void validate(ContextValidation contextValidation) {
		inputContainerUsed.validate(contextValidation);
		for(ContainerUsed containerUsed:outputContainerUseds){
			containerUsed.validate(contextValidation);
		}
	}
	@JsonIgnore
	public List<ContainerUsed> getInputContainers(){
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(inputContainerUsed);
		return cu;
	}
	@JsonIgnore
	public List<ContainerUsed> getOutputContainers(){
		return outputContainerUseds;
	}

	
}
