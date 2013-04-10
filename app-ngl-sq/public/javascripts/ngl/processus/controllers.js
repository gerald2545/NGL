"use strict";

function SearchCtrl($scope, $http,$filter,datatable,baskets,basketsLocal) {
	 
	$scope.datatableConfig = {
		pagination:{
			mode:'local'
		},
			search:{
				url:'/api/containers'
			},
			otherButtons:{
				active:true
			}
	};
	
	$scope.basketsConfig = {
		idBtnModal:"Add",
		titleModal:"Add to basket",
		textModal:"",
		modalId:"newBasketModal",
		textCancelModal:"Cancel",
		manualModal:false,
		url:"/baskets",
		urlList:"/basket/experimenttype"
	};
	
	$scope.basketsLocalConfig = {
		transform: function(container){
			var processus = {};
			processus.projectCode = container.projectCodes[0];
			processus.sampleCode = container.sampleCodes[0];
			processus.containerInputCode = container.code;
			processus.codeType = container.categoryCode;
			
			//var date = new Date();
			//processus.code =  processus.codeType+"/"+processus.projectCode+"/"+processus.sampleCode+"/"+$filter('date')(date,"yyyyMMddHHMMss");//yyyyMMDDHHMMSS
			
			return processus;
		}
	};
	
	/*$scope.createProcessus = function(processusType){	
		for(var i = 0; i < $scope.datatable.displayResult.length; i++){
			if($scope.datatable.displayResult[i] && $scope.datatable.displayResult[i].selected){
				$http({
					method: 'POST',
					url: '/api/processus',
					data: {"type":processusType,"container":$scope.datatable.displayResult[i].code},
					headers: {'Content-Type': 'application/json'}
				}).success(function(data) {
					var process = data;
					var code = process.code;
					$scope.addContainer(code,JSON.stringify({ "container": $scope.datatable.displayResult[i].code}));
				});
			}
		}
	}
	
	$scope.addContainer = function(code,container){
		$http({
			method: 'POST',
			url: '/api/processus/',
			data: container,
			headers: {'Content-Type': 'application/json'}
		}).success(function(data) {
			//Call post for adding container
		});
	}*/
	
	$scope.initialiseProcessus = function(processusType){
		for(var i = 0; i < $scope.datatable.displayResult.length; i++){
			if($scope.datatable.displayResult[i] && $scope.datatable.displayResult[i].selected){
				$scope.basketsLocal.add($scope.datatable.displayResult[i]);
			}
		}
	}
	
	$scope.init = function(){
		$scope.basketsLocal.setConfig($scope.basketsLocalConfig);
		if(angular.isUndefined($scope.getDatatable())){
			$scope.tabs.push({label:"Processus",href:jsRoutes.controllers.processus.tpl.Processus.home("list").url,remove:false});//$scope.tabs[1]
			$scope.datatable = datatable($scope, $scope.datatableConfig);
			$scope.baskets = baskets($scope, $scope.basketsConfig, $scope.datatable);
			
			$scope.setDatatable($scope.datatable);	
		}else{
			$scope.datatable = $scope.getDatatable();
		}
		
		if(angular.isUndefined($scope.getForm())){
			$scope.form = {};
			$http.get('/tpl/projects/list').
				success(function(data, status, headers, config){
					$scope.projects = data;
					$scope.form.projects = data;
				});
				
			$http.get('/tpl/experimenttypes/list').
				success(function(data, status, headers, config){
					$scope.experiments = data;
					$scope.form.experiments = data;
				});
				
			$http.get('/tpl/samples/list').
				success(function(data, status, headers, config){
					$scope.samples = data;
					$scope.form.samples = data;
				});
			
			$http.get('/tpl/processType/list').
				success(function(data, status, headers, config){
					$scope.process = data;
					$scope.form.process = data;
				});
				
				$scope.setForm($scope.form);
		}else{
		
			//loading the data
			$scope.projects = $scope.form.projects;
			$scope.experiments = $scope.form.experiments;
			$scope.samples = $scope.form.samples;
			$scope.process = $scope.form.process;
			
			//selecting the previous values selected
			$scope.project = $scope.form.projects.selected;
			$scope.sample = $scope.form.samples.selected;
			$scope.experiment = $scope.form.experiments.selected;
			$scope.processus = $scope.form.process.selected;
		}
	}
	
	$scope.search = function(){
		if($scope.processus==undefined && ($scope.project!=undefined || $scope.experiment!=undefined || $scope.sample!=undefined)){ 
			$('#processus').addClass('error');
			$('#processus').popover({content:"Information needed",trigger:"manual",placement:"bottom"}).popover('show');
			
		}else{
			$('#processus').removeClass('error');
			$('#processus').popover('destroy');
			
			var jsonSearch = {};
			
			jsonSearch.containerState = 'N';
			
			if($scope.project != undefined){
				jsonSearch.projectCode = $scope.project.code;
			}
			
			if($scope.experiment != undefined){
				jsonSearch.experimentCode = $scope.experiment.code;
			}
			if($scope.sample != undefined){
				jsonSearch.containerSample = $scope.sample.code;
			}
			
			if($scope.processus != undefined){
				jsonSearch.containerProcess = $scope.processus.code;
			}
			
			$scope.datatable.search(jsonSearch);
		}
		
		//Saving the selected data
		$scope.form.projects.selected = $scope.project;
		$scope.form.experiments.selected = $scope.experiment;
		$scope.form.samples.selected = $scope.sample;
		$scope.form.process.selected = $scope.processus;
		
	}
}
SearchCtrl.$inject = ['$scope','$http','$filter', 'datatable','baskets'];

function ListCtrl($scope, $http, $routeParams,datatable,baskets,basketsLocal) {
	$scope.datatableConfig = {
		pagination:{
			mode:'local',
			numberRecordsPerPage:30
		},
		hide:{
			active:true
		}
	};
	
	$scope.init = function(){
		$(".popover").remove();
		$scope.datatable = datatable($scope, $scope.datatableConfig);
		$scope.baskets = baskets($scope, $scope.basketsConfig, $scope.datatable);
		alert(JSON.stringify($scope.basketsLocal.get()));
		$scope.datatable.setData($scope.basketsLocal.get(),$scope.basketsLocal.get().length);
	}
	
};
ListCtrl.$inject = ['$scope', '$http', '$routeParams','datatable','baskets'];