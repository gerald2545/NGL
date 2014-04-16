package services.instance.container;

import java.sql.SQLException;
import java.util.List;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.container.instance.Container;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNG;
import play.Logger;

public class ContainerImportCNG extends AbstractImportDataCNG{

	public ContainerImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ContainerImportCNG",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		
		Logger.debug("start loading samples");
		
		List<Sample> samples = limsServices.findSampleToCreate(contextError, null) ;

		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
			
		limsServices.updateLimsSamples(samps, contextError, "creation");
		
		
		Logger.debug("start updating samples");
		
		samples = limsServices.findSampleToModify(contextError, null);
		
		for (Sample sample : samples) {
			MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
		}
		samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
			
		limsServices.updateLimsSamples(samps, contextError, "update");

		/*
		
		Logger.debug("start loading containers");		
		
		List<Container> containers = limsServices.findContainerToCreate(contextError);
		
		//common method for CNS & CNG
		ContainerHelper.createSupportFromContainers(containers, contextError);
		
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		limsServices.updateLimsContainers(ctrs, contextError, "creation");

		
		Logger.debug("start updating containers");		
		
		containers = limsServices.findContainerToModify(contextError);
		
		//common method for CNS & CNG
		ContainerHelper.createSupportFromContainers(containers, contextError);
		
		for (Container container : containers) {
			MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
		}
		ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		
		limsServices.updateLimsContainers(ctrs, contextError, "update");

		*/ 
		Logger.debug("end loading");
				
	}

}
