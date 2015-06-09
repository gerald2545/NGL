angular.module('home').controller('CreateNewCtrl',['$scope','$sce', '$window','$http','lists','$parse','$q','$position','$routeParams','$location','mainService','tabService','$filter','datatable', function($scope,$sce,$window, $http,lists,$parse,$q,$position,$routeParams,$location,mainService,tabService,$filter,datatable) {
	$scope.experiment = {
			processResolutions:[],
			outputGenerated:false,
			containerOutProperties:[],
			outputVoid:false,
			doPurif:false,
			comment:{},
			doQc:false,
			value: {
				code:"",
				typeCode:"",
				state:{
					resolutionCodes:[],
					code:"N"
				},
				reagents:[],
				protocolCode:"",
				instrument:{
					code:"",
					categoryCode:"",
					outContainerSupportCategoryCode:""
				},
				atomicTransfertMethods:[],
				comments:[],
				traceInformation:{
					createUser:"",
					creationDate:"",
					modifyUser:"",
					modifyDate:""
				}
			}
	};

	$scope.datatableConfigReagents = {
			name:"reagents",
			columns:[
			         {
			        	 "header":Messages("reagents.table.kitname"),
			        	 "property":"kitCatalogCode",
			        	 "order":true,
			        	 "type":"text",
			        	 "listStyle":"bt-select-filter",
			        	 "choiceInList":true,
			        	 "possibleValues": 'lists.getKitCatalogs()',
			        	 "render":'<div bt-select ng-model="value.data.kitCatalogCode" bt-options="kitCatalog.code as kitCatalog.name for kitCatalog in lists.getKitCatalogs()" ng-edit="false" placeholder="Messages("experiment.placeholder.reagents.kit")"></div>',
			        	 "edit":true
			         },
			         {
			        	 "header":Messages("reagents.table.boxcode"),
			        	 "property":"boxCode",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":true,
			        	 "editDirectives":'ng-keydown="scan($event,value.data,\'boxCode\')"'
			         },
			         {
			        	 "header":Messages("reagents.table.reagentcode"),
			        	 "property":"code",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":true,
			        	 "editDirectives":'ng-keydown="scan($event,value.data,\'code\')"'
			         },
			         {
			        	 "header":Messages("reagents.table.description"),
			        	 "property":"description",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":true
			         }
			         ],
			         compact:true,
			         pagination:{
			        	 mode:'local',
			        	 active:false
			         },		
			         search:{
			        	 active:false
			         },
			         order:{
			        	 mode:'local',
			        	 active:true,
			        	 by:'outputPositionX'
			         },
			         remove:{
			        	 mode:'local',
			        	 active:true,
			        	 withEdit:true,
			        	 callback:function(datatable){
			        		 var reagents = datatable.allResult;
			        		 $scope.experiment.value.reagents = reagents;
			        	 }
			         },
			         hide:{
			        	 active:false
			         },
			         edit:{
			        	 active:true,
			        	 columnMode:false,
			        	 showButton : false,
			        	 withoutSelect:true,
			        	 byDefault : false
			         },
			         save:{
			        	 active:true,
			        	 showButton:false,
			        	 withoutEdit:true,
			        	 mode:'local',
			        	 callback:function(datatable){
			        		 var reagents = datatable.allResult;
			        		 $scope.experiment.value.reagents = reagents;
			        	 }
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
			         otherButtons:{
			        	 active:true,
			        	 template:'<button class="btn btn btn-info" ng-click="addNewReagentLine()" title="'+Messages("experiments.addNewReagentLine")+'">'+Messages("experiments.addNewReagentLine")+'</button>'
			         }
	};


	$scope.datatableConfigOutPutContainers = {
			name:"outPutContainers",
			columns:[
			         {
			        	 "header":Messages("containers.table.code"),
			        	 "property":"code",
			        	 "order":true,
			        	 "position":1,
			        	 "type":"text"
			         },
			         {
			        	 "header":Messages("containers.table.state.code"),
			        	 "property":"state.code",
			        	 "order":true,
			        	 "type":"text",
			        	 "edit":true,
			        	 "position":2,
			        	 "choiceInList": true,
			        	 "possibleValues":"lists.getStates()", 
			        	 "filter":"codes:'state'"
			         }],
			         pagination:{
			        	 mode:'local'
			         },
			         order:{
			        	 by:'container.code',
			        	 reverse:true,
			        	 mode:'local'
			         },
			         edit:{
			        	 active:true,
			        	 columnMode:true
			         },
			         save:{
			        	 active:true,
			        	 url:jsRoutes.controllers.containers.api.Containers.updateBatch().url,
			        	 batch:true,
			        	 method:'put',
			        	 showButton: false,
			        	 callback: function(reason, error){

			        		 console.log("callback reason=" + reason);
			        		 console.log("callback error=" + error);
			        	 }
			         },
			         otherButtons:{
			        	 active:true,
			        	 template:'<a class="btn btn-default" data-dismiss="modal" href="#" ng-click="updateContainerStateDatatable()" ><i class="fa fa-save"></i></a>'
			         }
	};

	$scope.scan = function(e, property, propertyName){
		console.log(property);
		console.log(e);
		if(e.keyCode === 9 || e.keyCode === 13){
			property[propertyName] += '_';
			console.log(property);
			e.preventDefault();
		}
	};

	$scope.searchReagentsEvent = function(e){
		if(e.keyCode === 9 || e.keyCode === 13){
			$scope.searchReagents();
			e.preventDefault();
		}
	};


	$scope.searchReagents = function(){
		$http.get(jsRoutes.controllers.reagents.api.Reagents.list().url, {params:{"barCode":$scope.searchBarCode, "boxBarCode":$scope.searchBarCode}})
		.success(function(data, status,headers,config){
			console.log(data);
			var datatableData = $scope.datatableReagent.getData();
			for(var i=0;i<data.length;i++){
				var closureData = data[i];
				$http.get(jsRoutes.controllers.reagents.api.Boxes.list().url, {params:{"code":data[i].boxCode}})
				.success(function(dataBox, status,headers,config){
					var r = {"boxCode":dataBox[0].catalogRefCode+"_"+dataBox[0].bundleBarCode+"_"+dataBox[0].barCode,
							"code":closureData.catalogRefCode+"_"+closureData.bundleBarCode+"_"+closureData.barCode,
							"kitCatalogCode":closureData.catalogCode};
					if($scope.isReagentAdded(r.code) === false){
						datatableData.push(r);
						console.log(dataBox);
						$scope.datatableReagent.setData(datatableData);
					}
				});
			}
		}).error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');
			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};

	$scope.isReagentAdded = function(code){
		var datatableData = $scope.datatableReagent.getData();
		for(var i=0;i<datatableData.length;i++){
			if(datatableData[i].code === code){
				return true;
			}
		}
		return false
	};
	
	$scope.message = {};	

	/*$scope.isResolution = function(resolutionCodes){
		if(resolutionCodes != null && resolutionCodes.length === 1 && resolutionCodes[0] === "correct"){
			return false;
		}

		return true;
	};*/

	$scope.updateContainerStateDatatable = function(){
		var promiseFunction = function(){$scope.datatableContainer.save();};
		$scope.saveAllAndChangeState([promiseFunction]);
	};


	$scope.finishExperiment = function(){		
		if($scope.experiment.value.state.resolutionCodes !== null && $scope.experiment.value.state.resolutionCodes !== undefined && $scope.experiment.value.state.resolutionCodes.length >= 1){
			if($scope.experiment.value.state.resolutionCodes != null && $scope.experiment.value.state.resolutionCodes.length === 1 && $scope.experiment.value.state.resolutionCodes[0] === "correct"){
				$scope.correctResolution=true;
			}
			else { $scope.correctResolution=false;}

			//A revoir
				

				$scope.isLastExperiment = false;	
				$scope.continueProcess=false;

				$http.get(jsRoutes.controllers.processes.api.Processes.list().url,{params:{"experimentCode":$scope.experiment.value.code}})
				.success(function(data, status, headers, config){			
					console.log("data="+ data[0].typeCode);
					var processTypeCode = data[0].typeCode;					
					$http.get(jsRoutes.controllers.processes.api.ProcessTypes.get(processTypeCode).url)
					.success(function(data, status,headers,config){
						$scope.processTypeCode = data;
					var previousExperimentTypeCode = $scope.experimentType.code;
						if($scope.experiment.value.categoryCode==='transfert'){
							previousExperimentTypeCode = $scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds[0].fromExperimentTypeCodes[0];
							console.log("previousExperimentTypeCode= "+previousExperimentTypeCode);
						} 
						
						
						$http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.list().url, {params:{"previousExperimentTypeCode":previousExperimentTypeCode}})
						.success(function(data, status, headers, config) {
							$scope.nextExperimentTypeCodes = data;
							$scope.lists.refresh.resolutions({"typeCode":$scope.processTypeCode.code}, 'processResolution');
							$scope.lastExperimentTypeCode = $scope.processTypeCode.lastExperimentType.code;
							if($scope.lastExperimentTypeCode===previousExperimentTypeCode){
								$scope.isLastExperiment = true;
							}

							if($scope.nextExperimentTypeCodes !== null && $scope.nextExperimentTypeCodes !== undefined && $scope.nextExperimentTypeCodes.length==0 && $scope.correctResolution === true){
								return $scope.finishProcess();
							}

							if($scope.correctResolution==true && $scope.isLastExperiment==true && $scope.experimentType.atomicTransfertMethod=="OneToVoid"){
								return $scope.finishProcess();
							}
							else if($scope.correctResolution==true && $scope.isLastExperiment==false || ($scope.correctResolution==true && $scope.nextExperimentTypeCodes.length>0)){
								$scope.continueProcess=true;
							}
							$scope.continueExperiment();
							angular.element('#modalResolutionProcess').modal('show');
						});


					});	
				});		
				
		}else{
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');
			$scope.message.details = {"resolution":["propriété obligatoire"]};
			$scope.message.isDetails = true;
			$scope.experiment.experimentInformation.enabled = true;
		}
	};

	$scope.retryExperiment=function(){
		var promiseFn = function(){ return $http.put(jsRoutes.controllers.experiments.api.Experiments.retry($scope.experiment.value.code).url); };

		$scope.saveAllAndChangeState([promiseFn]);
	};

	$scope.stopProcess=function(){
		if($scope.experiment.processResolutions !== undefined && $scope.experiment.processResolutions !== null && $scope.experiment.processResolutions.length > 0){
			var promiseFn =  function(){ return $http.put(jsRoutes.controllers.experiments.api.Experiments.stopProcess($scope.experiment.value.code).url,{"processResolutionCodes":$scope.experiment.processResolutions})
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess');
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-danger";
					$scope.message.text = Messages('experiments.msg.save.error');
					$scope.message.details = data;
					$scope.meExperimssage.isDetails = true;
				});};
				$scope.saveAllAndChangeState([promiseFn]);
		}else{
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');
			$scope.message.details = {"resolution processus":["propriété obligatoire"]};
			$scope.message.isDetails = true;
		}
	}

	$scope.finishProcess=function(){
		if($scope.experiment.value.state.resolutionCodes !== undefined && $scope.experiment.value.state.resolutionCodes.length >= 1){
			var promiseFn =  function(){ return $http.put(jsRoutes.controllers.experiments.api.Experiments.endOfProcess($scope.experiment.value.code).url)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess');
					}					
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-danger";
					$scope.message.text = Messages('experiments.msg.save.error');
					$scope.message.details = data;
					$scope.message.isDetails = true;
				});};
				$scope.saveAllAndChangeState([promiseFn]);
		}
	}

	$scope.continueExperiment=function(){	
		$scope.datatableContainer = datatable($scope.datatableConfigOutPutContainers);
		$scope.outputContainers =[];
		var containers=[];
		angular.forEach($scope.experiment.value.atomicTransfertMethods, function(atomicTransfertMethod){
			if(atomicTransfertMethod.outputContainerUsed != undefined && atomicTransfertMethod.outputContainerUsed.code != null){
				var container = {"code":atomicTransfertMethod.outputContainerUsed.code,"state":{"code":"A"}};
				containers.push(container);
				$scope.outputContainers.push(atomicTransfertMethod.outputContainerUsed.code);
			}
		});
		$scope.lists.refresh.states({objectTypeCode:"Container"});
		$scope.datatableContainer.setData(containers);
	}

	$scope.updateContainerState=function(experimentCategory){
		var stateCode = "A";
		console.log(experimentCategory);
		switch(experimentCategory){
		case "qualitycontrol": stateCode = 'A-QC';
		break;
		case "transfert": 	   stateCode = 'A-TF';
		break;
		case "purification":   stateCode = 'A-PF';
		break;
		case "transformation":   stateCode = 'A-TM';
		break;								   
		default:               stateCode = 'A';
		}

		var containerUpdate={"stateCode":stateCode};

		var promiseFn =  function(){ $http.put(jsRoutes.controllers.containers.api.Containers.updateStateBatch().url,containerUpdate,{"params":{"codes":$scope.outputContainers}})
			.success(function(data, status, headers, config) {
				if(data!=null){
					$scope.message.clazz="alert alert-success";
					$scope.message.text=Messages('experiments.msg.save.sucess');
				}
			})
			.error(function(data, status, headers, config) {
				$scope.message.clazz = "alert alert-danger";
				$scope.message.text = Messages('experiments.msg.save.error');
				$scope.message.details = data;
				$scope.message.isDetails = true;
			});};

			$scope.saveAllAndChangeState([promiseFn]);
	};

	$scope.getPropertyColumnType = function(type){
		if(type === "java.lang.String"){
			return "text";
		}else if(type === "java.lang.Double"){
			return "number";
		}else if(type === "java.util.Date"){
			return "date";
		}

		return type;
	};

	$scope.addNewReagentLine = function(){
		$scope.datatableReagent.save();
		$scope.datatableReagent.addData([{}]);
		$scope.datatableReagent.setEdit();
	};

	$scope.setImage = function(imageData, imageName, imageFullSizeWidth, imageFullSizeHeight) {
		$scope.modalImage = imageData;

		$scope.modalTitle = imageName;

		var margin = 25;		
		var zoom = Math.min((document.body.clientWidth - margin) / imageFullSizeWidth, 1);

		$scope.modalWidth = imageFullSizeWidth * zoom;
		$scope.modalHeight = imageFullSizeHeight * zoom; //in order to conserve image ratio
		$scope.modalLeft = (document.body.clientWidth - $scope.modalWidth)/2;

		$scope.modalTop = (window.innerHeight - $scope.modalHeight)/2;

		$scope.modalTop = $scope.modalTop - 50; //height of header and footer
	}

	$scope.experiment.comments = {
			save:function(){
				if($scope.experiment.comment.code == undefined){
					$scope.clearMessages();
					//$scope.experiment.value.comments.push({"comment":$scope.experiment.comment});
					$http.post(jsRoutes.controllers.experiments.api.Experiments.addComment($scope.experiment.value.code).url, {"comment":$scope.experiment.comment.comment})
					.success(function(data, status, headers, config) {
						if(data!=null){
							$scope.message.clazz="alert alert-success";
							$scope.message.text=Messages('experiments.msg.save.sucess');
							$scope.experiment.value.comments.push(data);
							$scope.experiment.comment = "";
						}
					})
					.error(function(data, status, headers, config) {
						$scope.message.clazz = "alert alert-danger";
						$scope.message.text = Messages('experiments.msg.save.error');

						$scope.message.details = data;
						$scope.message.isDetails = true;
					});
				}else{
					this.update($scope.experiment.comment);
				}
			},
			update:function(com){
				$scope.clearMessages();
				//$scope.experiment.value.comments.push({"comment":$scope.experiment.comment});
				$http.put(jsRoutes.controllers.experiments.api.Experiments.updateComment($scope.experiment.value.code).url, com)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess');
						$scope.experiment.comment = {};
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-danger";
					$scope.message.text = Messages('experiments.msg.save.error');

					$scope.message.details = data;
					$scope.message.isDetails = true;
				});
			},
			delete:function(com){
				if (confirm(Messages("comments.remove.confirm"))) {
					$scope.clearMessages();
					//$scope.experiment.value.comments.push({"comment":$scope.experiment.comment});
					console.log(com);
					$http.delete(jsRoutes.controllers.experiments.api.Experiments.deleteComment($scope.experiment.value.code, com.code).url)
					.success(function(data, status, headers, config) {
						if(data!=null){
							$scope.message.clazz="alert alert-success";
							$scope.message.text=Messages('experiments.msg.save.sucess');
							//$scope.experiment.value.comments.pull(com);
							for(var i=0;$scope.experiment.value.comments.length;i++){
								if($scope.experiment.value.comments[i].code == com.code){
									$scope.experiment.value.comments.splice(i, 1);
								}
							}
						}
					})
					.error(function(data, status, headers, config) {
						$scope.message.clazz = "alert alert-danger";
						$scope.message.text = Messages('experiments.msg.save.error');

						$scope.message.details = data;
						$scope.message.isDetails = true;
					});
				}
			}
	};

	$scope.experiment.experimentInformation = {
			protocols:{},
			resolutions:{},
			enabled:true,
			toggleEdit:function(){
				this.enabled = !this.enabled;
			},
			save:function(){
				if($scope.experiment.value._id){
					$scope.clearMessages();
					return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateExperimentInformations($scope.experiment.value.code).url, $scope.experiment.value)
					.success(function(data, status, headers, config) {
						if(data!=null){
							$scope.experiment.value = data;
						}
					})
					.error(function(data, status, headers, config) {
						$scope.message.clazz = "alert alert-danger";
						$scope.message.text += Messages('experiments.msg.save.error');
						$scope.message.details += data;
						$scope.message.isDetails = true;
					});
				}else{
					$scope.save();
				}
			}
	};

	$scope.isOutputGenerated = function(){
		var j = 1;
		while($scope.experiment.value.atomicTransfertMethods[(j-1)] != null){
			if($scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUsed != null || $scope.experiment.value.atomicTransfertMethods[(j-1)].outputContainerUseds != null){
				$scope.experiment.outputGenerated = true;
				return true
			}
			j++;
		}

		return false;

	};

	/**
	 * Configure edit and remote on datatable
	 */
	$scope.setEditConfig = function(value){
		if($scope.experiment.value.state.code !== "F"){
			$scope.editMode = value;
			//var config = $scope.datatable.getConfig();
			//config.edit.active=value;		
			//config.remove.active=value;
			//config.save.active=value;
			//$scope.datatable.setConfig(config);
			if(value){
				$scope.startEditMode();								
			}else{
				$scope.stopEditMode();
			}
		}else{
			$scope.editMode = false;			
		}
	};
	/**
	 * Pass in edit mode
	 */
	$scope.edit = function(){
		if($scope.experiment.value.state.code !== "F"){
			$scope.setEditConfig(true);
			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.$broadcast('enableEditMode');

			if(mainService.isHomePage('search') && !tabService.isBackupTabs()){
				tabService.backupTabs();
				tabService.resetTabs();
				tabService.addTabs({label:Messages('experiments.tabs.create'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("new").url,remove:false});
				tabService.addTabs({label:$filter('codes')($scope.form.typeCode,'type'),href:"/experiments/new/"+$scope.form.typeCode,remove:false});
				tabService.addTabs({label:$scope.experiment.value.code,href:jsRoutes.controllers.experiments.tpl.Experiments.edit($scope.experiment.value.code).url,remove:true});
				tabService.activeTab(2);
				//reinit datatable
				$scope.datatableReagent.setEdit();
				mainService.setDatatable(undefined);
				$scope.form = {};
				$scope.form.nextExperimentTypeCode = experiment.typeCode;
				$scope.form.experimentCategoryCode = $scope.experimentType.category.code;
				$scope.form.processCategory = mainService.getForm().processCategory;
				$scope.form.processTypeCode= mainService.getForm().processTypeCode;
				if(experiment.instrument.inContainerSupportCategoryCode != undefined){
					$scope.form.containerSupportCategory = experiment.instrument.inContainerSupportCategoryCode;
				}

				mainService.setForm($scope.form);
			}
		}else{			
			$scope.setEditConfig(false);
			$scope.experiment.experimentProperties.enabled = false;
			$scope.experiment.experimentInformation.enabled = false;
			$scope.experiment.instrumentProperties.enabled = false;
			$scope.experiment.instrumentInformation.enabled = false;
			$scope.experiment.editMode=false;
			if($scope.experiment.value.state.code === "F"){
				$scope.$broadcast('disableEditMode');
			}	

		} 
	};

	/**
	 * Remove all change
	 */
	$scope.unedit = function(){
		$scope.experiment.experimentProperties.enabled = false;
		$scope.experiment.experimentInformation.enabled = false;
		$scope.experiment.instrumentProperties.enabled = false;
		$scope.experiment.instrumentInformation.enabled = false;

		$scope.clearMessages();
		$scope.setEditConfig(false);
		if(mainService.isHomePage('search') && tabService.isBackupTabs()){
			tabService.restoreBackupTabs();
			tabService.activeTab(1);
			mainService.setDatatable(undefined);
			$scope.form = {};
			$scope.form.typeCode = experiment.typeCode;
			//$scope.form.experimentCategoryCode = $scope.experimentType.category.code;
			$scope.form.processCategory = mainService.getForm().processCategory;
			$scope.form.processTypeCode= mainService.getForm().processTypeCode;
			/*if(experiment.instrument.inContainerSupportCategoryCode != undefined){
				$scope.form.containerSupportCategory = experiment.instrument.inContainerSupportCategoryCode;
			}*/
			mainService.setForm($scope.form);		
		}		
	}

	$scope.addSearchTabs = function(){
		if(tabService.getTabs().length < 1){ 	
			mainService.setHomePage('search');
			tabService.addTabs({label:Messages('experiments.tabs.search'),href:jsRoutes.controllers.experiments.tpl.Experiments.home("search").url,remove:false});
			tabService.addTabs({label:experiment.code,href:jsRoutes.controllers.experiments.tpl.Experiments.edit(experiment.code).url,remove:true});
			tabService.activeTab(1);
		}
	}

	$scope.getSampleTypeCodes = function(contents){
		var sampleTypeCodes = [];
		for(var i=0;i<contents.length;i++){
			if(contents[i].sampleTypeCode != undefined){
				sampleTypeCodes.push(contents[i].sampleTypeCode);
			}
		}

		return sampleTypeCodes;
	};

	$scope.getTags = function(contents){
		var tags = [];
		for(var i=0;i<contents.length;i++){
			if(contents[i].properties.tag != undefined){
				tags.push(contents[i].properties.tag.value);
			}
		}

		return tags;
	};

	$scope.getLibProcessTypeCodes = function(contents){
		var libProcessTypeCodes = [];
		for(var i=0;i<contents.length;i++){
			if(contents[i].properties.libProcessTypeCode != undefined){
				libProcessTypeCodes.push(contents[i].properties.libProcessTypeCode.value);
			}
		}

		return libProcessTypeCodes;
	};

	$scope.getTemplate = function(){		
		if($scope.experiment.inputTemplate != undefined){
			console.log("GET TEMPLATE "+$scope.experiment.inputTemplate);
			$scope.experiment.value.atomicTransfertMethods = [];
		}

		if($scope.experiment.value.instrument.outContainerSupportCategoryCode){
			$scope.experiment.inputTemplate =  jsRoutes.controllers.experiments.tpl.Experiments.getTemplate($scope.experimentType.atomicTransfertMethod,$scope.experiment.value.instrument.outContainerSupportCategoryCode,  $scope.experimentType.code).url;
		}else if($scope.experiment.outputVoid){
			$scope.experiment.inputTemplate =  jsRoutes.controllers.experiments.tpl.Experiments.getTemplate($scope.experimentType.atomicTransfertMethod,'void',$scope.experimentType.code).url;
		}
	};
	
	$scope.getPropertyValue =  function(propertyCode){
		var data = $scope.experiment.experimentProperties.inputs;
		if(data != undefined){
			angular.forEach(data, function(property){
				if(propertyCode===property.code){
					if($scope.getLevel( property.levels, "ContainerIn")){
						if(property.choiceInList){
							var possibleValues = $scope.possibleValuesToSelect(property.possibleValues);
						}
						
					}
					"inputExperimentProperties."+propertyCode+".value"
				}
				
				
			});
					
		}
	};

	$scope.removeNullProperties = function(properties){
		for (var p in properties) {
			if(properties[p] != undefined && (properties[p].value === undefined || properties[p].value === null || properties[p].value === "")){
				properties[p] = undefined;
			}
		}
	};

	$scope.experiment.experimentProperties = {
			enabled:true,

			toggleEdit:function(){
				if($scope.experiment.value.state.code !== "F"){
					this.enabled = !this.enabled;
				}else{
					this.enabled = false;
				}
			},
			save:function(){
				$scope.clearMessages();

				$scope.$broadcast('InputToExperiment', $scope.experimentType.atomicTransfertMethod);
				$scope.$broadcast('OutputToExperiment', $scope.experimentType.atomicTransfertMethod);

				$scope.removeNullProperties($scope.experiment.value.experimentProperties);

				return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateExperimentProperties($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.experiment.value = data;
						$scope.$broadcast('experimentToInput', $scope.experimentType.atomicTransfertMethod);
					}					

				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-danger";
					$scope.message.text += Messages('experiments.msg.save.error');

					$scope.message.details += data;
					$scope.message.isDetails = true;
				});

			}
	};

	$scope.addExperimentPropertiesInputsColumns = function(){
		var data = $scope.experiment.experimentProperties.inputs;
		if(data != undefined){
			angular.forEach(data, function(property){
				if($scope.getLevel( property.levels, "ContainerIn")){
					if(property.choiceInList){
						var possibleValues = $scope.possibleValuesToSelect(property.possibleValues);
					}
					$scope.$broadcast('addExperimentPropertiesInput', property, possibleValues);
				}
			});
			$scope.$broadcast('addExperimentPropertiesInputToScope', data);		
		}
	};

	$scope.experiment.instrumentInformation = {
			instrumentUsedTypes:{},
			instrumentCategorys:{},
			instruments:{},			
			enabled:true,
			toggleEdit:function(){
				this.enabled = !this.enabled;
			},
			save:function(){
				$scope.clearMessages();
				if(this.instruments.selected){
					$scope.experiment.value.instrument.code = this.instruments.selected.code;
					return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateInstrumentInformations($scope.experiment.value.code).url, $scope.experiment.value)
					.success(function(data, status, headers, config) {
						if(data!=null){
							$scope.experiment.value = data;
						}						
					})
					.error(function(data, status, headers, config) {
						$scope.message.clazz = "alert alert-danger";
						$scope.message.text += Messages('experiments.msg.save.error');
						$scope.message.details += data;
						$scope.message.isDetails = true;
					});
				}
			}	
	};

	$scope.experiment.instrumentProperties = {
			inputs:[],
			enabled:true,
			toggleEdit:function(){
				this.enabled = !this.enabled;
			},
			save:function(){
				$scope.clearMessages();
				$scope.$broadcast('inputToExperiment', $scope.experimentType.atomicTransfertMethod);
				$scope.$broadcast('outputToExperiment', $scope.experimentType.atomicTransfertMethod);

				$scope.removeNullProperties($scope.experiment.value.instrumentProperties);

				return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateInstrumentProperties($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.experiment.value = data;
						$scope.$broadcast('experimentToInput', $scope.experimentType.atomicTransfertMethod);
					}					
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-danger";
					$scope.message.text += Messages('experiments.msg.save.error');

					$scope.message.details += data;
					$scope.message.isDetails = true;
					alert("error");
				});
			}
	};


	$scope.saveContainers = function(){
		$scope.$broadcast('inputToExperiment', $scope.experimentType.atomicTransfertMethod);
		$scope.clearMessages();
		return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateContainers($scope.experiment.value.code).url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.experiment.value = data;
				$scope.$broadcast('refresh');
			}	

		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});

	};

	$scope.clearMessages  = function(){
		$scope.message = {clazz : undefined, text : undefined, showDetails : false, isDetails : false, details : []};
	};

	$scope.generateSampleSheet = function(){
		$http.post(jsRoutes.instruments.io.Outputs.sampleSheets().url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			var header = headers("Content-disposition");
			var filepath = header.split("filename=")[1];
			var filename = filepath.split("/");
			filename = filename[filename.length-1];
			if(data!=null){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.generateSampleSheet.success')+" : "+filepath;
				var blob = new Blob([data], {type: "text/plain;charset=utf-8"});    					
				saveAs(blob, filename);
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.generateSampleSheet.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
			alert("error");
		});
	};

	$scope.saveAllPromise = function(){
		$scope.clearMessages();
		if(!$scope.saveInProgress){
			$scope.saveInProgress = true;
			$scope.spinnerStart=true;
			var promises = [];			
			$scope.$broadcast('save', promises, $scope.saveAll);
			$scope.inProgressNow = false;
			$scope.inProgressMode();
			if($scope.experiment.value.state.code === "F"){
				$scope.$broadcast('disableEditMode');				
			}			
			if(promises.length<=0){
				$scope.message.clazz = "alert alert-danger";
				$scope.message.text = Messages('experiments.msg.empty.save.error');				
				$scope.$broadcast('enableEditMode');
				$scope.experiment.experimentProperties.enabled = true;
				$scope.experiment.experimentInformation.enabled = true;
				$scope.experiment.instrumentProperties.enabled = true;
				$scope.experiment.instrumentInformation.enabled = true;
				$scope.setEditConfig(true);	
				$scope.$broadcast('enableEditMode');				
				$scope.saveInProgress = false;
				$scope.spinnerStart=false;				

			}

		}
		$scope.$broadcast('refresh');
	};

	$scope.$on('viewSaved', function(e, promises, func, endPromises) {
		$scope.message.details = {};
		$scope.message.isDetails = false;

		$scope.experiment.experimentProperties.enabled = false;
		$scope.experiment.experimentInformation.enabled = false;
		$scope.experiment.instrumentProperties.enabled = false;
		$scope.experiment.instrumentInformation.enabled = false;
		$scope.setEditConfig(false);
		promises.push($scope.datatableReagent.save());
		
		if($scope.experiment.value._id != undefined){
			
			promises.push($scope.experiment.instrumentProperties.save());

			promises.push($scope.experiment.instrumentInformation.save());

			promises.push($scope.experiment.experimentInformation.save());

			promises.push($scope.experiment.experimentProperties.save());

			promises.push($scope.saveContainers());
		}else{
			$scope.$broadcast('inputToExperiment', $scope.experimentType.atomicTransfertMethod);
			//$scope.$broadcast('outputToExperiment', $scope.experimentType.atomicTransfertMethod);
			mainService.setBasket(undefined);
			tabService.resetTabs();
			promises.push($scope.save());
		}

		if(func){
			func(promises, endPromises);
		}
		
	});	

	$scope.saveAll = function(promises){    

		$q.all(promises).then(function (res) {
			if($scope.message.text != Messages('experiments.msg.save.error')){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess');
			}				
			$scope.experiment.experimentProperties.enabled = false;
			$scope.experiment.experimentInformation.enabled = false;
			$scope.experiment.instrumentProperties.enabled = false;
			$scope.experiment.instrumentInformation.enabled = false;
			$scope.setEditConfig(false);
			$scope.saveInProgress = false;
			$scope.spinnerStart=false;

		},function(reason) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.setEditConfig(true);
			$scope.message.details = reason.data;
			$scope.message.isDetails = true;
			$scope.$broadcast('enableEditMode');
			$scope.saveInProgress = false;
			$scope.spinnerStart=false;
			$scope.$broadcast('refresh');
		});
	};

	$scope.save = function(){
		return $http.post(jsRoutes.controllers.experiments.api.Experiments.save().url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.message.clazz = "alert alert-success";
				$scope.message.text= Messages('experiments.msg.save.sucess');
				$scope.experiment.value = data;
				$scope.saveInProgress = false;
				/*   */				$location.path(jsRoutes.controllers.experiments.tpl.Experiments.edit(data.code).url);				
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.setEditConfig(true);

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};

	$scope.saveAllAndChangeState = function(endPromises){		
		$scope.clearMessages();		
		if($scope.experiment.stopProcess === true && ($scope.experiment.processResolutions === undefined || $scope.experiment.processResolutions===null || $scope.experiment.processResolutions.length === 0)){
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = {"Processes resolution":["Propriété obligatoire"]};
			$scope.message.isDetails = true;
		}else{			
			if(!$scope.saveInProgress){
				var promises = [];
				//$scope.$broadcast('spinnerStart');
				$scope.spinnerStart=true;
				$scope.$broadcast('save', promises, $scope.changeState, endPromises);				
				$scope.saveInProgress = true;				
			}			
		}
	};

	$scope.changeState = function(promisesBefore, promisesAfter){
		$q.all(promisesBefore).then(function (res) {			
			$scope.experiment.experimentProperties.enabled = false;
			$scope.experiment.experimentInformation.enabled = false;
			$scope.experiment.instrumentProperties.enabled = false;
			$scope.experiment.instrumentInformation.enabled = false;
			$scope.setEditConfig(false);

			$scope.clearMessages();
			if($scope.experiment.value !== undefined){
				if($scope.experiment.value.state.code === "N"){
					$scope.nextStateCode = "IP";
				}else if($scope.experiment.value.state.code === "IP"){
					$scope.nextStateCode = "F";
				}
			}
			var promise = $http.put(jsRoutes.controllers.experiments.api.Experiments.updateStateCode($scope.experiment.value.code).url,{"nextStateCode":$scope.nextStateCode, "stopProcess":$scope.experiment.stopProcess, "retry":$scope.experiment.retry, "processResolutionCodes":$scope.experiment.processResolutions})
			.success(function(data, status, headers, config) {
				if(data!=null){
					$scope.experiment.value = data;
					if(!$scope.experiment.outputGenerated && $scope.isOutputGenerated()){
						$scope.$broadcast('addOutputColumns');
						$scope.addExperimentPropertiesOutputsColumns();
						$scope.addInstrumentPropertiesOutputsColumns();						
						$scope.saveInProgress = false;						
						$scope.spinnerStart=false;

					}

					

				}
				$scope.$broadcast('refresh');
				$scope.inProgressNow = false;
				$scope.inProgressMode();
				if($scope.experiment.value.state.code === "F"){
					$scope.$broadcast('disableEditMode');
					$scope.doneAndRecorded = true;
					//$scope.finishExperiment();
				}

			})
			.error(function(data, status, headers, config) {
				$scope.spinnerStart=false;
				$scope.message.clazz = "alert alert-danger";
				$scope.message.text = Messages('experiments.msg.save.error');

				$scope.$broadcast('enableEditMode');
				$scope.experiment.experimentProperties.enabled = true;
				$scope.experiment.experimentInformation.enabled = true;
				$scope.experiment.instrumentProperties.enabled = true;
				$scope.experiment.instrumentInformation.enabled = true;
				$scope.setEditConfig(true);

				$scope.message.details = data;
				$scope.message.isDetails = true;
				$scope.saveInProgress = false;
			});

			promise.then(function(res) {
				for(var i=0;promisesAfter!==undefined && i<promisesAfter.length;i++) {
					promisesAfter[i] = promisesAfter[i]();
				}

				$q.all(promisesAfter).then(function (res) {
					if($scope.message.text != Messages('experiments.msg.save.error')){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess');
						$scope.saveInProgress = false;
						$scope.spinnerStart=false;
						$scope.inProgressNow = false;
						$scope.inProgressMode();
						/*if(fn !== undefined){
							fn();
						}*/
						
						if($scope.experiment.outputGenerated){
							$scope.$broadcast('experimentToOutput', $scope.experimentType.atomicTransfertMethod);
						}
						
						if($scope.experiment.value.state.code === "F"){
							$scope.$broadcast('disableEditMode');
						}	
						$scope.$broadcast('refresh');
					}
				});
			}, function(reason){			    
				$scope.message.clazz = "alert alert-danger";
				$scope.message.text = Messages('experiments.msg.save.error');

				$scope.$broadcast('enableEditMode');
				$scope.experiment.experimentProperties.enabled = true;
				$scope.experiment.experimentInformation.enabled = true;
				$scope.experiment.instrumentProperties.enabled = true;
				$scope.experiment.instrumentInformation.enabled = true;
				$scope.setEditConfig(true);

				$scope.message.details = reason.data;
				$scope.message.isDetails = true;
				$scope.saveInProgress = false;
				$scope.spinnerStart=false;


			});
		}, function(reason){
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.$broadcast('enableEditMode');
			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.setEditConfig(true);
			$scope.$broadcast('refresh');
			$scope.message.details = reason.data;
			$scope.message.isDetails = true;
			$scope.saveInProgress = false;
			$scope.spinnerStart=false;

		});
	};	

	$scope.doPurifOrQc = function(code){
		$http.get(jsRoutes.controllers.experiments.api.ExperimentTypeNodes.list().url,{params:{"code":code}})
		.success(function(data, status, headers, config) {
			$scope.clearMessages();
			if(data != null && data[0] !=null){
				$scope.experiment.doPurif = data[0].doPurification;
				$scope.experiment.doQc = data[0].doQualityControl;
			}
		})
		.error(function(data, status, headers, config) {
			alert("error");
		});
	};



	$scope.create_experiment = function(containers, atomicTransfertMethod){
		//$scope.init_atomicTransfert(containers,atomicTransfertMethod);
		$scope.$broadcast('initAtomicTransfert', containers, atomicTransfertMethod);

		angular.element(document).ready(function() {
			if($scope.experiment.experimentProperties.inputs != undefined){
				angular.forEach($scope.experiment.experimentProperties.inputs, function(input){
					if($scope.getLevel( input.levels, "Experiment")){
						var getter = $parse("experiment.value.experimentProperties."+input.code+".value");
						getter.assign($scope,"");
					}
				});
			}
		});
	};

	$scope.init_experiment = function(containers,atomicTransfertMethod){
		if($scope.form != undefined && $scope.form.experiment != undefined){
			$scope.form.experiment = $scope.experiment;
		}
		$scope.experiment.value.categoryCode = $scope.experimentType.category.code;
		$scope.experiment.value.atomicTransfertMethods = {};
		if($scope.experiment.value.code === ""){
			$scope.create_experiment(containers,atomicTransfertMethod);
		}		

	};

	$scope.getInstrumentsTrigger = function(){
		if($scope.experiment.value.instrument != undefined && $scope.experiment.value.instrument.typeCode != null){
			$scope.experiment.value.instrument.outContainerSupportCategoryCode = undefined;
			if((angular.isUndefined($scope.outputVoid)&& $scope.experimentType.atomicTransfertMethod!=="OneToVoid") || $scope.outputVoid == false){
				$scope.experiment.inputTemplate = undefined; //reset the template
			}
			$scope.getInstruments(false);
			$scope.getInstrumentProperties($scope.experiment.value.instrument.typeCode,false);
		}
	};

	$scope.getInstruments = function(loaded){
		//$scope.experiment.value.instrument.outContainerSupportCategoryCode = "";
		if($scope.experiment.value.instrument.typeCode === null){
			$scope.experiment.instrumentProperties.inputs = [];
			$scope.experiment.instrumentInformation.instrumentCategorys.inputs = [];

		}

		$scope.$broadcast('deleteInstrumentPropertiesInputs', "Instruments");
		$scope.$broadcast('deleteInstrumentPropertiesOutputs', "Instruments");

		if(loaded == false){
			$scope.experiment.value.instrumentProperties = {};
			//$scope.experiment.value.instrument.outContainerSupportCategoryCode = "";
			if($scope.experimentType.atomicTransfertMethod == "ManyToOne"){				
				for(var i=0;i< $scope.experiment.value.atomicTransfertMethods.length;i++){
					for(var j=0;j<$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds.length;j++){
						$scope.experiment.value.atomicTransfertMethods[i].inputContainerUseds[j].instrumentProperties = {};
					}
				}
			}else{
				for(var i=0;i< $scope.experiment.value.atomicTransfertMethods.length;i++){
					$scope.experiment.value.atomicTransfertMethods[i].inputContainerUsed.instrumentProperties = {};
				}

				//Bug with the map ?
				/*$scope.experiment.value.atomicTransfertMethods = $scope.experiment.value.atomicTransfertMethods.map(function(atomicTransfertMethod){
						atomicTransfertMethod.inputContainerUsed.instrumentProperties = {};
						return atomicTransfertMethod;
				});*/
			}
		}

		if($scope.experiment.value.instrument.typeCode != null ){
			$scope.getInstrumentCategory($scope.experiment.value.instrument.typeCode);
			$scope.lists.refresh.instruments({"typeCode":$scope.experiment.value.instrument.typeCode, "active":true});
			$scope.lists.refresh.containerSupportCategories({"instrumentUsedTypeCode":$scope.experiment.value.instrument.typeCode});
		}
	};

	$scope.getInstrumentCategory = function(intrumentUsedTypeCode){
		$http.get(jsRoutes.controllers.instruments.api.InstrumentCategories.list().url, {params:{instrumentTypeCode:intrumentUsedTypeCode, list:true}})
		.success(function(data, status, headers, config) {
			$scope.experiment.value.instrument.categoryCode = data[0].code;
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};

	$scope.getInstrumentProperties = function(code,loaded){
		$scope.clearMessages();
		if(!loaded){
			loaded = false;
		}
		$http.get(jsRoutes.controllers.experiments.api.Experiments.getInstrumentProperties(code).url)
		.success(function(data, status, headers, config) {

			$scope.experiment.instrumentProperties.inputs = data;
			angular.forEach(data, function(property){
				//Creation of the properties in the scope
				if(loaded == false){
					var getter = $parse("experiment.value.instrumentProperties."+property.code+".value");
					getter.assign($scope,"");
				}
				if($scope.getLevel( property.levels,"ContainerIn")){
					if(property.choiceInList){
						//var possibleValues = [];
						var possibleValues = property.possibleValues.map(function(propertyPossibleValue){
							return {"name":propertyPossibleValue.value,"code":propertyPossibleValue.value};
						});
					}

					$scope.$broadcast('addInstrumentPropertiesInput', property, possibleValues);
				}
			});

			$scope.$broadcast('addInstrumentPropertiesInputToScope', data);
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};

	$scope.possibleValuesToSelect = function(possibleValues){
		return possibleValues.map(function(possibleValue){
			return {"code":possibleValue.code,"name":possibleValue.name};
		});
	};

	$scope.addExperimentPropertiesOutputsColumns = function(){
		if( $scope.experiment.experimentProperties.inputs != undefined){
			var data = $scope.experiment.experimentProperties.inputs;
			var outputGenerated = $scope.isOutputGenerated();
			angular.forEach(data, function(property){
				if($scope.getLevel( property.levels, "ContainerOut")){
					if(property.choiceInList){
						var possibleValues = $scope.possibleValuesToSelect(property.possibleValues);
					}

					$scope.$broadcast('addExperimentPropertiesOutput', property, possibleValues);
				}
			});

			if(outputGenerated || $scope.experiment.outputGenerated){
				$scope.$broadcast('addExperimentPropertiesOutputToScope', data);
			}
		}
		if($scope.experiment.value.state.code !== "F"){
			$scope.$broadcast('enableEditMode');
		}

	};

	$scope.addInstrumentPropertiesOutputsColumns = function(){
		var data = $scope.experiment.instrumentProperties.inputs;		
		var outputGenerated = $scope.isOutputGenerated();

		angular.forEach(data, function(property){
			if($scope.getLevel( property.levels,"ContainerOut")){	 					
				if(property.choiceInList){
					var possibleValues = $scope.possibleValuesToSelect(property.possibleValues);
				}

				$scope.$broadcast('addInstrumentPropertiesOutput', property, possibleValues);
			}
		});

		if(outputGenerated){
			$scope.$broadcast('addInstrumentPropertiesOutputToScope', data);
		}

	};

	$scope.getLevel = function(levels,level){
		if(levels != undefined){
			for(var i=0;i<levels.length;i++){
				if(levels[i].code === level){
					return true;
				}
			}
		}
		return false;
	};
	if($scope.isEditMode() != undefined){
		$scope.editMode = $scope.isEditMode();
	}else{
		$scope.editMode = false;
	}
	$scope.experimentType =  {};
	var promise = $q.when($scope.experimentType);
	var experiment = {
			code:"",
			typeCode:"",
			state:{
				resolutionCodes:[],
				code:"N"
			},
			protocolCode:"",
			instrument:{
				code:"",
				categoryCode:"",
				outContainerSupportCategoryCode:""
			},
			atomicTransfertMethods:[],
			comments:[],
			traceInformation:{
				createUser:"",
				creationDate:"",
				modifyUser:"",
				modifyDate:""
			}
	};

	$scope.convertToBr = function(text){
		return $sce.trustAsHtml(text.replace(/\n/g, "<br>"));
	};

	//init

	$scope.inProgressMode = function(){
		if($scope.experiment.value.state.code === "IP"){
			$scope.inProgressNow = true;
		}

	};

	if($routeParams.experimentCode){
		promise = $http.get(jsRoutes.controllers.experiments.api.Experiments.get($routeParams.experimentCode).url)
		.success(function(data, status, headers, config) {
			experiment = data;
			$scope.inProgressNow = false;
			$scope.inProgressMode();			
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	}	

	promise.then(function(result) {
		$scope.datatableReagent = datatable($scope.datatableConfigReagents);
		if(experiment.reagents === null || experiment.reagents === undefined || experiment.reagents.length === 0){
			$scope.datatableReagent.setData([]);
			$scope.datatableReagent.setEdit();
		}else{
			$scope.datatableReagent.setData(experiment.reagents);
		}
		if($routeParams.experimentTypeCode){
			$scope.experimentType.code = $routeParams.experimentTypeCode;
		}else{
			$scope.experimentType.code = experiment.typeCode;
		}

		promise = $http.get(jsRoutes.controllers.experiments.api.ExperimentTypes.get($scope.experimentType.code).url)
		.success(function(data, status, headers, config) {
			$scope.experimentType.category = data.category;
			$scope.form = mainService.getForm();
			if($scope.form === undefined){
				$scope.form = {};
			}
			//$scope.form.experimentCategoryCode = $scope.experimentType.category.code;
			mainService.setForm($scope.form);
			$scope.experimentType.atomicTransfertMethod = data.atomicTransfertMethod;
			if($scope.experimentType.atomicTransfertMethod == "OneToVoid"){
				$scope.experiment.outputVoid = true;
			}
			$scope.experiment.experimentProperties.inputs = data.propertiesDefinitions;
			experiment.typeCode =  data.code;
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
		promise.then(function(result) {
			$scope.lists = lists;
			$scope.lists.clear("containerSupportCategories");
			$scope.lists.clear("instruments");
			$scope.lists.clear("instrumentUsedTypes");
			$scope.lists.clear("protocols");
			$scope.lists.clear("resolutions");
			$scope.lists.clear("states");
			$scope.lists.clear("experimentTypeCodes");
			$scope.lists.clear("experimentCategories");

			$scope.lists.refresh.experimentTypes({categoryCode:$scope.experimentType.category.code},$scope.experimentType.category.code);
			$scope.lists.refresh.instrumentUsedTypes({"experimentTypeCode":experiment.typeCode});
			$scope.lists.refresh.protocols({"experimentTypeCode":experiment.typeCode});
			$scope.lists.refresh.resolutions({"typeCode":experiment.typeCode});
			$scope.lists.refresh.states({"objectTypeCode":"Experiment"});
			$scope.lists.refresh.kitCatalogs();
			$scope.lists.refresh.experimentCategories();

			$scope.doneAndRecorded = false;
			$scope.inProgressNow = false;
			$scope.inProgressMode();

			if(!$routeParams.experimentCode){
				$scope.form = mainService.getForm();
				experiment.instrument.inContainerSupportCategoryCode = $scope.form.containerSupportCategory;
				$scope.experiment.editMode=false;
				$scope.experiment.value = experiment;
				if($scope.experiment.outputVoid === true){
					$scope.getTemplate();
				}

				$scope.form.nextExperimentTypeCode = experiment.typeCode;
				$scope.form.experimentCategoryCode = $scope.experimentType.category.code;
				$scope.form.processCategory = mainService.getForm().processCategory;
				$scope.form.processTypeCode= mainService.getForm().processTypeCode;
				if(experiment.instrument.inContainerSupportCategoryCode != undefined){
					$scope.form.containerSupportCategory = experiment.instrument.inContainerSupportCategoryCode;
				}

					mainService.setForm($scope.form);
				
				$scope.edit();
			}else{
				$scope.experiment.editMode=true;
				$scope.doneAndRecorded = false;
				$scope.experiment.experimentProperties.enabled = false;
				$scope.experiment.experimentInformation.enabled = false;
				$scope.experiment.instrumentProperties.enabled = false;
				$scope.experiment.instrumentInformation.enabled = false;
				$scope.form = mainService.getForm();
				if($scope.form === undefined){
					$scope.form = {};
				}
				if($scope.editMode === false){
					$scope.form.typeCode = experiment.typeCode;
					//$scope.form.containerSupportCategory = experiment.instrument.inContainerSupportCategoryCode;
					mainService.setForm($scope.form);
				}
				$scope.addSearchTabs();
				$scope.experiment.value.instrument.outContainerSupportCategoryCode = experiment.instrument.outContainerSupportCategoryCode;
				$scope.experiment.value = experiment;
				$scope.inProgressNow = false;
				$scope.inProgressMode();
				
				if($scope.experiment.value.state.code === "F"){
					$scope.experiment.instrumentProperties.enabled = false;
					$scope.experiment.experimentProperties.enabled = false;
					$scope.experiment.instrumentInformation.enabled = false;
					$scope.experiment.experimentInformation.enabled = false;
					$scope.editMode = false;
					$scope.setEditConfig(false);
					$scope.doneAndRecorded = true;
				}
				$scope.getInstruments();
				$scope.getTemplate();
			}
		});
	});
}]);