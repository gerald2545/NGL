angular.module('home').controller('OneToVoidQCCtrl',['$scope', '$parse','atmToSingleDatatable',
                                                             function($scope,$parse, atmToSingleDatatable) {
	 var datatableConfig = {
			name:"FDR_Void",
			columns:[
			         {
			        	 "header":Messages("containers.table.supportCode"),
			        	 "property":"inputContainer.support.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":1,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.categoryCode"),
			        	 "property":"inputContainer.support.categoryCode",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":2,
			 			 "filter":"codes:'container_support_cat'",
			 			"extraHeaders":{0:Messages("experiments.inputs")}
			         },
					 {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"inputContainer.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":3,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.fromTransformationTypeCodes"),
			        	 "property":"inputContainer.fromTransformationTypeCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			 			"render":"<div list-resize='cellValue | unique | codes:\"type\"' list-resize-min-size='3'>",
			        	 "position":4,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         {
			        	 "header":Messages("containers.table.valuation.valid"),
			        	 "property":"inputContainer.valuation.valid",
			        	 "filter":"codes:'valuation'",
			        	 "order":true,
						 "edit":false,
						 "hide":false,
			        	 "type":"text",			        	
			        	 "position":5,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
					 {
			        	 "header":Messages("containers.table.projectCodes"),
			        	 "property":"inputContainer.projectCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":6,
			        	 "render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
					 {
			        	 "header":Messages("containers.table.sampleCodes"),
			        	 "property":"inputContainer.sampleCodes",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
			        	 "position":7,
			        	 "render":"<div list-resize='cellValue' list-resize-min-size='3'>",
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         
			         {
			 			"header":Messages("containers.table.tags"),
			 			"property": "inputContainer.contents",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":8,
			 			"render":"<div list-resize='cellValue | getArray:\"properties.tag.value\" | unique' list-resize-min-size='3'>",
			 			"extraHeaders":{0:Messages("experiments.inputs")} 			
			 			
			 		},
			 		{
			 			"header":Messages("containers.table.libProcessType"),
			 			"property": "inputContainer.contents",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":9,
			 			"render":"<div list-resize='cellValue | getArray:\"properties.libProcessTypeCode.value\" | unique' list-resize-min-size='3'>",
			 			"extraHeaders":{0:Messages("experiments.inputs")}	 						 			
			 		},
			 		{
			 			"header":Messages("containers.table.size"),
			 			"property": "inputContainer.size.value",
			 			"order":false,
			 			"hide":true,
			 			"type":"text",
			 			"position":10,
			 			"extraHeaders":{0:Messages("experiments.inputs")}			 						 			
			 		},
			         
					 {
			        	 "header":Messages("containers.table.stateCode"),
			        	 "property":"inputContainer.state.code",
			        	 "order":true,
						 "edit":false,
						 "hide":true,
			        	 "type":"text",
						 "filter":"codes:'state'",
			        	 "position":11,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
			         
			         {
			        	 "header":Messages("containers.table.valuationqc.valid"),
			        	 "property":"inputContainerUsed.valuation.valid",
			        	 "filter":"codes:'valuation'",
			        	 "order":true,
						 "edit":true,
						 "hide":false,
			        	 "type":"text",
			        	 "choiceInList":true,
						 "listStyle":'bt-select',
					     "possibleValues":'lists.getValuations()',
			        	 "position":20,
			        	 "extraHeaders":{0:Messages("experiments.inputs")}
			         },
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
				by:'ContainerInputCode'
			},
			remove:{
				active: ($scope.isEditModeAvailable() && $scope.isNewState()),
				showButton: ($scope.isEditModeAvailable() && $scope.isNewState()),
			},
			save:{
				active:true,
	        	changeClass:false,
				mode:'local'
			},
			hide:{
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
				number:2,
				dynamic:true,
			}			
	};

		$scope.$on('save', function(e, callbackFunction) {	
			console.log("call event save on one-to-void");
			$scope.atmService.data.save();			
			$scope.atmService.viewToExperimentOneToVoid($scope.experiment);
			$scope.updatePropertyUnit($scope.experiment);
			$scope.copyPropertiesToInputContainer($scope.experiment); //override from child
			$scope.$emit('childSaved', callbackFunction);
		});
		
		$scope.$on('refresh', function(e) {
			console.log("call event refresh on one-to-void");		
			var dtConfig = $scope.atmService.data.getConfig();
			dtConfig.edit.active = ($scope.isEditModeAvailable() && $scope.isWorkflowModeAvailable('F'));
			dtConfig.edit.byDefault = false;
			dtConfig.remove.active = ($scope.isEditModeAvailable() && $scope.isNewState());
			$scope.atmService.data.setConfig(dtConfig);
			$scope.atmService.refreshViewFromExperiment($scope.experiment);
			$scope.$emit('viewRefeshed');
		});
		
		$scope.$on('cancel', function(e) {
			console.log("call event cancel");
			$scope.atmService.data.cancel();	
			if($scope.isCreationMode()){
				var dtConfig = $scope.atmService.data.getConfig();
				dtConfig.edit.byDefault = false;
				$scope.atmService.data.setConfig(dtConfig);
			}
		});
		
		$scope.$on('activeEditMode', function(e) {
			console.log("call event activeEditMode");
			$scope.atmService.data.selectAll(true);
			$scope.atmService.data.setEdit();
		});
		
		var atmService = atmToSingleDatatable($scope, datatableConfig, true);
		//defined new atomictransfertMethod
		atmService.newAtomicTransfertMethod = function(){
			return {
				class:"OneToVoid",
				line:"1", 
				column:"1", 				
				inputContainerUseds:new Array(0)
			};
		};
		
		atmService.experimentToView($scope.experiment, $scope.experimentType);
		
		$scope.atmService = atmService;
		

}]);