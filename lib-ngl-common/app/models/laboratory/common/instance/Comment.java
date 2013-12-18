package models.laboratory.common.instance;

import java.util.Date;

import models.administration.authorisation.User;
import models.utils.HelperObjects;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;

/**
 * Comment are embedded data in collection like Container, Experiment.... 
 * 
 * @author mhaquell
 *
 */
public class Comment implements IValidation {

	
	public String comment;
	public String createUser;
	public Date creationDate;
	
	public Comment(String comment) {
		setComment(comment);
	}
	
	public Comment(){
		
	}
	
	public void setComment(String comment){
		
		if(creationDate==null) 
			creationDate = new Date();
		this.comment=comment;
		
	}

	@JsonIgnore
	public User getCreateUser(){
		return new HelperObjects<User>().getObject(User.class, createUser);
	}

	@Override
	public void validate(ContextValidation contextValidation) {

	}
	
}
