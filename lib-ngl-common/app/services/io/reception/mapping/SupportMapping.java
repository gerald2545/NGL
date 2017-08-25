package services.io.reception.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.StorageHistory;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.instance.Sample;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import play.Logger;
import services.io.reception.Mapping;
import validation.ContextValidation;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;



public class SupportMapping extends Mapping<ContainerSupport> {

	public SupportMapping(Map<String, Map<String, DBObject>> objects, Map<String, ? extends AbstractFieldConfiguration> configuration, Action action, ContextValidation contextValidation) {
		super(objects, configuration, action, InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, Mapping.Keys.support, contextValidation);
	}

	
	protected void update(ContainerSupport support) {
		//TODO update categoryCode if not a code but a label.
		if(Action.update.equals(action)){
			support.traceInformation.setTraceInformation(contextValidation.getUser());
		}else{
			support.traceInformation = new TraceInformation(contextValidation.getUser());
		}
		
		/* GA/FDS obsolete
		if(null == support.state || null == support.state.code){
			support.state = new State("IS", contextValidation.getUser());
		}else
		
		if(support.state.user == null){
			support.state.user = contextValidation.getUser();
		}
		*/
		
		//historisation of storageCode
		if(configuration.containsKey("storageCode")){
			if(support.storages == null){
				support.storages = new ArrayList<StorageHistory>();
			}
			StorageHistory sh = new StorageHistory();
			sh.code = support.storageCode;
			sh.date = new Date();
			sh.user = contextValidation.getUser();
			sh.index = support.storages.size();
			support.storages.add(sh);
		}
	}


	@Override
	public void consolidate(ContainerSupport support) {
		if(objects.containsKey(Keys.container.toString())){
			List<Container> containers = getContainersForASupport(support);
			support.nbContainers = containers.size();
			support.nbContents = containers.stream().mapToInt(c -> c.contents.size()).sum();
			support.projectCodes = containers.stream().map(c -> c.projectCodes).flatMap(Set::stream).collect(Collectors.toSet());
			support.sampleCodes = containers.stream().map(c -> c.sampleCodes).flatMap(Set::stream).collect(Collectors.toSet());
			if(null == support.categoryCode){
				support.categoryCode = getSupportCategoryCode(containers);
			}
			
			if(null == support.storageCode){
				support.storageCode = getStorageCode(containers);
			}
			
			if(null == support.state || null == support.state.code){
				support.state = getState(containers);
			}else if(support.state.user == null){
				support.state.user = contextValidation.getUser();
			}
			
			if(null == support.fromTransformationTypeCodes || support.fromTransformationTypeCodes.size() == 0){
				support.fromTransformationTypeCodes = getFromTransformationTypeCodes(containers);
			}
		}
		
		
	}

	
	private Set<String> getFromTransformationTypeCodes(List<Container> containers) {
		Set<String> transformationTypeCodes = containers
									.stream()
									.filter(c -> c.fromTransformationTypeCodes != null)
									.map(c -> c.fromTransformationTypeCodes)
									.flatMap(Set::stream)
									.collect(Collectors.toSet());
		if(transformationTypeCodes.size() > 0){
			return transformationTypeCodes;
		}else{
			return null;
		}
	}


	private State getState(List<Container> containers) {
		Set<String> categoryCodes = containers.stream().map(c -> c.state.code).collect(Collectors.toSet());
		if(categoryCodes.size() == 1)
			return containers.iterator().next().state;
		else if(categoryCodes.size() > 1){
			contextValidation.addErrors("state.code","different for several containers");
			return null;
		}else{
			return null;
		}
	}
	
	private String getStorageCode(List<Container> containers) {
		Set<String> categoryCodes = containers.stream().map(c -> c.support.storageCode).collect(Collectors.toSet());
		if(categoryCodes.size() == 1)
			return categoryCodes.iterator().next();
		else if(categoryCodes.size() > 1){
			contextValidation.addErrors("storageCode","different for several containers");
			return null;
		}else{
			return null;
		}
	}
	
	private String getSupportCategoryCode(List<Container> containers) {
		Set<String> categoryCodes = containers.stream().map(c -> c.support.categoryCode).collect(Collectors.toSet());
		if(categoryCodes.size() == 1)
			return categoryCodes.iterator().next();
		else{
			contextValidation.addErrors("categoryCode","different for several containers");
			return null;
		}
	}
	private List<Container> getContainersForASupport(ContainerSupport containerSupport) {
		Map<String, DBObject> allContainers = objects.get(Keys.container.toString());
		
		List<Container> selectedContainers = allContainers.values().stream()
			.map(c -> (Container)c)
			.filter(c -> containerSupport.code.equals(c.support.code))
			.collect(Collectors.toList());
		
		return selectedContainers;
	}

	@Override
	public void synchronizeMongoDB(DBObject c){
		if(Action.update.equals(action) && configuration.containsKey("storageCode") && !objects.containsKey(Keys.container.toString())){
			Logger.info("update storageCode for all container for support "+c.code);
			ContainerSupport support = (ContainerSupport)c;
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, ContainerSupport.class, 
					DBQuery.and(DBQuery.is("support.code", support.code)), 
					DBUpdate.set("support.storageCode", support.storageCode).set("traceInformation", support.traceInformation));
			
		}
		super.synchronizeMongoDB(c);
	}
}
