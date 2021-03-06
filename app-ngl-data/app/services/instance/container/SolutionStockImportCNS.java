package services.instance.container;

import java.sql.SQLException;

import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;

public class SolutionStockImportCNS extends ContainerImportCNS {

	public SolutionStockImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("Container Solution stock CNS", durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
			createContainers(contextError,"pl_MaterielmanipToNGL @emnco=14 ","tube","IW-P","solution-stock","pl_ContentFromContainer @matmanom=?");
	}
}
