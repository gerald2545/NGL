package models.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.laboratory.common.description.AbstractCategory;
import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureUnit;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.description.StateCategory;
import models.laboratory.common.description.Value;
import models.laboratory.common.description.dao.ObjectTypeDAO;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.sample.description.ImportCategory;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.Model.Finder;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.validation.ValidationError;
import play.db.DB;
import play.api.modules.spring.Spring;

public class DescriptionHelper {


	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Boolean required, Boolean active, Boolean choiceInList,
			List<Value> possiblesValues , Class<?> type, String description, String  displayFormat, int displayOrder
			,boolean propagation, String inOut, String defaultValue 
			,String level, MeasureCategory measureCategory, MeasureUnit measureValue) {
		PropertyDefinition propertyDefinition = new PropertyDefinition();
		propertyDefinition.code = keyCode;
		propertyDefinition.name = keyName;
		propertyDefinition.required = required;
		propertyDefinition.active = active;
		propertyDefinition.choiceInList = choiceInList;
		propertyDefinition.possibleValues = possiblesValues;
		propertyDefinition.type = type.getName();
		propertyDefinition.description=description;
		propertyDefinition.displayFormat=displayFormat;
		propertyDefinition.displayOrder=displayOrder;
		propertyDefinition.propagation=propagation;
		//propertyDefinition.inOut=inOut;
		propertyDefinition.defaultValue=defaultValue;
		//propertyDefinition.level="current";
		propertyDefinition.measureCategory=measureCategory;
		propertyDefinition.saveMeasureValue=measureValue;		

		return propertyDefinition;
	}


	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Boolean required, Boolean active, Class<?> type) {
		return getPropertyDefinition(keyCode, keyName, required, active, false, null, type, null, null, 0,Boolean.FALSE,null,null,null,null,null);
	}

	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Class<?> type) {
		return getPropertyDefinition(keyCode, keyName, Boolean.TRUE, Boolean.TRUE, type);
	}

	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Class<?> type, Boolean required,MeasureCategory measureCategory,MeasureUnit measureValue) {
		return getPropertyDefinition(keyCode, keyName, required, Boolean.TRUE, false, null, type, null, null, 0,Boolean.FALSE,null,null,null,measureCategory,measureValue);

	}

	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Class<?> type,MeasureCategory measureCategory,MeasureUnit measureValue) {
		return getPropertyDefinition(keyCode, keyName, Boolean.TRUE, Boolean.TRUE, false, null, type, null, null, 0,Boolean.FALSE,null,null,null,measureCategory,measureValue);

	}

	public static PropertyDefinition getPropertyDefinition(String keyCode, String keyName, Class<?> type,List<Value> possiblesValues) {
		return getPropertyDefinition(keyCode, keyName, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE,possiblesValues, type, null, null, 0,Boolean.FALSE,null,null,null,null,null);
	}


	public static PropertyDefinition getPropertyDefinition(String keyCode,
			String keyName, Boolean false1, Class<String> class1,
			List<Value> listIndex) {

		return getPropertyDefinition(keyCode, keyName, false1, Boolean.TRUE, Boolean.TRUE,listIndex, class1, null, null, 0,Boolean.FALSE,null,null,null,null,null);

	}



	public static CommonInfoType getCommonInfoType(String code,String name,String collectionName, List<State> variableStates,List<PropertyDefinition> propertyDefinitions, List<Resolution> resolutions,String objectType) throws DAOException {

		CommonInfoType commonInfoType=new CommonInfoType();
		commonInfoType.name=name;
		commonInfoType.code=code;
		commonInfoType.states = variableStates;
		commonInfoType.resolutions = resolutions;
		commonInfoType.propertiesDefinitions=propertyDefinitions;
		ObjectTypeDAO objectTypeDAO=Spring.getBeanOfType(ObjectTypeDAO.class);
		commonInfoType.objectType=objectTypeDAO.findByCode(objectType);

		return commonInfoType;
	}


	public static SampleCategory getSampleCategory(String code,String name) throws DAOException{

		SampleCategory sampleCategory=SampleCategory.find.findByCode(code);
		Logger.debug("SampleCategory "+sampleCategory); 
		if(sampleCategory==null){
			sampleCategory=new SampleCategory();
			sampleCategory.code=code;
			sampleCategory.name=name;
		}
		return sampleCategory;
	}


	public static State getState(String code) throws InstantiationException, IllegalAccessException, ClassNotFoundException{

		State state = null;
		try {
			state = State.find.findByCode(code);

			if(state==null){
				state = new State();
				state.active=true;
				state.code=code;
				state.name=code;
				state.position=0;
				
			}


		} catch (DAOException e) {

			e.printStackTrace();
		}
		return state;
	}

	public static Resolution getResolution(String code){

		Resolution resolution = null;
		try {
			resolution = Resolution.find.findByCode(code);

			if(resolution==null){
				resolution = new Resolution();
				resolution.code=code;
				resolution.name=code;
				//resolution.save();
			}


		} catch (DAOException e) {

			e.printStackTrace();
		}
		return resolution;
	}


	public static SampleType getSampleType(String codeType, String nameType,String codeCategory,List<PropertyDefinition> propertyDefinitions) throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{

		List<State> states = new ArrayList<State>();
		states.add(DescriptionHelper.getState("Etat"+codeType));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(DescriptionHelper.getResolution("Resolution"+codeType));

		CommonInfoType commonInfoType = DescriptionHelper.getCommonInfoType(codeType,nameType, "Sample", null, null, null, "Sample");
		commonInfoType.propertiesDefinitions=propertyDefinitions;
		commonInfoType.resolutions=resolutions;
		commonInfoType.states=states;

		SampleType sampleType = new SampleType();
		sampleType.setCommonInfoType(commonInfoType);

		sampleType.category=getCategory(SampleCategory.class,codeCategory);

		return sampleType;
	}


	public static ExperimentType getExperimentType(String codeType,
			String nameType, String codeCategory,
			List<PropertyDefinition> propertyDefinitions) throws DAOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		List<State> states = new ArrayList<State>();
		states.add(DescriptionHelper.getState("New"));
		states.add(DescriptionHelper.getState("In Progress"));
		states.add(DescriptionHelper.getState("Finish"));

		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(DescriptionHelper.getResolution("Resolution"+codeType));

		CommonInfoType commonInfoType = DescriptionHelper.getCommonInfoType(codeType,nameType, "Experiment", null, null, null, "Experiment");
		commonInfoType.propertiesDefinitions=propertyDefinitions;
		commonInfoType.states=states;
		commonInfoType.resolutions=resolutions;

		ExperimentType experimentType = new ExperimentType();
		experimentType.setCommonInfoType(commonInfoType);

		experimentType.category=getExperimentCategory(codeCategory);

		return experimentType;
	}

	public static ExperimentType getExperimentType(String codeType,
			String nameType, String codeCategory,
			List<PropertyDefinition> propertyDefinitions,
			List<InstrumentUsedType> instrumentUsedTypes,
			List<Protocol> protocol, List<State> states,
			List<Resolution> resolutions) throws DAOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {

		CommonInfoType commonInfoType = DescriptionHelper.getCommonInfoType(
				codeType, nameType, "Experiment", states, propertyDefinitions,
				resolutions, "Experiment");

		ExperimentType experimentType = new ExperimentType();
		experimentType.setCommonInfoType(commonInfoType);
		experimentType.category = getExperimentCategory(codeCategory);
		experimentType.instrumentUsedTypes = instrumentUsedTypes;
		return experimentType;
	}


	public static MeasureCategory getMeasureCategory(String code,String name,String codeValue,String valueValue){

		MeasureCategory measureCategory = null;
		try {
			measureCategory = MeasureCategory.find.findByCode(code);
			if(measureCategory==null){
				measureCategory = new MeasureCategory();
				measureCategory.code=code;
				measureCategory.name=name;

			}

		} catch (DAOException e) {

			e.printStackTrace();
		}
		return measureCategory;
	}


	public static List<Value> getListFromProcedureLims(String procedure){
		List<Value> listIndex=new ArrayList<Value>();
		try{
			Connection connection=DB.getConnection("lims");
			Statement stm=connection.createStatement();

			ResultSet resultSet=stm.executeQuery(procedure);
			while(resultSet.next()){
				Value value =new Value();
				value.code=resultSet.getString(1);
				value.value=resultSet.getString(1);
				listIndex.add(value);
			}
			stm.close();
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listIndex;
	}

	public static ImportType getImportType(String codeImport, String nameImport,String codeCategory,List<PropertyDefinition> propertyDefinitions) throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{

		List<State> states = new ArrayList<State>();
		states.add(DescriptionHelper.getState("Etat"+codeImport));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(DescriptionHelper.getResolution("Resolution"+codeImport));

		CommonInfoType commonInfoType = DescriptionHelper.getCommonInfoType(codeImport,nameImport, "Import", null, null, null, "Import");
		commonInfoType.propertiesDefinitions=propertyDefinitions;
		commonInfoType.states=states;
		commonInfoType.resolutions=resolutions;

		ImportType importType = new ImportType();
		importType.setCommonInfoType(commonInfoType);

		importType.category=getCategory(ImportCategory.class,codeCategory);

		return importType;
	}


	public static ProjectType getProjectType(String codeProject, String nameProject,String codeCategory,List<PropertyDefinition> propertyDefinitions) throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{

		List<State> states = new ArrayList<State>();
		states.add(DescriptionHelper.getState("Etat"+codeProject));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(DescriptionHelper.getResolution("Resolution"+codeProject));

		CommonInfoType commonInfoType = DescriptionHelper.getCommonInfoType(codeProject,nameProject, "Project", null, null, null, "Project");
		commonInfoType.propertiesDefinitions=propertyDefinitions;
		commonInfoType.states=states;
		commonInfoType.resolutions=resolutions;

		ProjectType projectType =new ProjectType();
		projectType.setCommonInfoType(commonInfoType);

		projectType.category=getCategory(ProjectCategory.class,codeCategory);

		return projectType;
	}


	public static <T extends AbstractCategory<T>> T getCategory(Class<T> type,String codeCategory) throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException{

		Finder<T> find = new Finder<T>(type.getName().replaceAll("description", "description.dao")+"DAO");
		T objectCategory=find.findByCode(codeCategory);
		if(objectCategory==null){
			Logger.debug("category not find :"+codeCategory);
			objectCategory=(T) Class.forName(type.getName()).newInstance();
			objectCategory.code=codeCategory;
			objectCategory.name=codeCategory;
		}

		return objectCategory;
	}

	public static ExperimentCategory getExperimentCategory(String codeCategory) throws DAOException {
		Logger.debug("Find experiment category :"+codeCategory);
		ExperimentCategory experimentCategory = ExperimentCategory.find.findByCode(codeCategory);

		if(experimentCategory==null){
			Logger.debug("experiment category not find :"+codeCategory);
			experimentCategory=new ExperimentCategory();
			experimentCategory.code=codeCategory;
			experimentCategory.name=codeCategory;
		}
		return experimentCategory;
	}


	
	
	
	public static <T extends Model> List<T> arrayToListType(Class<T> type, String[] listString) throws DAOException{
		List<T> listType = new ArrayList<T>();
		Finder<T> find = new Finder<T>(type.getName().replaceAll("description", "description.dao")+"DAO");
		T object;
		for(String i:listString){
			object=find.findByCode(i);
			if(object!=null)
			listType.add(object);
		}
		
		return listType;
	}


	public static InstrumentUsedType getInstrumentUsedType(String instrumentUsedTypeCode, String instrument, String instrumentCategory,List<PropertyDefinition> propertyDefinitions) throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		List<State> states = new ArrayList<State>();
		states.add(DescriptionHelper.getState("Etat"+instrumentUsedTypeCode));
		List<Resolution> resolutions = new ArrayList<Resolution>();
		resolutions.add(DescriptionHelper.getResolution("Resolution"+instrumentUsedTypeCode));

		CommonInfoType commonInfoType = DescriptionHelper.getCommonInfoType(instrumentUsedTypeCode,instrumentUsedTypeCode, "Instrument", null, null, null, "Instrument");
		commonInfoType.propertiesDefinitions=propertyDefinitions;

		InstrumentUsedType instrumentUsedType =new InstrumentUsedType();
		instrumentUsedType.setCommonInfoType(commonInfoType);

		instrumentUsedType.instruments=new ArrayList<Instrument>();
		Instrument ins=new Instrument();
		ins.code=instrument;
		ins.name=instrument;
		instrumentUsedType.instruments.add(ins);

		
		instrumentUsedType.category=getCategory(InstrumentCategory.class, instrumentCategory);

		return instrumentUsedType;
	}

	public static InstrumentUsedType getInstrumentUsedType(String instrumentUsedTypeCode, List<Instrument> instrument, String instrumentCategory,List<PropertyDefinition> propertyDefinitions) throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException {

		InstrumentUsedType instrumentUsedType=DescriptionHelper.getInstrumentUsedType(instrumentUsedTypeCode, "", instrumentCategory, propertyDefinitions);

		instrumentUsedType.instruments=instrument;

		return instrumentUsedType;
	}


	public static State getState(String stateCode, String stateName, String level) throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		State state = new State();
		state.code=stateCode;
		state.name=stateName;
		return state;
	}


	public static ProcessType getProcessType(String typeCode, String categoryCode, List<PropertyDefinition> propertyDefinitions,List<ExperimentType> experimentTypes, 
			ExperimentType voidExperimentType, ExperimentType firstExperimentType, ExperimentType lastExperimentType,List<State> variableStates, List<Resolution> resolutions) throws DAOException, InstantiationException, IllegalAccessException, ClassNotFoundException  {
		ProcessType processType = new ProcessType();
		processType.setCommonInfoType(DescriptionHelper.getCommonInfoType(typeCode,typeCode, "Process", variableStates, propertyDefinitions, resolutions, "Process"));
		processType.experimentTypes=experimentTypes;
		processType.category=getCategory(ProcessCategory.class, categoryCode);
		processType.voidExperimentType=voidExperimentType;
		processType.firstExperimentType=firstExperimentType;
		processType.lastExperimentType=lastExperimentType;
		return processType;
	}

	public static Instrument getInstrument(String name) {
		Instrument ins=new Instrument();
		ins.code=name;
		ins.name=name;
		return ins;
	}
}
