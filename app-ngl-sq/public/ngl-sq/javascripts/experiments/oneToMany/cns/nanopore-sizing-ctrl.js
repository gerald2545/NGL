angular.module('home').controller('NanoporeSizingCtrl',['$scope', '$parse', 'atmToGenerateMany',
                                                               function($scope, $parse, atmToGenerateMany) {
	
	// NGL-1055: name explicite pour fichier CSV exporté: typeCode experience	
	var datatableConfigTubeParam = {
			//peut etre exporté CSV ??
			name: $scope.experiment.typeCode+'_PARAM'.toUpperCase(),
			columns:[   
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },	
			         {
			        	 "header":"Nb sizing",
			        	 "property":"outputNumber",
			        	 "order":false,
						 "edit":true,
						 "hide":false,
			        	 "type":"number",						
			        	 "position":8,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         }
			         
			         ],
			compact:true,
			showTotalNumberRecords:false,
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local', //or 
				active:true,
				by:'inputContainer.code'
			},
			remove:{
				active: ($scope.isEditModeAvailable() && $scope.isNewState()),
				showButton: ($scope.isEditModeAvailable() && $scope.isNewState()),
				mode:'local'
			},
			save:{
				active:true,
				withoutEdit: true,
				keepEdit:true,
				changeClass : false,
				mode:'local',
				showButton:false
			},			
			select:{
				active:true
			},
			edit:{
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
				showButton: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('IP')),
				byDefault:($scope.isCreationMode()),
				columnMode:true
			},	
			cancel : {
				active:true
			},
			extraHeaders:{
				number:1,
				dynamic:true,
			}

	};	
	
	// NGL-1055: mettre getArray et codes:'' dans filter et pas dans render
	// NGL-1055: name explicite pour fichier CSV exporté: typeCode experience
	var datatableConfigTubeConfig =  {
			name: $scope.experiment.typeCode.toUpperCase(),
			columns:[   
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "mergeCells" : true,
			        	 "position":1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },	
			        /* {
			 			"header" : Messages("containers.table.workName"),
			 			"property" : "inputContainer.properties.workName.value",
			 			"order" : true,
			 			"edit" : false,
			 			"hide" : true,
			 			"type" : "text",
			 			"position" : 1.1,
			 			"extraHeaders" : {0 : Messages("experiments.inputs")}
			 		},*/
			         {
			        	"header":Messages("containers.table.projectCodes"),
			 			"property": "inputContainer.projectCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"mergeCells" : true,
			 			"position":2,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			            "extraHeaders":{0:Messages("experiments.inputs")}
				     },
				     {
			        	"header":Messages("containers.table.sampleCodes"),
			 			"property": "inputContainer.sampleCodes",
			 			"order":true,
			 			"hide":true,
			 			"type":"text",
			 			"mergeCells" : true,
			 			"position":3,
			 			"render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	"extraHeaders":{0:Messages("experiments.inputs")}
				     },
				     {
			        	 "header":Messages("containers.table.fromTransformationTypeCodes"),
			        	 "property":"inputContainer.fromTransformationTypeCodes",
			        	 "filter":"unique | codes:'type'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "mergeCells" : true,
			 			 "render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "position":4,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.volume") + " (µL)",
			        	 "property":"inputContainer.volume.value",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"number",
			        	 "position":6,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         
			         {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"inputContainer.state.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "filter":"codes:'state'",
			        	 "position":7,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },			        
			         {
			        	 "header":Messages("containers.table.volume")+" (µL)",
			        	 "property":"outputContainerUsed.volume.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
						 "type":"number",
			        	 "position":51,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.concentration")+" (ng/µL)",
			        	 "property":"outputContainerUsed.concentration.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
						 "type":"number",
			        	 "position":52,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.quantity")+" (ng)",
			        	 "property":"outputContainerUsed.quantity.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
						 "type":"number",
			        	 "position":53,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         }, 
			         {
			        	 "header":Messages("containers.table.size")+" (pb)",
			        	 "property":"outputContainerUsed.size.value",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
						 "type":"number",
			        	 "position":54,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         }, 
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"outputContainerUsed.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "type":"text",
			        	 "position":400,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         },
			         {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"outputContainer.state.code | codes:'state'",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
						 "type":"text",
			        	 "position":500,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         } ,
			         {
			        	 "header":Messages("containers.table.storageCode"),
			        	 "property":"outputContainerUsed.locationOnContainerSupport.storageCode",
			        	 "order":true,
						 "edit":true,
						 "hide":true,
			        	 "type":"text",
			        	 "position":600,
			        	 "extraHeaders":{0:Messages("experiments.outputs")}
			         }
			         ],
			compact:true,
			pagination:{
				active:false
			},		
			search:{
				active:false
			},
			order:{
				mode:'local', //or 
				active:true,
				by:'inputContainer.code'
			},
			remove:{
				active:false,
			},
		
			save:{
				active:true,
	        	withoutEdit: true,
	        	showButton:false,
	        	changeClass:false,
	        	mode:'local',
	        	//callback:function(datatable){
	        		//copyOutputContainerUsedAttributesToContentProperties(datatable);
	        	//}
	        		
			},
			
			hide:{
				active:true
			},
			mergeCells:{
				active:true 
			},			
			edit:{
				active: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
				showButton: ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F')),
				byDefault:($scope.isCreationMode()),
				columnMode:true
			},
			messages:{
				active:false,
				columnMode:true
			},
			exportCSV:{
				active:true,
				showButton:true,
				delimiter:";",
				start:false
			},
			extraHeaders:{
				number:1,
				dynamic:true,
			}

	};	
	
	var copyOutputContainerUsedAttributesToContentProperties = function(experiment){
		for(var i=0 ; i < experiment.atomicTransfertMethods.length && experiment.atomicTransfertMethods != null; i++){
			var atm = experiment.atomicTransfertMethods[i];
			
			//si from transfotype=nanolib
			/*var concentration = atm.outputContainerUseds[0].concentration.value;
			console.log("conc",concentration);	
			$parse('outputContainerUseds.experimentProperties["ligationConcentration"]').assign(atm, {value:concentration, unit:"ng/µl"});
			*/	
			var concentration = atm.outputContainerUseds[0].concentration;
			console.log("conc",concentration);	
			$parse('outputContainerUseds[0].experimentProperties.ligationConcentrationPostSizing').assign(atm,concentration);
			
				
		var quantity = atm.outputContainerUseds[0].quantity.value;	
		$parse('outputContainerUseds[0].experimentProperties["ligationQuantityPostSizing"]').assign(atm, {value:quantity, unit:"ng"});
		
	
		//si from transfo type=frg ou ext-to-nanopore-rep-lib-depot
		var size = atm.outputContainerUseds[0].size.value;	
		$parse('outputContainerUseds[0].experimentProperties["measuredSizePostSizing"]').assign(atm, {value:size, unit:"pb"});
		
		}				
	};
	
	
	$scope.$on('save', function(e, callbackFunction) {	
		console.log("call event save on sizing");
		$scope.atmService.viewToExperiment($scope.experiment);
		copyOutputContainerUsedAttributesToContentProperties($scope.experiment);
		$scope.$emit('childSaved', callbackFunction);
	});
	
	
	$scope.$on('refresh', function(e) {
		console.log("call event refresh on sizing");
		
		var dtConfig = $scope.atmService.data.datatableParam.getConfig();
		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isNewState());
		dtConfig.remove.active = ($scope.isEditModeAvailable() && $scope.isNewState());
		dtConfig.edit.byDefault = false;
		$scope.atmService.data.datatableParam.setConfig(dtConfig);
		
		dtConfig = $scope.atmService.data.datatableConfig.getConfig();
		dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
		dtConfig.edit.showButton = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
		dtConfig.remove.active = ($scope.isEditModeAvailable() && $scope.isNewState());
		dtConfig.edit.byDefault = false;
		$scope.atmService.data.datatableConfig.setConfig(dtConfig);
		
		$scope.atmService.refreshViewFromExperiment($scope.experiment);
		$scope.$emit('viewRefeshed');
	});
	
	$scope.$on('cancel', function(e) {
		console.log("call event cancel on sizing");
		$scope.atmService.data.datatableParam.cancel();
		$scope.atmService.data.datatableConfig.cancel();
				
		if($scope.isCreationMode()){
			var dtConfig = $scope.atmService.data.datatableParam.getConfig();
			dtConfig.edit.byDefault = false;
			$scope.atmService.data.datatableParam.setConfig(dtConfig);
			
			dtConfig = $scope.atmService.data.datatableConfig.getConfig();
			dtConfig.edit.byDefault = false;
			$scope.atmService.data.datatableConfig.setConfig(dtConfig);
		}
	});
	
	$scope.$on('activeEditMode', function(e) {
		console.log("call event activeEditMode on sizing");
		$scope.atmService.data.datatableParam.selectAll(true);
		$scope.atmService.data.datatableParam.setEdit();
		
		$scope.atmService.data.datatableConfig.selectAll(true);
		$scope.atmService.data.datatableConfig.setEdit();
	});
	
	var atmService = atmToGenerateMany($scope, datatableConfigTubeParam, datatableConfigTubeConfig);
	//defined new atomictransfertMethod
	atmService.newAtomicTransfertMethod = function(){
		return {
			class:"OneToMany",
			line:"1", 
			column:"1", 				
			inputContainerUseds:new Array(0), 
			outputContainerUseds:new Array(0)
		};		
	};
	
	//defined default output unit
	atmService.defaultOutputUnit = {
			volume : "µL",
			quantity:"ng"
	}
	atmService.experimentToView($scope.experiment, $scope.experimentType);
	
	$scope.atmService = atmService;
}]);