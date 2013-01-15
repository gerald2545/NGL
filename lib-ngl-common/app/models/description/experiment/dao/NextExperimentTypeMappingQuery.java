package models.description.experiment.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import models.description.common.CommonInfoType;
import models.description.common.dao.CommonInfoTypeDAO;
import models.description.experiment.ExperimentType;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

public class NextExperimentTypeMappingQuery extends MappingSqlQuery<ExperimentType>{

	public NextExperimentTypeMappingQuery(DataSource ds)
	{
		super(ds,"SELECT id, fk_common_info_type "+
				"FROM experiment_type "+
				"JOIN next_experiment_types ON fk_next_experiment_type=id "+
				"WHERE fk_experiment_type = ? ");
		super.declareParameter(new SqlParameter("id", Type.LONG));
		compile();
	}
	@Override
	protected ExperimentType mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
		ExperimentType experimentType = new ExperimentType();
		experimentType.setId(rs.getLong("id"));
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.find(idCommonInfoType);
		experimentType.setCommonInfoType(commonInfoType);
		return experimentType;
	}

}
