package services.instance.sample;

import java.sql.SQLException;

import com.mongodb.MongoException;

import models.utils.dao.DAOException;
import services.ncbi.TaxonomyServices;
import rules.services.RulesException;
import scala.concurrent.duration.FiniteDuration;

public class UpdateSampleNCBITaxonCNG extends AbstractUpdateSampleNCBITaxon{

	public UpdateSampleNCBITaxonCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("UpdateSampleNCBI", durationFromStart, durationFromNextIteration);

	}

	

	
}
