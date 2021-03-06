package models.laboratory.experiment.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.sample.description.SampleType;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.Logger;
//import play.Logger;
import play.api.modules.spring.Spring;


@Repository
public class ExperimentTypeDAO extends AbstractDAOCommonInfoType<ExperimentType>{

	public ExperimentTypeDAO() {
		super("experiment_type", ExperimentType.class,ExperimentTypeMappingQuery.class,
				"SELECT distinct c.id, c.fk_experiment_category, c.fk_common_info_type, c.atomic_transfert_method, c.short_code, c.new_sample ",
				"FROM experiment_type as c "+ sqlCommonInfoType, false);
	}
	@Override
	public long save(ExperimentType experimentType) throws DAOException
	{

		if(null == experimentType){
			throw new DAOException("ExperimentType is mandatory");
		}
		//Check if category exist
		if(experimentType.category == null || experimentType.category.id == null){
			throw new DAOException("ExperimentCategory is not present !!");
		}

		//Add commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		experimentType.id = commonInfoTypeDAO.save(experimentType);

		//Create experimentType 
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", experimentType.id);
		parameters.put("fk_common_info_type", experimentType.id);
		parameters.put("fk_experiment_category", experimentType.category.id);
		parameters.put("atomic_transfert_method", experimentType.atomicTransfertMethod);
		parameters.put("short_code", experimentType.shortCode);
		parameters.put("new_sample", experimentType.newSample);
		jdbcInsert.execute(parameters);

		//Add list instruments
		insertInstrumentUsedTypes(experimentType.instrumentUsedTypes, experimentType.id, false);
		insertSampleTypes(experimentType.sampleTypes,experimentType.id,false);
		return experimentType.id;
	}

	private void insertSampleTypes(List<SampleType> sampleTypes, Long id, boolean deleteBefore) {
		if(deleteBefore){
			removeSampleTypes(id);
		}
		if(sampleTypes!=null && sampleTypes.size()>0){
			String sql = "INSERT INTO experiment_type_sample_type (fk_experiment_type, fk_sample_type) VALUES(?,?)";
			for(SampleType sampleType:sampleTypes){
				if(sampleType == null || sampleType.id == null ){
					throw new DAOException("sampleType is mandatory");
				}
				jdbcTemplate.update(sql, id,sampleType.id);
			}
		}	
		
	}
	
	private void removeSampleTypes(Long id) {
		String sql = "DELETE FROM experiment_type_sample_type WHERE fk_experiment_type=?";
		jdbcTemplate.update(sql, id);
		
	}
	private void insertInstrumentUsedTypes(
			List<InstrumentUsedType> instrumentUsedTypes, Long id, boolean deleteBefore) throws DAOException {
		if(deleteBefore){
			removeInstrumentUsedTypes(id);
		}
		if(instrumentUsedTypes!=null && instrumentUsedTypes.size()>0){
			String sql = "INSERT INTO experiment_type_instrument_type (fk_experiment_type, fk_instrument_used_type) VALUES(?,?)";
			for(InstrumentUsedType instrumentUsedType:instrumentUsedTypes){
				if(instrumentUsedType == null || instrumentUsedType.id == null ){
					throw new DAOException("instrumentUsedType is mandatory");
				}
				jdbcTemplate.update(sql, id,instrumentUsedType.id);
			}
		}		
	}


	private void removeInstrumentUsedTypes(Long id) {
		String sql = "DELETE FROM experiment_type_instrument_type WHERE fk_experiment_type=?";
		jdbcTemplate.update(sql, id);

	}


	@Override
	public void update(ExperimentType experimentType) throws DAOException
	{
		ExperimentType expTypeDB = findById(experimentType.id);
		//Add list instruments
		insertInstrumentUsedTypes(experimentType.instrumentUsedTypes, experimentType.id, true);
		insertSampleTypes(experimentType.sampleTypes, experimentType.id, true);
	}

	@Override
	public void remove(ExperimentType experimentType) throws DAOException {
		//Remove instrument type common_info_type_instrument_type
		removeInstrumentUsedTypes(experimentType.id);
		removeSampleTypes(experimentType.id);
		//Remove experiment
		super.remove(experimentType);
		//Remove commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.remove(experimentType);
	}

	public List<ExperimentType> findByProcessTypeCode(String processTypeCode, boolean onlyPositivePosition) throws DAOException
	{
		String sql = sqlCommon + "inner join process_experiment_type as pet ON pet.fk_experiment_type=c.id "
				+"                inner join process_type as pt on pt.id = pet.fk_process_type"
				+"                inner join common_info_type as cpt on cpt.id=pt.id"
				
				+" where cpt.code = ?";
	
		if(onlyPositivePosition){
			sql = sql +" and pet.position_in_process > -1";
		}
		return initializeMapping(sql, new SqlParameter("pt.code", Types.VARCHAR)).execute(processTypeCode);		
	}
	
	
	public Integer countDistinctExperimentPositionInProcessType(String processTypeCode) throws DAOException{
		String sql = "SELECT count(distinct pet.position_in_process) "+sqlCommonFrom
				+ "inner join process_experiment_type as pet ON pet.fk_experiment_type=c.id "
				+"                inner join process_type as pt on pt.id = pet.fk_process_type"
				+"                inner join common_info_type as cpt on cpt.id=pt.id"
				
				+" where cpt.code = ? and pet.position_in_process > -1";
		
		int result = jdbcTemplate.queryForInt(sql, processTypeCode);
		
		return Integer.valueOf(result);
		
	}

	public List<String> findVoidProcessExperimentTypeCode(String processTypeCode){
		String query = "SELECT distinct t.code "+sqlCommonFrom+
				"inner join process_type as p on p.fk_void_experiment_type = t.id " +
				"inner join common_info_type as cp on p.fk_common_info_type=cp.id "+
				" where cp.code=?";

		List<String> list = jdbcTemplate.query(
				query,
				new RowMapper<String>() {

					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						String listObj = rs.getString("code");
						return listObj;
					}
				}, processTypeCode);

		return list;
	}

	public List<ExperimentType> findPreviousExperimentTypeForAnExperimentTypeCode(String code) throws DAOException{

		String sql = sqlCommon+" inner join experiment_type_node as n on n.fk_experiment_type = t.id"+
				" inner join previous_nodes as p on p.fk_previous_node = n.id "+
				" inner join experiment_type_node as np on np.id = p.fk_node "+
				" inner join  common_info_type as cp on cp.id = np.fk_experiment_type "+
				" where cp.code=?";
		return initializeMapping(sql, new SqlParameter("cp.code", Types.VARCHAR)).execute(code);
	}
	
	public List<ExperimentType> findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(String code, String processTypeCode) throws DAOException{

		String sql = sqlCommon+" inner join experiment_type_node as n on n.fk_experiment_type = t.id"+
				" inner join process_experiment_type as previous_pet on previous_pet.fk_experiment_type = t.id"+
				" 	inner join process_type as previous_pt on previous_pt.id = previous_pet.fk_process_type"+
				" 		inner join common_info_type as previous_cpt on previous_cpt.id = previous_pt.fk_common_info_type and previous_cpt.code=?"+
				" inner join experiment_type_node as previous_node on previous_node.fk_experiment_type = t.id"+
				" 	inner join previous_nodes as pn on pn.fk_previous_node = previous_node.id"+
				"		inner join experiment_type_node as current_node on current_node.id = pn.fk_node"+ 		
				" 			inner join common_info_type as current_t on current_t.id = current_node.fk_experiment_type"+ 
				"				inner join process_experiment_type as current_pet on current_pet.fk_experiment_type = current_t.id"+
				"					inner join common_info_type as current_pt on current_pt.id = current_pet.fk_process_type and current_pt.code=?"+
				" where current_t.code=? and previous_pet.position_in_process = current_pet.position_in_process-1";

		//Logger.debug(sql);
		
		return initializeMapping(sql, new SqlParameter("previous_cpt.code", Types.VARCHAR),
				new SqlParameter("current_pt.code", Types.VARCHAR),new SqlParameter("current_t.code", Types.VARCHAR)).execute(processTypeCode, processTypeCode, code);
	}
	
	public List<ExperimentType> findNextExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(String code, String processTypeCode) throws DAOException{

		String sql = sqlCommon+" inner join experiment_type_node as n on n.fk_experiment_type = t.id"+ 
		" 		inner join process_experiment_type as next_pet on next_pet.fk_experiment_type = t.id"+      
		" 			inner join process_type as next_pt on next_pt.id = next_pet.fk_process_type"+                 
		" 				inner join common_info_type as next_cpt on next_cpt.id = next_pt.fk_common_info_type and next_cpt.code=?"+
		" 		inner join experiment_type_node as previous_node on previous_node.fk_experiment_type = t.id"+        
		" 			inner join previous_nodes as pn on pn.fk_node = previous_node.id"+               
		" 				inner join experiment_type_node as current_node on current_node.id = pn.fk_previous_node"+                         
		" 		inner join common_info_type as current_t on current_t.id = current_node.fk_experiment_type"+                              
		" 			inner join process_experiment_type as current_pet on current_pet.fk_experiment_type = current_t.id"+                                      
		" 				inner join common_info_type as current_pt on current_pt.id = current_pet.fk_process_type and current_pt.code=?"+ 
		" 		where current_t.code=? and next_pet.position_in_process = current_pet.position_in_process+1";
		return initializeMapping(sql, new SqlParameter("next_cpt.code", Types.VARCHAR),
				new SqlParameter("current_pt.code", Types.VARCHAR),new SqlParameter("current_t.code", Types.VARCHAR)).execute(processTypeCode, processTypeCode, code);
	}
	
	public List<ExperimentType> findSatelliteExperimentByNodeId(Long id) throws DAOException {

		String sql = sqlCommon + "inner join satellite_experiment_type as s ON s.fk_experiment_type=t.id "+
				"and s.fk_experiment_type_node = ?";
		return initializeMapping(sql, new SqlParameter("p.fk_process_type", Type.LONG)).execute(id);
	}

	public List<ExperimentType> findByCategoryCode(String categoryCode){
		String sql = "SELECT t.code AS code, t.name AS name , t.display_order AS displayOrder, t.active as active  "+
				 sqlCommonFrom+
				" JOIN experiment_category as ec  ON c.fk_experiment_category=ec.id "
				+"where ec.code=? "
				+"ORDER by t.display_order, t.name";
		BeanPropertyRowMapper<ExperimentType> mapper = new BeanPropertyRowMapper<ExperimentType>(ExperimentType.class);
		return this.jdbcTemplate.query(sql, mapper, categoryCode);
	}
	
	public List<ExperimentType> findActiveByCategoryCode(String categoryCode){
		String sql = "SELECT t.code AS code, t.name AS name , t.display_order AS displayOrder, t.active as active  "+
				 sqlCommonFrom+
				" JOIN experiment_category as ec  ON c.fk_experiment_category=ec.id "
				+"where ec.code=? and t.active = 1 "
				+"ORDER by t.display_order, t.name";
		BeanPropertyRowMapper<ExperimentType> mapper = new BeanPropertyRowMapper<ExperimentType>(ExperimentType.class);
		return this.jdbcTemplate.query(sql, mapper, categoryCode);
	}
	
	
	public List<ExperimentType> findByCategoryCodes(List<String> categoryCodes){
		String sql = "SELECT t.code AS code, t.name AS name , t.display_order AS displayOrder, t.active as active  "+
				 sqlCommonFrom+
				" JOIN experiment_category as ec  ON c.fk_experiment_category=ec.id "
				+"where ec.code in (:list) "
				+"ORDER by t.display_order, t.name";
		BeanPropertyRowMapper<ExperimentType> mapper = new BeanPropertyRowMapper<ExperimentType>(ExperimentType.class);
		return this.jdbcTemplate.query(sql, mapper, Collections.singletonMap("list", categoryCodes));
	}
	
	public List<ExperimentType> findByCategoryCodesWithoutOneToVoid(List<String> categoryCodes){
		String sql = "SELECT t.code AS code, t.name AS name, t.display_order AS displayOrder, t.active as active  "
					+sqlCommonFrom
					+" JOIN experiment_category as ec  ON c.fk_experiment_category=ec.id "
					+"where ec.code in (:list) and c.atomic_transfert_method!='OneToVoid' "
					+"ORDER by t.display_order, t.name";
		BeanPropertyRowMapper<ExperimentType> mapper = new BeanPropertyRowMapper<ExperimentType>(ExperimentType.class);
		return this.jdbcTemplate.query(sql, mapper,Collections.singletonMap("list", categoryCodes));
	}
	
	public List<ExperimentType> findByCategoryCodeAndProcessTypeCode(String categoryCode, String processTypeCode){
		String sql = "SELECT t.code AS code, t.name AS name, t.active as active "+
				 sqlCommonFrom+
				" JOIN experiment_category as ec  ON c.fk_experiment_category=ec.id inner join process_experiment_type as p ON p.fk_experiment_type=c.id, process_type as pt " +
				"inner join common_info_type as cp on pt.fk_common_info_type=cp.id"
				+" where ec.code=? and p.fk_process_type = pt.id and cp.code=?";
		BeanPropertyRowMapper<ExperimentType> mapper = new BeanPropertyRowMapper<ExperimentType>(ExperimentType.class);
		return this.jdbcTemplate.query(sql, mapper, categoryCode, processTypeCode);
	}
	
	public List<ExperimentType> findByCategoryCodeWithoutOneToVoid(String categoryCode){
		String sql = "SELECT t.code AS code, t.name AS name, t.display_order AS displayOrder, t.active as active  "
					+sqlCommonFrom
					+" JOIN experiment_category as ec  ON c.fk_experiment_category=ec.id "
					+"where ec.code=? and c.atomic_transfert_method!='OneToVoid' "
					+"ORDER by t.display_order, t.name";
		BeanPropertyRowMapper<ExperimentType> mapper = new BeanPropertyRowMapper<ExperimentType>(ExperimentType.class);
		return this.jdbcTemplate.query(sql, mapper, categoryCode);
	}
	
	public List<ExperimentType> findNextExperimentTypeCode(String previousExperimentTypeCode) throws DAOException{
		String sql = sqlCommon+" inner join experiment_type_node as n on n.fk_experiment_type = t.id"+
				" inner join previous_nodes as p on p.fk_node = n.id "+
				" inner join experiment_type_node as np on np.id = p.fk_previous_node "+
				" inner join  common_info_type as cp on cp.id = np.fk_experiment_type "+
				" where cp.code=?";				
		//Logger.debug(sql);
		return initializeMapping(sql, new SqlParameter("cp.code", Types.VARCHAR)).execute(previousExperimentTypeCode);
	}
	
	@Deprecated
	public List<ExperimentType> findByPreviousExperimentTypeCodeInProcessTypeContext(String previousExperimentTypeCode, String processTypeCode) throws DAOException{
		String sql = sqlCommon
				+" inner join experiment_type_node as n on n.fk_experiment_type = t.id"+
				" inner join previous_nodes as p on p.fk_node = n.id "+
				" inner join experiment_type_node as np on np.id = p.fk_previous_node "+
				" inner join common_info_type as ce on ce.id = np.fk_experiment_type "+
				" inner join process_experiment_type as pet ON pet.fk_experiment_type=c.id "+
			    " inner join process_type as pt on pt.id = pet.fk_process_type "+
			    " inner join common_info_type as cp on cp.id=pt.fk_common_info_type "+
				" where ce.code=? and cp.code=?";
		return initializeMapping(sql, new SqlParameter("ce.code", Types.VARCHAR), new SqlParameter("cp.code", Types.VARCHAR)).execute(previousExperimentTypeCode, processTypeCode);
	}
	
	
	
}
