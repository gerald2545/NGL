package controllers.analyses.api;

import java.util.List;

import controllers.ListForm;



public class AnalysesSearchForm extends ListForm{
	
	public List<String> stateCodes;
	public String stateCode;
	
	public List<String> projectCodes;
	public String projectCode;
	
	public List<String> sampleCodes;
	public String sampleCode;
	
	
	public String validCode;
	public List<String> resolutionCodes;
	public List<String> typeCodes;
	
		
	public String regexCode;
	public String regexSampleCode;
	
	public String analyseValuationUser;
    
   
	 
}