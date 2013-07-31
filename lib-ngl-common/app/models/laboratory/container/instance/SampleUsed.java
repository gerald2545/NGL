package models.laboratory.container.instance;


import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;

import org.codehaus.jackson.annotate.JsonIgnore;


import play.Logger;
import validation.utils.BusinessValidationHelper;
import validation.utils.ContextValidation;

public class SampleUsed implements IValidation{

	// Reference Sample code
	public String sampleCode;
	// Reference SampleType code
	public String typeCode;
	// Reference SampleCategory code
	public String categoryCode;
	
	public SampleUsed(){
	
	}

	@JsonIgnore
	public SampleUsed(String sampleCode,String typeCode,String categoryCode){
		this.sampleCode=sampleCode;
		this.typeCode=typeCode;
		this.categoryCode=categoryCode;
		
	}

	@JsonIgnore
	public Sample getSample(){
		return new HelperObjects<Sample>().getObject(Sample.class, sampleCode);
		
	}
	
	@JsonIgnore
	public SampleType getSampleType(){
		return new HelperObjects<SampleType>().getObject(SampleType.class, typeCode);

	}
	
	@JsonIgnore
	public SampleCategory getSampleCategory(){
		return new HelperObjects<SampleCategory>().getObject(SampleCategory.class, categoryCode);

		
	}

	@JsonIgnore
	@Override
	public void validate(ContextValidation contextErrors) {

		BusinessValidationHelper.validateExistInstanceCode(contextErrors.errors, sampleCode, "sampleCode", Sample.class, InstanceConstants.SAMPLE_COLL_NAME, false);
		BusinessValidationHelper.validateExistDescriptionCode(contextErrors.errors, typeCode, "typeCode", SampleType.find,false);
		BusinessValidationHelper.validateExistDescriptionCode(contextErrors.errors, categoryCode, "categoryCode",SampleCategory.find,false);

	}

}
