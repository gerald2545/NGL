"use strict"

function SearchCtrl($scope,$location,$routeParams,$filter, datatable, lists) {
	
	$scope.datatableConfig = {	
			columns:[
			{
				"header":Messages("containers.table.code"),
				"property":"code",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("experiments.table.projectCodes"),
				"property":"projectCodes",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("experiments.table.volume.value"),
				"property":"mesuredVolume.value",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.sampleCodes"),
				"property":"sampleCodes",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.valid"),
				"property":"valid",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.stateCode"),
				"property":"stateCode",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.categoryCode"),
				"property":"categoryCode",
				"order":true,
				"hide":true,
				"type":"text"
			},
			{
				"header":Messages("containers.table.fromExperimentTypeCodes"),
				"property":"typeCode",
				"order":true,
				"hide":true,
				"type":"text"
			}
			],
			show:{
				active:true,
				add :function(line){
					$scope.addTabs({label:line.code,href:"/experiments/edit/"+line.code,remove:true});
				}
			},
			search:{
				url:jsRoutes.controllers.experiments.api.Experiments.list()
				
			},
			order:{
				by:'code'
			},
			edit:{
				active:true
			}
		};
	
	$scope.lists = lists;
	
	$scope.changeTypeCode = function(){
		$scope.search();
	}
	
	var search = function(values, query){
		var queryElts = query.split(',');
		
		var lastQueryElt = queryElts.pop();
		
		var output = [];
		angular.forEach($filter('filter')(values, lastQueryElt), function(value, key){
			if(queryElts.length > 0){
				this.push(queryElts.join(',')+','+value.code);
			}else{
				this.push(value.code);
			}
		}, output);
		
		return output;
	}
	
	$scope.changeExperimentType = function(){
		this.search();
	}
	
	$scope.changeProcessCategory = function(){
		$scope.form.experimentType = undefined;
		$scope.form.experimentCategory = undefined;
		$scope.form.processType = undefined;
		$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory.code});
	}
	
	$scope.changeProcessType = function(){
		$scope.form.experimentType = undefined;
		$scope.form.experimentCategory = undefined;
	}
	
	
	$scope.changeExperimentCategory = function(){
		$scope.form.experimentType = undefined;
		if($scope.form.processType && $scope.form.experimentCategory){
			$scope.lists.refresh.experimentTypes({categoryCode:$scope.form.experimentCategory.code, processTypeCode:$scope.form.processType.code}, true);
		}else if($scope.form.experimentCategory){
			$scope.lists.refresh.experimentTypes({categoryCode:$scope.form.experimentCategory.code}, true);
		}
	}
	
	$scope.searchProjects = function(query){
		return search(lists.getProjects(), query);
	}

	$scope.searchSamples = function(query){
		return search(lists.getSamples(), query);
	}
	
	$scope.reset = function(){
		$scope.form = {
				
		}
	}
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
		}
	};
	
	
	
	$scope.init = function(){
		
		if(angular.isUndefined($scope.getHomePage())){
			$scope.setHomePage('search');
			$scope.addTabs({label:Messages('experiment.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("search").url,remove:false});
			$scope.activeTab(0);
		}
		
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {};
			$scope.setForm($scope.form);
			//$scope.form.typeCodes.options = $scope.comboLists.getExperimentTypes().query();
			
			//$scope.lists.refresh.types({objectTypeCode:"Experiment"});
			$scope.lists.refresh.types({objectTypeCode:"Process"}, true);
			$scope.lists.refresh.processCategories();
			$scope.lists.refresh.experimentCategories();
			$scope.lists.refresh.projects();
			
		}else{
			$scope.form = $scope.getForm();			
		}
		
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		if($scope.form.type){
			$scope.search();
		}
	}
	
	$scope.search = function(){		
			var jsonSearch = {};			

			var jsonSearch = {};	
			if($scope.form.projectCodes){
				jsonSearch.projectCodes = $scope.form.projectCodes.split(",");
			}			
			if($scope.form.sampleCodes){
				jsonSearch.sampleCodes = $scope.form.sampleCodes.split(",");
			}			
			if($scope.form.processType){
				jsonSearch.processTypeCode = $scope.form.processType.code;
			}		
			
			if($scope.form.type){
				jsonSearch.typeCode = $scope.form.type.code;
			}
			
			
			if($scope.form.fromDate)jsonSearch.fromDate = moment($scope.form.fromDate).valueOf();
			if($scope.form.toDate)jsonSearch.toDate = moment($scope.form.toDate).valueOf();	
			
			$scope.datatable.search(jsonSearch);						
	}
}

SearchCtrl.$inject = ['$scope','$location','$routeParams','$filter', 'datatable','lists'];