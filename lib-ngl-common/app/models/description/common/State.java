package models.description.common;

import java.util.HashMap;
import java.util.Map;

import models.description.IDynamicType;
import models.description.common.dao.StateDAO;
import play.modules.spring.Spring;

/**
 * Value of the possible state of type
 * must implement IDynamicType interface in order to be used in GenericType (temporary)
 * @author ejacoby
 *
 */
public class State implements IDynamicType{

	public Long id;
	
	public String name;
	
	public String code;
	
	public boolean active;
	
	public Integer priority;
	
	public State() {
		super();
	}

	public State(String name, String code, boolean active, Integer priority) {
		super();
		this.name = name;
		this.code = code;
		this.active = active;
		this.priority = priority;
	}

	public static Map<String, String> getMapPossibleStates()
	{
		Map<String, String> mapPossibleStates = new HashMap<String, String>();
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		for(State possibleState : stateDAO.findAll()){
			mapPossibleStates.put(possibleState.id.toString(), possibleState.name);
		}
		return mapPossibleStates;
	}

	@Override
	public CommonInfoType getInformations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getIdType() {
		return id;
	}

	@Override
	public IDynamicType findById(long id) {
		StateDAO stateDAO = Spring.getBeanOfType(StateDAO.class);
		return stateDAO.findById(id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	
}
