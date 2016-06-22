package models.laboratory.reception.instance;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.instance.Comment;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.utils.CodeHelper;
import validation.ContextValidation;

public class CommentsFieldConfiguration extends ObjectFieldConfiguration<Comment> {
	
	public CommentsFieldConfiguration() {
		super(AbstractFieldConfiguration.commentsType);		
	}

	@Override
	public void populateField(Field field, Object dbObject,
			Map<Integer, String> rowMap, ContextValidation contextValidation, Action action)
			throws Exception {
		//we create or update all the comments
		Comment commentObject = new Comment();
		commentObject.creationDate = new Date();
		commentObject.createUser = contextValidation.getUser();
		commentObject.code = CodeHelper.getInstance().generateExperimentCommentCode(commentObject);

		populateSubFields(commentObject, rowMap, contextValidation, action);
		if(StringUtils.isNotBlank(commentObject.comment)){
			populateField(field, dbObject, Collections.singletonList(commentObject));
		}
				
	}

}
