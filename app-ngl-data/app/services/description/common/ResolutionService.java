package services.description.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Institute;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.ResolutionCategory;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;
import play.data.validation.ValidationError;
import services.description.DescriptionFactory;
import static services.description.DescriptionFactory.*;

public class ResolutionService {
	public static void main(Map<String, List<ValidationError>> errors) throws DAOException{		
		DAOHelpers.removeAll(Resolution.class, Resolution.find);
		DAOHelpers.removeAll(ResolutionCategory.class, ResolutionCategory.find);
		
		saveResolutionCategories(errors);	
		saveResolutions(errors);	
	}
		
	public static void saveResolutionCategories(Map<String,List<ValidationError>> errors) throws DAOException{
		List<ResolutionCategory> l = new ArrayList<ResolutionCategory>();
				
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème machine", "PbM"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème réactifs", "PbR")); 
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème qualité : SAV", "SAV"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème informatique", "PbI"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Informations", "Info"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème quantité", "Qté"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème indexing", "IND"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème qualité", "Qlté"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème taxon", "TAXO"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème ribosomes", "RIBO"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème mapping", "MAP"));
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Problème merging", "MERG"));
		
		//A SUPPRIMER : juste pour test
		l.add(DescriptionFactory.newSimpleCategory(ResolutionCategory.class, "Default", "default"));
		
		DAOHelpers.saveModels(ResolutionCategory.class, l, errors);
	}
	
	public static void saveResolutions(Map<String,List<ValidationError>> errors) throws DAOException{
		List<Resolution> l = new ArrayList<Resolution>();
				
	/*	l.add(newResolution("Réhybridation R1 / même séquenceur","rehybridR1",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Experiment.name())));
		l.add(newResolution("Réhybridation R1 / autre séquenceur","rehybridR1Ext",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Experiment.name())));
		l.add(newResolution("Réhybridation Read Index","rehybridRIndex",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Experiment.name())));
		l.add(newResolution("Réhybridation R2","rehybridR2",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Experiment.name())));		
		l.add(newResolution("Dépôt OK","depotOk",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Experiment.name())));
*/
		/*
		l.add(newResolution("reso1","pro_reso1",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Process.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(newResolution("reso2","pro_reso2",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Process.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS) ));
		l.add(newResolution("reso3","pro_reso3",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Process.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		
		l.add(newResolution("reso1","cont_reso1",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Container.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(newResolution("reso2","cont_reso2",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Container.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(newResolution("reso3","cont_reso3",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Container.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		
		
		l.add(newResolution("reso1","proj_reso1",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Project.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(newResolution("reso2","proj_reso2",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Project.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(newResolution("reso3","proj_reso3",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Project.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		
		l.add(newResolution("reso1","run_reso1",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Run.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(newResolution("reso2","run_reso2",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Run.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		l.add(newResolution("reso3","run_reso3",ResolutionCategory.find.findByCode(ResolutionCategory.CODE.Run.name()), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		*/
		
		//new
		l.add(newResolution("indéterminé","pbMIndetermine",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("chiller","pbMChiller",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("pelletier","pbMPelletier",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("fluidique","pbMFluidiq",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("laser","pbMLaser",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("camera","pbMCamera",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("lentille","pbMLentille",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("PE module","pbMPEmodule",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("cBot","pbMCbot",ResolutionCategory.find.findByCode("PbM"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newResolution("indéterminé","pbRIndetermine",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("flowcell","pbRFC",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("cBot","pbRCbot",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("séquencage","pbRSeq",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("indexing","pbRIndexing",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("PE module","pbRPEmodule",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("rehyb primer R1","pbRRehybR1",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("rehyb primer R2","pbRRehybR2",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("erreur réactifs","pbRErreurReac",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("rajout réactifs","pbRAjoutReac",ResolutionCategory.find.findByCode("PbR"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newResolution("intensité","savIntensite",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("densité clusters trop élevée","savDensiteElevee",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("densité clusters trop faible","savDensiteFaible",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("%PF","savPF",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("phasing","savPhasing",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("prephasing","savPrephasing",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("error rate","savErrRate",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("Q30","savQ30",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("indexing/demultiplexage","savIndDemultiplex",ResolutionCategory.find.findByCode("SAV"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newResolution("PC","pbIPC",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("écran","pbIEcran",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("espace disq insuf","pbIEspDisqInsuf",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("logiciel","pbILogiciel",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("reboot PC","pbIRebootPC",ResolutionCategory.find.findByCode("PbI"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		
		l.add(newResolution("run de validation","infoRunValidation",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("arret séquenceur","infoArretSeq",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("arret logiciel","infoArretLogiciel",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));
		l.add(newResolution("remboursement","infoRemboursement",ResolutionCategory.find.findByCode("Info"), DescriptionFactory.getInstitutes(Institute.CODE.CNS)));

		
		
		//A supprimer !
		l.add(newResolution("Default","default",ResolutionCategory.find.findByCode("default"), DescriptionFactory.getInstitutes(Institute.CODE.CNG, Institute.CODE.CNS)));
		
		DAOHelpers.saveModels(Resolution.class, l, errors);
	}

}
