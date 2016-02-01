package models.laboratory.experiment.description;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.dao.DAOException;

/**
 * Parent class categories not represented by a table in the database
 * Database relationship for experiment with instrumentUsedType and protocol are represented in CommonInfoType table
 * @author ejacoby
 *
 */
public class ExperimentType extends CommonInfoType{
 
	public ExperimentCategory category;
	//Relationship accessible by the parent table in the database
	public List<InstrumentUsedType> instrumentUsedTypes = new ArrayList<InstrumentUsedType>();
	//public List<Protocol> protocols = new ArrayList<Protocol>();
	public String atomicTransfertMethod;
	
	public String shortCode;
	
	public ExperimentType() {
		super(ExperimentTypeDAO.class.getName());		
	}
	
	@JsonIgnore
	public static ExperimentTypeFinder find = new ExperimentTypeFinder();
	
	@JsonIgnore
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(models.laboratory.common.description.Level.CODE.Experiment);
	}
	
	public static class ExperimentTypeFinder extends CommonInfoType.AbstractCommonInfoTypeFinder<ExperimentType>{

		public ExperimentTypeFinder() {
			super(ExperimentTypeDAO.class);
		}
		
		public List<String> findVoidProcessExperimentTypeCode(String processTypeCode) throws DAOException{
			return ((ExperimentTypeDAO)getInstance()).findVoidProcessExperimentTypeCode(processTypeCode);
		}
		
		public List<ExperimentType> findPreviousExperimentTypeForAnExperimentTypeCode(String code) throws DAOException{
			return ((ExperimentTypeDAO)getInstance()).findPreviousExperimentTypeForAnExperimentTypeCode(code);
		}
		
		public List<ExperimentType> findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(String code, String processTypeCode) throws DAOException{
			return ((ExperimentTypeDAO)getInstance()).findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(code, processTypeCode);
		}
		
		public List<ExperimentType> findNextExperimentTypeForAnExperimentTypeCode(String code) throws DAOException{
			return ((ExperimentTypeDAO)getInstance()).findNextExperimentTypeForAnExperimentTypeCode(code);
		}
		
		public List<ExperimentType> findByCategoryCode(String categoryCode) throws DAOException{
			return ((ExperimentTypeDAO)getInstance()).findByCategoryCode(categoryCode);
		}
		
		public List<ExperimentType> findByCategoryCodes(List<String> categoryCode) throws DAOException{
			return ((ExperimentTypeDAO)getInstance()).findByCategoryCodes(categoryCode);
		}
		
		public List<ExperimentType> findByCategoryCodeAndProcessTypeCode(String categoryCode, String processTypeCode) throws DAOException{
			return ((ExperimentTypeDAO)getInstance()).findByCategoryCodeAndProcessTypeCode(categoryCode, processTypeCode);
		}
		
		public List<ExperimentType> findByCategoryCodeWithoutOneToVoid(String categoryCode) throws DAOException{
			return ((ExperimentTypeDAO)getInstance()).findByCategoryCodeWithoutOneToVoid(categoryCode);
		}	
		
		public List<ExperimentType> findByCategoryCodesWithoutOneToVoid(List<String> categoryCodes) throws DAOException{
			return ((ExperimentTypeDAO)getInstance()).findByCategoryCodesWithoutOneToVoid(categoryCodes);
		}	

		public List<ExperimentType> findByPreviousExperimentTypeCode(String previousExperimentTypeCode) throws DAOException{
			return ((ExperimentTypeDAO)getInstance()).findByPreviousExperimentTypeCode(previousExperimentTypeCode);
		}

	}
	
}
