angular.module('home').controller('OneToVoidGelMigrationCNSCtrl',['$scope', '$parse','$http',
                                                             function($scope,$parse,$http) {
	
	$scope.$parent.copyPropertiesToInputContainer = function(experiment){
		
		//nothing
	
	};
	
	$scope.$watch("gel",function(imgNew, imgOld){
		if(imgNew){			
			
			angular.forEach($scope.atmService.data.displayResult, function(dr){
				$parse('inputContainerUsed.experimentProperties.electrophoresisGelPhoto').assign(dr.data, this);
			}, imgNew);
			
		}
		angular.element('#importFile')[0].value = null;
		
	});
	
	$scope.button = {
			isShow:function(){
				return ($scope.isInProgressState() && !$scope.mainService.isEditMode())
				}	
		};
	
	var columns = $scope.atmService.data.getColumnsConfig();
	
	columns.push({
		"header" : Messages("containers.table.sampleTypes"),
		"property" : "inputContainer.contents",
		"order" : false,
		"hide" : true,
		"type" : "text",
		"position" : 7,
		"render" : "<div list-resize='cellValue | getArray:\"sampleTypeCode\" | unique' list-resize-min-size='3'>",
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}

	});
	
	columns.push({
		"header" : Messages("containers.table.concentration"),
		"property": "inputContainer.concentration",
		"render":"<span ng-bind='cellValue.value|number'/> <span ng-bind='cellValue.unit'/>",
		"order" : true,
		"edit" : false,
		"hide" : true,
		"type" : "text",
		"position" : 9,
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}
	});

	columns.push({
		"header" : Messages("containers.table.volume") + " (µL)",
		"property" : "inputContainer.volume.value",
		"order" : true,
		"edit" : false,
		"hide" : true,
		"type" : "number",
		"position" : 10,
		"extraHeaders" : {
			0 : Messages("experiments.inputs")
		}
	});
	
	
	$scope.atmService.data.setColumnsConfig(columns);

	
}]);