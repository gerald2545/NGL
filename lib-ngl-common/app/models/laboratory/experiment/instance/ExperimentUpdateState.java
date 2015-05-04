package models.laboratory.experiment.instance;

import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;

public class ExperimentUpdateState {

	public String nextStateProcesses;
	public List<String> processResolutionCodes;
	public String nextStateInputContainers;
	public String nextStateOutputContainers;
	public List<Process> processes ;
	public List<Container> inputContainers;
	public List<Container> outputContainers;
}
