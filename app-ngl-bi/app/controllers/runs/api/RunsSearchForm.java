package controllers.runs.api;

import java.util.Date;
import java.util.List;

import play.data.format.Formats.DateTime;

import controllers.ListForm;

public class RunsSearchForm extends ListForm {
	
    public List<String> codes;
    public String code;
    
    public List<String> stateCodes;
    public String stateCode;

    public List<String> typeCodes;

    public List<String> projectCodes;
    public String projectCode;
    
    public List<String> sampleCodes;
    public String sampleCode;
    
    public String validCode;

    public Date fromDate;
    public Date toDate;
    
    public String valuationUser;
    
    public Boolean keep;
    
    public List<String> instrumentCodes;
    public List<String> runResolutionCodes;
    public List<String> laneResolutionCodes;
    public List<String> resolutionCodes;
    
    public List<String> existingFields, notExistingFields;
}
