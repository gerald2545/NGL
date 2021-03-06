package models.administration.authorisation.description.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import models.administration.authorisation.Permission;
import models.utils.dao.AbstractDAODefault;
import models.utils.dao.DAOException;
import play.Logger;
import play.mvc.Results;

/**
 * 
 * @author michieli
 *
 */
@Repository
public class PermissionDAO extends AbstractDAODefault<Permission>{

	protected PermissionDAO(){
		super("permission", Permission.class, true);
	}

	/*
	 * findByUserLogin()
	 */
	public List<Permission> findByUserLogin(String aLogin) throws DAOException{
		if(null == aLogin){
			throw new DAOException("login is mandatory");
		}
		String sql = getSqlCommon() + " "
				+ "INNER JOIN role_permission AS rp ON t.id = rp.permission_id "
				+ "INNER JOIN role AS r ON rp.role_id = r.id "
				+ "INNER JOIN user_role AS ur ON r.id = ur.role_id "
				+ "INNER JOIN user AS u ON ur.user_id = u.id "
				+ "WHERE u.login=?";
		//Logger.debug(sql);
		BeanPropertyRowMapper<Permission> mapper = new BeanPropertyRowMapper<Permission>(entityClass);
		return this.jdbcTemplate.query(sql, mapper, aLogin);
	}
}
