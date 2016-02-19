package controllers.instruments.io.utils;

import java.io.File;

import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;

public abstract class AbstractOutput {
	
	public abstract File generateFile(Experiment experiment, ContextValidation contextValidation) throws Exception;
}
