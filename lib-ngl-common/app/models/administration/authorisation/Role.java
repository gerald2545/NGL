package models.administration.authorisation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.administration.authorisation.description.dao.RoleDAO;
import models.utils.Model;
import models.utils.dao.DAOException;

/**
 * 
 * @author michieli
 *
 */
public class Role extends Model<Role>{
	
	public String label;
	public List<Permission> permissions;
	
	@JsonIgnore
	public static RoleFinder find = new RoleFinder();
	
	public static class RoleFinder extends Finder<Role>{
		
		public RoleFinder(){
			super(RoleDAO.class.getName());
		}
		
		public List<Role> findAll() throws DAOException{
			return ((RoleDAO)getInstance()).findAll();
		}
		
		public List<Role> findByUserLogin(String aLogin) throws DAOException{
			return ((RoleDAO)getInstance()).findByUserLogin(aLogin);
		}
	}

}
