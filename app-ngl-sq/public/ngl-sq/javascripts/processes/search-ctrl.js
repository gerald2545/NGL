"use strict"

function SearchCtrl($scope,$location,$routeParams, datatable, lists,$filter,$http) {
	$scope.lists = lists;
	
	$scope.datatableConfig = {
			search:{
				url:jsRoutes.controllers.processes.api.Processes.list()
				
			},
			order:{
				by:'code'
			},
			columnsUrl:jsRoutes.controllers.processes.tpl.Processes.searchColumns().url,
			edit:{
				active:true,
				columnMode:true
			},
			save:{
				active:true,
				url:jsRoutes.controllers.processes.api.Processes.save(),
				mode:'remote'
			}
	};
	
	$scope.changeProcessTypeCode = function(){
		if($scope.form.processCategory){
			$scope.getColumns();
			$scope.search();
		}else{
			$scope.form.processType = undefined;	
		}
	};
	
	$scope.changeProject = function(){
		if($scope.form.project){
				$scope.lists.refresh.samples({projectCode:$scope.form.project.code});
			}else{
				$scope.lists.clear("samples");
			}
		
		if($scope.form.type){
			$scope.search();
		}
	};

	$scope.reset = function(){
		$scope.form = {};
		$scope.getColumns();
	};
	
	$scope.refreshSamples = function(){
		if($scope.form.projectCode){
			lists.refresh.samples({projectCode:$scope.form.projectCode});
		}
	};
	
	$scope.changeProcessCategory = function(){
		if($scope.form.processCategory){
			$scope.lists.refresh.processTypes({processCategoryCode:$scope.form.processCategory.code});
		}else{
			$scope.lists.clear("processTypes");
		}
	};
	
	$scope.search = function(){	
		if($scope.form.projectCode || $scope.form.sampleCode || $scope.form.processType || $scope.form.processCategory){
			var jsonSearch = {};
			if($scope.form.projectCode){
				jsonSearch.projectCode = $scope.form.projectCode;
			}			
			
			if($scope.form.sampleCode){
				jsonSearch.sampleCode = $scope.form.sampleCode;
			}				
			
			if($scope.form.processType){
				jsonSearch.typeCode = $scope.form.processType.code;
			}
			
			if($scope.form.processCategory){
				jsonSearch.categoryCode = $scope.form.processCategory.code;
			}
			
			$scope.datatable.search(jsonSearch);						
		}else{
			$scope.datatable.setData({},0);
		}
	};
	
	$scope.getColumns = function(){
		var typeCode = "";
		if($scope.form.processType){
			typeCode = $scope.form.processType.code;
		}
		
		$http.get(jsRoutes.controllers.processes.tpl.Processes.searchColumns().url,{params:{"typeCode":typeCode}})
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.datatable.setColumnsConfig(data);
			}
		})
		.error(function(data, status, headers, config) {
		
		});
	};
	
	//init
	if(angular.isUndefined($scope.getHomePage())){
		$scope.setHomePage('new');
		$scope.addTabs({label:Messages('processes.tabs.search'),href:jsRoutes.controllers.processes.tpl.Processes.home("new").url,remove:false});
		$scope.activeTab(0);
	}
	
	if(angular.isUndefined($scope.getForm())){
		$scope.form = {};
		$scope.setForm($scope.form);
		$scope.lists.refresh.projects();
		$scope.lists.refresh.processCategories();
	}else{
		$scope.form = $scope.getForm();			
	}
	
	$scope.datatable = datatable($scope, $scope.datatableConfig);
	
	if($scope.form.project || $scope.form.type){
		$scope.search();
	}
}

SearchCtrl.$inject = ['$scope','$location','$routeParams', 'datatable','lists','$filter','$http'];