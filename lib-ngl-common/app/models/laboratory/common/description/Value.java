package models.laboratory.common.description;

public class Value {

	public Long id;
	
	public String value;   
	
	public Boolean defaultValue = Boolean.FALSE;

	
	public Value() {
		super();
	}


	public Value(String value, Boolean defaultValue) {
		super();
		this.value = value;
		this.defaultValue = defaultValue;
	}

	
	
	
    
}
