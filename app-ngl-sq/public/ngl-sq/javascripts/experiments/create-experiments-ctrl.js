angular.module('home').controller('CreateNewCtrl',['$scope', '$window','$http','lists','$parse','$q','$position', function($scope,$window, $http,lists,$parse,$q,$position) {
	$scope.experiment = {
		outputGenerated:false,
		outputVoid:false,
		doPurif:false,
		doQc:false,
		value: {
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
		}
	};
	
	$scope.experiment.comments = {
			save:function(){
				$scope.clearMessages();
				$scope.experiment.value.comments.push({"comment":$scope.experiment.comment});
				$http.put(jsRoutes.controllers.experiments.api.Experiments.updateComments($scope.experiment.value.code).url, $scope.experiment.value)
				.success(function(data, status, headers, config) {
					if(data!=null){
						$scope.message.clazz="alert alert-success";
						$scope.message.text=Messages('experiments.msg.save.sucess')
						$scope.experiment.value = data;
					}
				})
				.error(function(data, status, headers, config) {
					$scope.message.clazz = "alert alert-danger";
					$scope.message.text = Messages('experiments.msg.save.error');

					$scope.message.details = data;
					$scope.message.isDetails = true;
				});
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
	
	$scope.getInputTemplate = function(){
		if($scope.experiment.value.instrument.outContainerSupportCategoryCode){
			$scope.experiment.inputTemplate =  jsRoutes.controllers.experiments.tpl.Experiments.getInputTemplate($scope.experimentType.atomicTransfertMethod, $scope.experiment.value.instrument.outContainerSupportCategoryCode).url;
		}else if($scope.experiment.value.instrument.typeCode && $scope.lists.get('containerSupportCategories').length == 0){
			$scope.experiment.inputTemplate =  jsRoutes.controllers.experiments.tpl.Experiments.getInputTemplate($scope.experimentType.atomicTransfertMethod, 'void').url;
		}
	};
	
	$scope.getOutputTemplate = function(){
	};
	
	$scope.experiment.experimentProperties = {
			enabled:true,
			toggleEdit:function(){
				this.enabled = !this.enabled;
			},
			save:function(){
					$scope.clearMessages();
					
					$scope.$broadcast('InputToExperiment', $scope.experimentType.atomicTransfertMethod);
					$scope.$broadcast('OutputToExperiment', $scope.experimentType.atomicTransfertMethod);
					
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
			data.forEach(function(property){
				if($scope.getLevel( property.levels, "ContainerIn")){		
					if(property.choiceInList){
						var possibleValues = $scope.possibleValuesToMap(property.possibleValues);
					}
					$scope.$broadcast('addExperimentPropertiesInput', property, possibleValues);
				}
			});
			$scope.$broadcast('addExperimentPropertiesInputToScope', property);		
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
		$scope.clearMessages();
		return $http.put(jsRoutes.controllers.experiments.api.Experiments.updateContainers($scope.experiment.value.code).url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.experiment.value = data;
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
			if(data!=null){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess')
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
			alert("error");
		});
	};
	
	$scope.saveAllPromise = function(){
		if(!$scope.saveInProgress){
			$scope.saveInProgress = true;
			var promises = [];
			$scope.$broadcast('save', promises, $scope.saveAll);
		}
	};
	
	$scope.$on('viewSaved', function(e, promises, func) {
		$scope.message.details = {};
		$scope.message.isDetails = false;
		
		$scope.experiment.experimentProperties.enabled = false;
		$scope.experiment.experimentInformation.enabled = false;
		$scope.experiment.instrumentProperties.enabled = false;
		$scope.experiment.instrumentInformation.enabled = false;
		console.log($scope.experiment.value._id);
		if($scope.experiment.value._id != undefined){
			promises.push($scope.experiment.instrumentProperties.save());
	
			promises.push($scope.experiment.instrumentInformation.save());
			
			promises.push($scope.experiment.experimentInformation.save());
	
			promises.push($scope.experiment.experimentProperties.save());
	
			promises.push($scope.saveContainers());
		}else{
			promises.push($scope.save());
		}
		
		if(func){
			func(promises);
		}
	});
	
	$scope.saveAll = function(promises){
		console.log(promises);
		$q.all(promises).then(function (res) {
			if($scope.message.text != Messages('experiments.msg.save.error')){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess');
			}
			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.$broadcast('refresh');
			$scope.saveInProgress = false;
		},function(reason) {
		    console.log(reason);
		    $scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.$broadcast('refresh');
			$scope.saveInProgress = false;
		  });
	};
	
	$scope.save = function(){
		return $http.post(jsRoutes.controllers.experiments.api.Experiments.save().url, $scope.experiment.value)
		.success(function(data, status, headers, config) {
			if(data!=null){
				$scope.message.clazz="alert alert-success";
				$scope.message.text=Messages('experiments.msg.save.sucess')
				$scope.experiment.value = data;
				$scope.saveInProgress = false;
			}
		})
		.error(function(data, status, headers, config) {
			$scope.message.clazz = "alert alert-danger";
			$scope.message.text = Messages('experiments.msg.save.error');

			$scope.message.details = data;
			$scope.message.isDetails = true;
		});
	};
	
	$scope.saveAllAndChangeState = function(){
		if(!$scope.saveInProgress){
			var promises = [];
			$scope.$broadcast('save', promises, $scope.changeState);
			$scope.saveInProgress = true;
		}
	};
	
	$scope.changeState = function(promises){
		$q.all(promises).then(function (res) {
			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.clearMessages();
			var promise = $http.put(jsRoutes.controllers.experiments.api.Experiments.nextState($scope.experiment.value.code).url)
			.success(function(data, status, headers, config) {
				if(data!=null){
					$scope.message.clazz="alert alert-success";
					$scope.message.text=Messages('experiments.msg.save.sucess')
					$scope.experiment.value = data;
					if(!$scope.experiment.outputGenerated && $scope.isOutputGenerated()){
						$scope.$broadcast('addOutputColumns');
						$scope.addExperimentPropertiesOutputsColumns();
						$scope.addInstrumentPropertiesOutputsColumns();
						$scope.$broadcast('experimentToOutput', $scope.experimentType.atomicTransfertMethod);
						$scope.saveInProgress = false;
					}
				}
			})
			.error(function(data, status, headers, config) {
				$scope.message.clazz = "alert alert-danger";
				$scope.message.text = Messages('experiments.msg.save.error');
				$scope.message.details = data;
				$scope.message.isDetails = true;
				$scope.saveInProgress = false;
			});
			
			 promise.then(function(res) {
				if(	$scope.message.text != Messages('experiments.msg.save.error')){
					$scope.message.clazz="alert alert-success";
					$scope.message.text=Messages('experiments.msg.save.sucess');
					$scope.saveInProgress = false;
				}
			}, function(reason){
				$scope.saveInProgress = false;
			});
		}, function(reason){
			$scope.experiment.experimentProperties.enabled = true;
			$scope.experiment.experimentInformation.enabled = true;
			$scope.experiment.instrumentProperties.enabled = true;
			$scope.experiment.instrumentInformation.enabled = true;
			$scope.saveInProgress = false;
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
	
	$scope.init_atomicTransfert = function(containers, atomicTransfertMethod){
		if(atomicTransfertMethod == "ManyToOne"){
			$scope.experiment.value.atomicTransfertMethods[0] = {class:atomicTransfertMethod, inputContainerUseds:[]};
			containers.forEach(function(container){
				$scope.experiment.value.atomicTransfertMethods[0].inputContainerUseds.push({code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state});
			});
		}else{
			containers.forEach(function(container,index){
				$scope.experiment.value.atomicTransfertMethods[index] = {class:atomicTransfertMethod, inputContainerUsed:[]};
				$scope.experiment.value.atomicTransfertMethods[index].inputContainerUsed = {code:container.code,instrumentProperties:{},experimentProperties:{},state:container.state};
				
				if($scope.experiment.value.atomicTransfertMethods[index].class == "OneToVoid"){
					$scope.experiment.outputVoid = true;
				}
			});
		}
	};
	
	$scope.create_experiment = function(containers, atomicTransfertMethod){
		$scope.init_atomicTransfert(containers,atomicTransfertMethod);

		angular.element(document).ready(function() {
			if($scope.experiment.experimentProperties.inputs != undefined){
				$scope.experiment.experimentProperties.inputs.forEach(function(input){
					var getter = $parse("experiment.value.experimentProperties."+input.code+".value");
					getter.assign($scope,"");
				});

				$scope.addExperimentPropertiesInputsColumns();
			}
		});
	}
	
	$scope.init_experiment = function(containers,atomicTransfertMethod){
		if($scope.form != undefined && $scope.form.experiment != undefined){
			$scope.form.experiment = $scope.experiment;
			$scope.setForm($scope.form);
		}
		$scope.experiment.value.categoryCode = $scope.experimentType.category.code;
		$scope.experiment.value.atomicTransfertMethods = {};
		if($scope.experiment.value.code === ""){
			$scope.create_experiment(containers,atomicTransfertMethod);
		}
		
	};
	
	$scope.getInstruments = function(loaded){
		if($scope.experiment.value.instrument.typeCode === null){
			$scope.experiment.instrumentProperties.inputs = [];
			$scope.experiment.instrumentInformation.instrumentCategorys.inputs = [];
		}

		$scope.$broadcast('deleteInstrumentPropertiesInputs', "Instruments");
		$scope.$broadcast('deleteInstrumentPropertiesOutputs', "Instruments");
		
		if(loaded == false){
			$scope.experiment.value.instrumentProperties = {};
			if($scope.experimentType.atomicTransfertMethod == "ManyToOne"){
				$scope.experiment.value.atomicTransfertMethods = $scope.experiment.value.atomicTransfertMethods.map(function(atomicTransfertMethod){
					atomicTransfertMethod.inputContainerUseds = atomicTransfertMethod.inputContainerUseds.map(function(inputContainerUsed){
						inputContainerUsed.instrumentProperties = {};
						return inputContainerUsed;
					});
					return atomicTransfertMethod;
				});
			}else{
				$scope.experiment.value.atomicTransfertMethods = $scope.experiment.value.atomicTransfertMethods.map(function(atomicTransfertMethod){
						atomicTransfertMethod.inputContainerUsed.instrumentProperties = {};
						return atomicTransfertMethod;
				});
			}
		}

		if($scope.experiment.value.instrument.typeCode != null ){
			$scope.getInstrumentCategory($scope.experiment.value.instrument.typeCode);
			$scope.lists.refresh.instruments({"typeCode":$scope.experiment.value.instrument.typeCode});
			$scope.lists.refresh.containerSupportCategories({"instrumentUsedTypeCode":$scope.experiment.value.instrument.typeCode});
			$scope.getInstrumentProperties($scope.experiment.value.instrument.typeCode,loaded);
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

			data.forEach(function(property){
				//Creation of the properties on the scope
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
			return {"code":possibleValue.value,"name":possibleValue.value};
		});
	};
	
	$scope.addExperimentPropertiesOutputsColumns = function(){
		if( $scope.experiment.experimentProperties.inputs != undefined){
			var data = $scope.experiment.experimentProperties.inputs;
			var outputGenerated = $scope.isOutputGenerated();
			
			data.forEach(function(property){
				if($scope.getLevel( property.levels, "ContainerOut")){
					if(property.choiceInList){
						var possibleValues = $scope.possibleValuesToSelect(property.possibleValues);
					}
					
					$scope.$broadcast('addExperimentPropertiesOutput', property, possibleValues);
				}
			});
			
			if(outputGenerated){
				$scope.$broadcast('addExperimentPropertiesOutputToScope', data);
			}
		}
	};
	
	$scope.addInstrumentPropertiesOutputsColumns = function(){
		var data = $scope.experiment.instrumentProperties.inputs;		
		var outputGenerated = $scope.isOutputGenerated();
		
		data.forEach(function(property){
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
	
	$scope.init = function(experimentCategoryCode, atomicTransfertMethod, experiment){
	
		$scope.experimentType =  {};
		$scope.experimentType.category= {};
		$scope.experimentType.category.code = experimentCategoryCode;
		$scope.experimentType.atomicTransfertMethod = atomicTransfertMethod;
		console.log($scope.experimentType.atomicTransfertMethod);
		if(experiment != ""){
			experiment =  JSON.parse(experiment);
			$scope.experiment.value.typeCode = experiment.typeCode;
		}
		
		$scope.form = $scope.getForm();
		if($scope.form != undefined && $scope.form.experimentType != undefined){
			$scope.experiment.value.typeCode = $scope.form.experimentType.code;
		}
		$scope.lists = lists;
	
		$scope.lists.refresh.instrumentUsedTypes({"experimentTypeCode":$scope.experiment.value.typeCode});
		$scope.lists.refresh.protocols({"experimentTypeCode":$scope.experiment.value.typeCode});
		$scope.lists.refresh.resolutions({"objectTypeCode":"Experiment"});
		$scope.lists.refresh.states({"objectTypeCode":"Experiment"});
		
		if(experiment == ""){
			$scope.experiment.editMode=false;
		}else{
			$scope.experiment.editMode=true;
			$scope.experiment.value.instrument.outContainerSupportCategoryCode = experiment.instrument.outContainerSupportCategoryCode;
			$scope.getInputTemplate();
			$scope.experiment.value = experiment;
			
			$scope.addExperimentPropertiesInputsColumns();
		}
	};
}]);