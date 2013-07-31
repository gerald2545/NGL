package models.laboratory.sample.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;


import play.Logger;
import validation.utils.BusinessValidationHelper;
import validation.utils.ConstraintsHelper;
import validation.utils.ContextValidation;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

/**
 * 
 * Instances Sample are stored in MongoDB collection named Sample
 *  
 * Sample is referenced in Content
 *  
 * @author mhaquell
 *
 */
@MongoCollection(name="Sample")
public class Sample extends DBObject implements IValidation{


	@JsonIgnore
	public final static String HEADER="Sample.code;Sample.projectCodes;Sample.name;Sample.referenceCollab;Sample.taxonCode;Sample.comments";

	@JsonIgnore
	public final static String LEVEL_SEARCH=Level.CODE.Sample.toString();
	
	// SampleType Ref
	public String typeCode;

	public String importTypeCode;
	//Sample Category Ref
	public String categoryCode;

	public List<String> projectCodes;

	// ?? Wath is difference with code / referenceCollbab => code s'est interne au genoscope
	public String name;
	public String referenceCollab; 
	public Map<String,PropertyValue> properties;
	// Valid taxon
	public TBoolean valid;
	//public List<CollaboratorInvolve> collaborators;
	public String taxonCode;

	public List<Comment> comments;
	public TraceInformation traceInformation;

	public Sample(){
		this.traceInformation=new TraceInformation();
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
	public List<Project> getProjects(){
		return new HelperObjects<Project>().getObjects(Project.class, projectCodes);
	}


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextErrors) {

		BusinessValidationHelper.validateUniqueInstanceCode(contextErrors.errors, this.code, Sample.class, InstanceConstants.SAMPLE_COLL_NAME);

		BusinessValidationHelper.validateRequiredDescriptionCode(contextErrors.errors, this.categoryCode, "categoryCode", SampleCategory.find,false);

		//Validate properties definition from sampleType and importType where level contain string "Sample"
		List<PropertyDefinition> proDefinitions=new ArrayList<PropertyDefinition>();

		SampleType sampleType=BusinessValidationHelper.validateRequiredDescriptionCode(contextErrors.errors, this.typeCode, "typeCode", SampleType.find,true);
		ImportType importType=BusinessValidationHelper.validateRequiredDescriptionCode(contextErrors.errors, this.importTypeCode,"importTypeCode", ImportType.find,true);

		proDefinitions.addAll(sampleType.propertiesDefinitions);
		
		for(PropertyDefinition propertyDefinition:importType.propertiesDefinitions){
			if(propertyDefinition.level.code.contains(LEVEL_SEARCH)){
				proDefinitions.add(propertyDefinition);
			}
		}

		ConstraintsHelper.validateProperties(contextErrors.errors, this.properties, proDefinitions,"");

		BusinessValidationHelper.validateRequiredInstanceCodes(contextErrors.errors, this.projectCodes, "projectCodes",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);

		//TODO validation taxon 
	}



}
