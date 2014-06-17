package controllers.analyses.tpl;

import play.Routes;
import play.mvc.Result;
import views.html.analyses.details;
import views.html.analyses.home;
import views.html.analyses.search;
import controllers.CommonController;
import controllers.APICommonController;

/**
 * Controller around Run object
 * @author galbini
 *
 */
public class Analyses extends CommonController {
	
	public static Result home(String homecode) {
		return ok(home.render(homecode));
	}
	
	public static Result get(String code) {
		return ok(home.render("search")); 
	}
	
	public static Result valuation(String code) {
		return ok(home.render("valuation")); 
	}
	
	public static Result search(String type) {
		return ok(search.render());
	}
	
	public static Result details() {
		return ok(details.render());
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.analyses.tpl.routes.javascript.Analyses.home(),  
  	    		controllers.analyses.tpl.routes.javascript.Analyses.get(), 
  	    		controllers.analyses.tpl.routes.javascript.Analyses.valuation(),
  	    		controllers.analyses.api.routes.javascript.Analyses.get(),
  	    		controllers.analyses.api.routes.javascript.Analyses.list(),
  	    		controllers.analyses.api.routes.javascript.Analyses.list(),
  	    		controllers.analyses.api.routes.javascript.Analyses.state(),
  	    		controllers.analyses.api.routes.javascript.Analyses.stateBatch(),
  	    		controllers.analyses.api.routes.javascript.Analyses.valuation(),
  	    		controllers.analyses.api.routes.javascript.Analyses.valuationBatch(),
  	    		controllers.analyses.api.routes.javascript.Analyses.properties(),
  	    		controllers.analyses.api.routes.javascript.Analyses.propertiesBatch(),
  	    		//controllers.commons.api.routes.javascript.Resolutions.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
  	      		controllers.valuation.api.routes.javascript.ValuationCriterias.list(),
  	      		controllers.valuation.api.routes.javascript.ValuationCriterias.get(),
	    		controllers.projects.api.routes.javascript.Projects.list(),	    		
  	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
  	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
  	      		controllers.commons.api.routes.javascript.Users.list(),
  	      		controllers.commons.api.routes.javascript.Resolutions.list()
	      		
  	      )	  	      
  	    );
  	  }
	
}
