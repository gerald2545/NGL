package lims.cns.dao;

import java.util.List;

import junit.framework.Assert;
import lims.cng.services.LimsRunServices;
import lims.models.instrument.Instrument;
import lims.models.runs.EtatTacheHD;
import lims.models.runs.TacheHD;

import org.junit.Test;

import play.Logger;
import play.api.modules.spring.Spring;
import utils.AbstractTests;

public class LimsAbandonDAOTest extends AbstractTests {

	@Test
	public void getTacheHD() {
		LimsAbandonDAO  dao = Spring.getBeanOfType(LimsAbandonDAO.class);
		Assert.assertNotNull(dao);
		List<TacheHD> taches = dao.listTacheHD("20626");
		Assert.assertTrue(taches.size() == 0);
		
	}

	@Test
	public void getEtatTacheHD() {
		LimsAbandonDAO  dao = Spring.getBeanOfType(LimsAbandonDAO.class);
		Assert.assertNotNull(dao);
		List<EtatTacheHD> etaches = dao.listEtatTacheHD();
		Logger.debug("Nb Etat tache = "+etaches.size());
		Assert.assertTrue(etaches.size() > 0);
		
	}
}
