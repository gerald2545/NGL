package services.instance.protocol;

import static services.instance.InstanceFactory.newProtocol;

import java.util.ArrayList;
import java.util.List;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.protocol.instance.Protocol;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import play.Logger;
import services.instance.InstanceFactory;
import validation.ContextValidation;

public class ProtocolServiceCNG {	

	private final static String institute = "CNG";
	public static void main(ContextValidation ctx) {	

		Logger.info("Start to create protocols collection for "+institute+"...");
		Logger.info("Remove protocol");
		removeProtocols(ctx);
		Logger.info("Save protocols ...");
		saveProtocols(ctx);
		Logger.info(institute+" Protocols collection creation is done!");
	}

	// FDS 10/02/2016 vieux code obsolete ????
	/*
	public static void saveProtocolsCNG(ContextValidation ctx){
		
		try {
			List<models.laboratory.experiment.description.Protocol> lpOld = models.laboratory.experiment.description.Protocol.find.findByInstituteCode(institute);
			for(int i=0; i<lpOld.size();i++ ){
				Protocol protocole = new Protocol();				
				protocole.code = lpOld.get(i).code;
				protocole.name = lpOld.get(i).name;
				protocole.filePath = lpOld.get(i).filePath;
				protocole.version = lpOld.get(i).version;
				protocole.categoryCode = lpOld.get(i).category.code;					
				List<CommonInfoType> lCommonInfoType = CommonInfoType.find.findByProtocolCode(lpOld.get(i).code);
				for(CommonInfoType cit:lCommonInfoType){
					protocole.experimentTypeCodes.add(cit.code);
				}				
				InstanceHelpers.save(InstanceConstants.PROTOCOL_COLL_NAME, protocole,ctx);
				Logger.debug("");
			}
		} catch (DAOException e) {
			Logger.error("Protocol importation error from SQL: "+e.getMessage());
		}
		

	} */
	
	private static void removeProtocols(ContextValidation ctx) {
		MongoDBDAO.delete(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, DBQuery.empty());
	}

	public static void saveProtocols(ContextValidation ctx){		
		List<Protocol> lp = new ArrayList<Protocol>();
		
		//-------Experiences de transformation
		lp.add(newProtocol("PrepFC_CBot_ptr_sox139_1","PrepFC_CBot_ptr_sox139_1","","1","production", 
				InstanceFactory.setExperimentTypeCodes("prepa-flowcell")));
		
		// meme protocole pour une exp de transformation et controle qualité ???????????
		lp.add(newProtocol("1a-sop-ill-pcrfree","1A_SOP_ILL_PCRfree_270116", "?","1","production",
				InstanceFactory.setExperimentTypeCodes("prep-pcr-free",
													   "labchip-migration-profile" )));
		
		lp.add(newProtocol("1a-sop-ill-pcrfree-dap-plate","1A_SOP_ILL_PCRfree_DAPplate", "?","1","production",
				InstanceFactory.setExperimentTypeCodes("prep-pcr-free",
													   "labchip-migration-profile" )));
		
		//10/08/2016 protocole  pour toutes les experiences du processus X5_WG NANO
		lp.add(newProtocol("1a-sop-ill-nano-240214","1A_SOP_ILL_NANO_240214", "?","1","production",
				InstanceFactory.setExperimentTypeCodes("prep-pcr-free",
													   "pcr-and-purification",
													   "lib-normalization",
													   "prepa-fc-ordered",
													   "illumina-depot" )));
		
		// protocoles communs a plusieurs Experiment Types.....=> en attente....
		lp.add(newProtocol("sop-1","SOP 1","?","1","production", 
				InstanceFactory.setExperimentTypeCodes("illumina-depot",
													   "denat-dil-lib")));
		
		lp.add(newProtocol("sop-en-attente","SOP en attente","?","1","production", 
				InstanceFactory.setExperimentTypeCodes("prepa-fc-ordered",
													   "lib-normalization", 
													   "normalization-and-pooling",
													   "aliquoting",
													   "pool", 
													   "pcr-and-purification",
													   "library-prep",
													   "tubes-to-plate",
													   "plate-to-tubes",
													   "plates-to-plate",
													   "x-to-plate")));

		
		//-------Experiences de Control Qualité
		lp.add(newProtocol("7-sop-miseq","7_SOP_Miseq","?","1","production", InstanceFactory.setExperimentTypeCodes("miseq-qc")));
		lp.add(newProtocol("3a-kapa-qPCR-240715","3A_KAPA_qPCR_240715", "?","1","production",InstanceFactory.setExperimentTypeCodes("qpcr-quantification")));
		
		for(Protocol protocole:lp){
			InstanceHelpers.save(InstanceConstants.PROTOCOL_COLL_NAME, protocole,ctx);
			Logger.debug("protocol '"+protocole.name + "' saved..." );
		}
	}
	
}