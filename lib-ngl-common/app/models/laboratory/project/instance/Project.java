package models.laboratory.project.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.utils.HelperObjects;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import fr.cea.ig.DBObject;



/**
 * Instance Project is stocked in Collection mongodb Project
 * Project Name are referencing in class Experience and Container
 * 
 * @author mhaquell
 *
 */
@MongoCollection(name="Project")
public class Project extends DBObject {

	public String typeCode;
	public String categoryCode;
	public String name;
	public String stateCode;
	public TraceInformation traceInformation;
	public Map<String, PropertyValue> properties;
	public List<Comment> comments;
	
	
	@JsonIgnore
	public ProjectType getProjectType(){
			return new HelperObjects<ProjectType>().getObject(ProjectType.class, typeCode, null);
	}
	
	@JsonIgnore
	public ProjectCategory getProjectCategory(){
			return new HelperObjects<ProjectCategory>().getObject(ProjectCategory.class, categoryCode, null);
	}

	@JsonIgnore
	public State getState(){
			return new HelperObjects<State>().getObject(State.class, stateCode, null);
	}
}
