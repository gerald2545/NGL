package models.utils.dao;

import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.State;
import models.utils.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import play.Logger;
import play.cache.Cache;

/**
 * Common operations between Simple DAO et DAO Using mappingQuery
 * Must not implement an interface for transactional context because
 * If implements interface Spring creates an instance for the application and get not that class but a Java Dynamic Proxy that implements that classes interface
 * Because of this, you cannot cast that object to the original type.
 * If there aren't interfaces to implement, it will give a CGLib proxy of the class, which is basically just a runtime modified version of the class and so is assignable to the class itself
 * @author ejacoby
 *
 * @param <T>
 */
@Transactional(readOnly=false, rollbackFor=DAOException.class)
public abstract class AbstractDAO<T> {


	protected String tableName;
	protected DataSource dataSource;
	protected SimpleJdbcTemplate jdbcTemplate;
	protected SimpleJdbcInsert jdbcInsert;
	protected Class<T> entityClass;
	//Use automatic key id generation 
	//False for type because id provided by commonInfoType
	protected boolean useGeneratedKey;

	protected AbstractDAO(String tableName, Class<T> entityClass, boolean useGeneratedKey) {
		this.tableName = tableName;
		this.entityClass = entityClass;
		this.useGeneratedKey=useGeneratedKey;
	}

	@Autowired
	@Qualifier("ngl")
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);   
		if(useGeneratedKey)
			jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName).usingGeneratedKeyColumns("id");
		else
			jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(tableName);
	}

	public void remove(T value) throws DAOException
	{
		String sql = "DELETE FROM "+tableName+" WHERE id=:id";
		SqlParameterSource ps = new BeanPropertySqlParameterSource(value);
		jdbcTemplate.update(sql, ps);
	}

	public abstract List<T> findAll() throws DAOException;
		
	public abstract T findById(Long id) throws DAOException;
	
	public abstract T findByCode(String code) throws DAOException;

	public abstract List<T> findByCodes(List<String> code) throws DAOException;

	public abstract long save(T value) throws DAOException;

	public abstract void update(T value) throws DAOException;

	public Boolean isCodeExist(String code) throws DAOException
	{
		if(null == code){
			throw new DAOException("code is mandatory");
		}
		try {
			try{
				String sql = "select id from "+tableName+" WHERE code=?";
				long id =  this.jdbcTemplate.queryForLong(sql, code);
				if(id > 0){
					return Boolean.TRUE;
				}else{
					return Boolean.FALSE;
				}
			}catch (EmptyResultDataAccessException e ) {
				return Boolean.FALSE;
			}
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected String listToParameters(List<?> parameters){
		String args = "";
		for(int i = 0; i<parameters.size();i++){
			args += "?";
			if(i != (parameters.size() - 1)){
				args += ",";
			}
		}
		return args;
	}

	protected SqlParameter[] listToSqlParameters(List<?> parameters, String paramName, int type){
		SqlParameter[] params = new SqlParameter[parameters.size()];
		for(int i = 0; i<parameters.size();i++){
			params[i] =  new SqlParameter(paramName, type);
		}
		return params;
	}
	
	@SuppressWarnings("unchecked")
	protected T getObjectInCache(String code){
		if(null != code){
			try {
				String key = entityClass.toString()+"."+code;
				return (T) Cache.get(key);				
			} catch (DAOException e) {
				throw new RuntimeException(e);
			}
		}else{
			return null;
		}		
	}
	
	protected void setObjectInCache(T o, String code){
		if(null != o && null != code){
			Cache.set(entityClass.toString()+"."+code, o, 60 * 60);
		}		
	}
	
	public void cleanCache(){
		List<T> l = this.findAll();
		l.forEach(o -> {
			Cache.remove(entityClass.toString()+"."+((Model)o).code);
		});
		
	}
}
