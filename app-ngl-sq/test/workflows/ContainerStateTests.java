package workflows;

import java.util.Date;

import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;

import org.junit.Assert;
import org.junit.Test;

import utils.AbstractTests;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class ContainerStateTests extends AbstractTests {
	
	public static String CONTAINER_DATA="ContainerDataCNS";
	
	
	@Test
	public static void setContainerStateCode(){
		Container container=MongoDBDAO.findOne(CONTAINER_DATA, Container.class,DBQuery.is("state.code", "A"));
		container=MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, container);
		State state=new State();
		state.code="IW-E";
		state.user="test";
		state.date=new Date();
		ContextValidation contextValidation=new ContextValidation();
		Workflows.setContainerState(container.code,state,contextValidation);
		
		Container containerUpdate=MongoDBDAO.findOne(CONTAINER_DATA, Container.class,DBQuery.is("code", container.code));
		Assert.assertSame(containerUpdate.state.code, state.code);
		Assert.assertTrue(contextValidation.errors.size()==0);
				
	}
	
	@Test
	public static void setContainerStateCodeError(){
		Container container=MongoDBDAO.findOne(CONTAINER_DATA, Container.class,DBQuery.is("state.code", "A"));
		container=MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, container);
		State state=new State();
		state.code="TEST";
		state.user="test";
		state.date=new Date();
		ContextValidation contextValidation=new ContextValidation();
		Workflows.setContainerState(container.code,state,contextValidation);
		Assert.assertTrue(contextValidation.errors.size()==1);
	
	}
}
