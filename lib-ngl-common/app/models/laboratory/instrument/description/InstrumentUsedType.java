package models.laboratory.instrument.description;

import java.util.List;

import play.api.modules.spring.Spring;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.laboratory.processes.description.dao.ProcessTypeDAO;
import models.utils.ListObject;
import models.utils.dao.DAOException;


/**
 * Entity type used to declare properties that will be indicated with the use of the instrument
 * @author ejacoby
 *
 */
public class InstrumentUsedType extends CommonInfoType{
	
	public List<Instrument> instruments;
	
	public InstrumentCategory category;

	public List<ContainerSupportCategory> inContainerSupportCategories;
	public List<ContainerSupportCategory> outContainerSupportCategories;
	
	
	public static Finder<InstrumentUsedType> find = new Finder<InstrumentUsedType>(InstrumentUsedTypeDAO.class.getName()); 
	
	public InstrumentUsedType() {
		super(InstrumentUsedTypeDAO.class.getName());
	}
	
}
