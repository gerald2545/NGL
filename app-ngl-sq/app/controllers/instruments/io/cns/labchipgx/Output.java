package controllers.instruments.io.cns.labchipgx;



import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import validation.ContextValidation;
import controllers.instruments.io.cns.biomekfx.tpl.txt.normalisation_x_to_plate;
import controllers.instruments.io.cns.labchipgx.tpl.txt.*;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	 public File generateFile(Experiment experiment,ContextValidation contextValidation) throws Exception {
		String type = (String)contextValidation.getObject("type");
		
		String content=null;
		//tube / 96-well-plate
		if ("chip-migration".equals(experiment.typeCode)){
			content = OutputHelper.format(chipmigration.render(experiment).body());
		}else {
			//rna-prep; pcr-purif; normalization-and-pooling a venir.....
			throw new RuntimeException(experiment.typeCode+" not managed");
		}
		
		File file = new File(getFileName(experiment)+".csv", content);
		return file;
	}
	
	private String getFileName(Experiment experiment) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyMMdd");
		return experiment.typeCode.toUpperCase()+"_"+experiment.atomicTransfertMethods.get(0).inputContainerUseds.get(0).locationOnContainerSupport.code+"_"+sdf.format(new Date());
	}

	
	
}
