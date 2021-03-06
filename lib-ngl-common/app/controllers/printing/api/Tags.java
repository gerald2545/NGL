package controllers.printing.api;


import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.printing.Tag;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;

import play.Play;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import rules.services.RulesServices6;
import services.print.PrinterService;
import validation.ContextValidation;
import controllers.APICommonController;
import fr.cea.ig.MongoDBDAO;

public class Tags extends APICommonController<Tag>{
	final static Form<TagPrintForm> printForm = form(TagPrintForm.class);
	public Tags() {
		super(Tag.class);
	}

	public Result list(){
		
		TagListForm form = filledFormQueryString(TagListForm.class);
		List<Object> facts = getFacts(form);
		List<Object> tags = RulesServices6.getInstance().callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), "tags", facts);
		return ok(Json.toJson(tags));
	}

	public Result print(){
		Form<TagPrintForm> form = getFilledForm(printForm, TagPrintForm.class);
		TagPrintForm input = form.get();
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), form.errors());
		
		Spring.getBeanOfType(PrinterService.class).printTags(input.printerCode, input.barcodePositionId, input.tags, ctxVal);
		if (!ctxVal.hasErrors()) {
			return ok();
		} else {
			return badRequest(form.errorsAsJson());
		}
	}
	
	
	private List<Object> getFacts(TagListForm form) {
		List<Object> facts = new ArrayList<Object>();
		
		if(StringUtils.isNotBlank(form.experimentCode)){
			Experiment exp = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, form.experimentCode);		
			facts.add(exp);
		}else if(CollectionUtils.isNotEmpty(form.containerSupportCodes)){
			List<ContainerSupport> supports = MongoDBDAO.find(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.in("code", form.containerSupportCodes)).toList();
			facts.addAll(supports);			
		}		
		return facts;
	}
	
}
