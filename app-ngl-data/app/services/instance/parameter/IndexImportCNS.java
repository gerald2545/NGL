package services.instance.parameter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.Constants;
import models.LimsCNSDAO;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.parameter.Parameter;
import models.laboratory.parameter.index.IlluminaIndex;
import models.laboratory.parameter.index.Index;
import models.laboratory.parameter.index.NanoporeIndex;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class IndexImportCNS extends AbstractImportDataCNS{

	public IndexImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("IndexImportCNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createIndexIllumina(limsServices,contextError);
		createIndexNanopore(contextError);
		createIndexChromium(contextError);
		createIndexNEBNext(contextError);
	}

	
	

	public static void createIndexIllumina(LimsCNSDAO limsServices,ContextValidation contextValidation) throws SQLException, DAOException{
		
	List<Index> indexs = limsServices.findIndexIlluminaToCreate(contextValidation) ;
		
		for(Index index:indexs){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, index.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, index.code);
			}
		}
	
		InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,indexs,contextValidation);
		
	}
	
	public static void createIndexNanopore(ContextValidation contextValidation) {

		for (int i = 1; i <= 12; i++) {
			Index index = getNanoporeIndex(i);
			if (!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class,
					index.code)) {
				InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME, index, contextValidation);
			}
		}

	}

	private static Index getNanoporeIndex(int i) {
		Index index = new NanoporeIndex();
		String code = (i < 10)?"NB0"+i:"NB"+i;
		index.code = code;
		index.name = code;
		index.shortName = code;
		index.sequence = code;
		index.categoryCode = "SINGLE-INDEX";
		index.supplierName = new HashMap<String,String>();
		index.supplierName.put("oxfordNanopore", code);
		index.traceInformation=new TraceInformation("ngl-data");
		return index;
	}
	
	public static void createIndexChromium(ContextValidation contextValidation) throws DAOException{
		
		IndexImportUtils.getChromiumIndex().forEach((k,v)-> {
			Index index = getChromiumIndex(k,  v);
			if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, index.code)){
				//Logger.info("creation index : "+ index.code +" / "+ index.categoryCode);
				InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,index,contextValidation);
			} else {
				//Logger.info("index : "+ index.code + " already exists !!");
			}
		});			
		
	}

	private static Index getChromiumIndex(String code, String seq) {
		Index index = new IlluminaIndex();
		
		index.code = code;
		index.name = code;
		index.shortName = code;
		index.sequence = seq ;  //Voir plus tard: il y a 4 sequences pour les POOL-INDEX...Chromium
		index.categoryCode = "POOL-INDEX";
		index.supplierName = new HashMap<String,String>();
		index.supplierName.put("10x Genomics", code);
		index.traceInformation=new TraceInformation("ngl-data");
		
		return index;
	}
	
	
	private void createIndexNEBNext(ContextValidation contextValidation) {
		List<Index> indexes = new ArrayList<Index>();
		
		indexes.add(getNEBNextIndex("NEBNext1", "ATCACG", "IND1"));
		indexes.add(getNEBNextIndex("NEBNext2", "CGATGT", "IND2"));
		indexes.add(getNEBNextIndex("NEBNext3", "TTAGGC", "IND3"));
		indexes.add(getNEBNextIndex("NEBNext4", "TGACCA", "IND4"));
		indexes.add(getNEBNextIndex("NEBNext5", "ACAGTG", "IND5"));
		indexes.add(getNEBNextIndex("NEBNext6", "GCCAAT", "IND6"));
		indexes.add(getNEBNextIndex("NEBNext7", "CAGATC", "IND7"));
		indexes.add(getNEBNextIndex("NEBNext8", "ACTTGA", "IND8"));
		indexes.add(getNEBNextIndex("NEBNext9", "GATCAG", "IND9"));
		indexes.add(getNEBNextIndex("NEBNext10", "TAGCTT", "IND10"));
		indexes.add(getNEBNextIndex("NEBNext11", "GGCTAC", "IND11"));
		indexes.add(getNEBNextIndex("NEBNext12", "CTTGTA", "IND12"));
		
		indexes.forEach(index-> {
			if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, index.code)){
				//Logger.info("creation index : "+ index.code +" / "+ index.categoryCode);
				InstanceHelpers.save(InstanceConstants.PARAMETER_COLL_NAME,index,contextValidation);
			} else {
				//Logger.info("index : "+ index.code + " already exists !!");
			}
		});			
	}
	
	private static Index getNEBNextIndex(String code, String seq, String shortName) {
		Index index = new IlluminaIndex();
		
		index.code = code;
		index.name = code;
		index.shortName = shortName;
		index.sequence = seq ; 
		index.categoryCode = "SINGLE-INDEX";
		index.supplierName = new HashMap<String,String>();
		index.supplierName.put("NEB", code);
		index.traceInformation=new TraceInformation("ngl-data");
		
		return index;
	}
}
