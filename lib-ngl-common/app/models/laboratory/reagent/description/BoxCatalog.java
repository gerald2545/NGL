package models.laboratory.reagent.description;

import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.reagentCatalogs.instance.KitCatalogValidationHelper;
import validation.utils.ValidationHelper;

public class BoxCatalog extends AbstractCatalog{
	public String kitCatalogCode;
	public double storageConditions;

	@Override
	public void validate(ContextValidation contextValidation) {
		ValidationHelper.required(contextValidation, name, "name");
		if(!contextValidation.hasErrors()){
			KitCatalogValidationHelper.validateCode(this, InstanceConstants.REAGENT_CATALOG_COLL_NAME, contextValidation);
			KitCatalogValidationHelper.validateKitCatalogCode(kitCatalogCode, contextValidation);
		}
	}
}