package models.laboratory.common.instance;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;

public class State implements IValidation {

	public String code;
	public Date date;
	public String user;
	public List<String> resolutionCodes;

	public List<TransientState> historical;

	public State(String code,String user){
		this.code=code;
		this.user=user;
		this.date = new Date();
	}
	
	public State(){
		this.date = new Date();
	}
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		CommonValidationHelper.validateStateCode(this.code, contextValidation);
		ValidationHelper.required(contextValidation, date, "date");
		ValidationHelper.required(contextValidation, user, "user");
		CommonValidationHelper.validateResolutionCodes(resolutionCodes,
				contextValidation);
	}

}
