package models.description.content.dao;

import javax.sql.DataSource;

import models.description.content.SampleType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SampleTypeDAO {

	private SampleTypeMappingQuery sampleTypeMappingQuery;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.sampleTypeMappingQuery = new SampleTypeMappingQuery(dataSource);
	}
	
	public SampleType findById(long id)
	{
		return this.sampleTypeMappingQuery.findObject(id);
	}
}
