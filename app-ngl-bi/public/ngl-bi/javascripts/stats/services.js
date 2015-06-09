 "use strict";
 
 angular.module('ngl-bi.StatsServices', []).
	factory('statsConfigReadSetsService', ['$http', '$filter', 'lists', 'datatable', function($http, $filter, lists, datatable){
		var datatableConfig = {
				search : {
					active:false
				},
				pagination:{
					active:false
				},
				remove:{
					active:true,
					mode:'local'
				},
				columns : [
					{
						   property:"column.header",
						   header: "stats.property",
						   type :"text",
						   order:true
					},
			           {	
			        	   property:"typeCode",
			        	   header: "stats.typeCode",
			        	   type :"text",		    	  	
			        	   order:true
			           }
				]
		};
		
		var isInit = false;
		
		var initListService = function(){
			if(!isInit){
				lists.refresh.reportConfigs({pageCodes:["readsets-addcolumns"]}, "readsets-addcolumns");				
				isInit=true;
			}
		};
		
		var statsService = {
				datatable:undefined,
				statsTypes : [{code:'z-score', name:Messages("stat.typelabel.zscore")},{code:'simple-value', name:Messages("stat.typelabel.simplevalue")}],
				statColumns:[],
				select : {
					typeCode:'z-score',
					properties:[]
				},
				getStatsTypes : function(){
					return this.statsTypes;//,{code:'histogram', name:Messages("stat.typelabel.histogram")}];
				},
				reset : function(){
					this.select =  {
							typeCode:'z-score',
							properties:[]
						};
				},
				add : function(){
					var data = [];
					for(var i = 0; i < this.select.properties.length; i++){
						for(var j = 0; j < this.statColumns.length; j++){
							if(this.select.properties[i] === this.statColumns[j].header){
								if(this.select.typeCode){
									data.push({typeCode : this.select.typeCode, column: this.statColumns[j]});
								}
							}
						}						
					}		
					this.datatable.addData(data);
					this.reset();
				},
				setData : function(values){
					this.datatable.setData(values);
				},
				getData : function(){
					return this.datatable.getData();
				},
				isData : function(){
					return (this.datatable && this.datatable.getData().length > 0);
				},
				initStatColumns:function(){
					if(lists.get("readsets-addcolumns") && lists.get("readsets-addcolumns").length === 1){
						this.statColumns = $filter('filter')(lists.get("readsets-addcolumns")[0].columns,{modes:"chart"});
					}
				},
				getStatColumns : function(){
					if(this.statColumns.length === 0){
						this.initStatColumns();
					}
					return this.statColumns;									
				},
				init : function(){
					initListService();
					this.datatable= datatable(datatableConfig);
					this.datatable.setData([], 0);
				}
		};
		if(!isInit){
			statsService.init();
		}
		return statsService;				
	}			
]).factory('queriesConfigReadSetsService', ['$http', '$q', 'datatable', function($http, $q, datatable){
	var datatableConfig = {
			search : {
				active:false
			},
			pagination:{
				mode:'local'
			},
			remove:{
				active:true,
				mode:'local',
				callback:function(datatable){
					queriesService.queries = datatable.getData();					
				}
			},
			columns : [
			           {
			        	   property:"form",
			        	   render:function(v){
			        		 return JSON.stringify(v.form);  
			        	   },
			        	   header: "query.form",
			        	   type :"text"
			           },
			           {	
			        	   property:"nbResults",
			        	   header: "query.nbResults",
			        	   type :"number",		    	  	
			        	   order:true
			           }
			]
	};
	
	var url = jsRoutes.controllers.readsets.api.ReadSets.list().url;
	
	var updateResultQueries = function(queries){
		var promises = [];
		for(var i = 0; i < queries.length ; i++){
			if(!queries.nbResults){
				var form = angular.copy(queries[i].form);
				form.count = true;
				promises.push($http.get(url,{params:form, query:queries[i]}));
			}
		}
		return promises;
	}
	
	
	var queriesService = {
			datatable:undefined,
			queries:[],						
			loadDatatable:function(){
				this.datatable = datatable(datatableConfig);
				if(this.queries && this.queries.length > 0){
					$q.all(updateResultQueries(this.queries)).then(function(results){
						angular.forEach(results, function(value, key){
							value.config.query.nbResults = value.data;																
						});	
						queriesService.datatable.setData(queriesService.queries, queriesService.queries.length);
					});	
										
				} else {
					queriesService.datatable.setData([], 0);
				}
			},
			addQuery : function(query){
				this.queries = [query];
			},
			init : function(){
				this.queries = [];
			}
	};
	
	return queriesService;		
}
	

]).factory('chartsReadSetsService', ['$http', '$q','$parse', '$window', 'datatable', 'statsConfigReadSetsService','queriesConfigReadSetsService', 'lists', 'mainService',
                                     function($http, $q, $parse, $window, datatable, statsConfigReadSetsService, queriesConfigReadSetsService, lists, mainService){
	
	var datatableConfig = {
			group : {
				active : true,
				callback:function(datatable){
					computeCharts();
				}
			},
			search : {
				active:false
			},
			pagination:{
				mode:'local'
			},
			order:{
				mode:'local',
				orderBy:'code',
				callback:function(datatable){
					computeCharts();
				}
			},
			hide:{
				active:true
			}
	};
	var defaultDatatableColumns = [
			{	property:"code",
			  	header: "readsets.code",
			  	type :"text",		    	  	
			  	order:true,
			  	position:1
			},
			{	property:"runCode",
				header: "readsets.runCode",
				type :"text",
				order:true,
				group:true,
			  	position:2
			},
			{	property:"laneNumber",
				header: "readsets.laneNumber",
				type :"text",
				order:true,
			  	position:3
			},
			{	property:"projectCode",
				header: "readsets.projectCode",
				type :"text",
				order:true,
				group:true,
			  	position:4
			},
			{	property:"sampleCode",
				header: "readsets.sampleCode",
				type :"text",
				order:true,
				group:true,
			  	position:5
		  	},
		  	{	property:"runSequencingStartDate",
				header: "runs.sequencingStartDate",
				type :"date",
				order:true,
			  	position:6
		  	},
		  	{	property:"state.code",
				filter:"codes:'state'",
				header: "readsets.stateCode",
				type :"text",
				order:true,
			  	position:7
			},
		 	{	property:"productionValuation.valid",
				filter:"codes:'valuation'",
				header: "readsets.productionValuation.valid",
				
				type :"text",
		    	order:true,
			  	position:70
			},
			{	property:"productionValuation.resolutionCodes",
				header: "readsets.productionValuation.resolutions",
				render:'<div bt-select ng-model="value.data.productionValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
				type :"text",
				hide:true,
			  	position:72
			},
			{	property:"bioinformaticValuation.valid",
				filter:"codes:'valuation'",
				header: "readsets.bioinformaticValuation.valid",
				type :"text",
		    	order:true,
			  	position:80
			},
			{	property:"bioinformaticValuation.resolutionCodes",
				header: "readsets.bioinformaticValuation.resolutions",
				render:'<div bt-select ng-model="value.data.bioinformaticValuation.resolutionCodes" bt-options="valid.code as valid.name group by valid.category.name for valid in searchService.lists.getResolutions()" ng-edit="false"></div>',
				type :"text",
				hide:true,
			  	position:82
			}];
	var readsetDatatable;
	var charts = [];
	var excludeValues = [];
	var statsConfigs, queriesConfigs = [];
	var loadData = function() {
		
		if(chartService.reportingConfigurationCode !== undefined && chartService.reportingConfigurationCode !== null){
			$http.get(jsRoutes.controllers.stats.api.StatsConfigurations.get(chartService.reportingConfigurationCode).url).success(function(data, status,	headers, config) {
				statsConfigs = data.statsForm; 
				queriesConfigs =[{form : data.queryForm}];
				//synchronize other tab
				statsConfigReadSetsService.init();
				queriesConfigReadSetsService.init();
				mainService.setForm(null);
				if(angular.isDefined(mainService.getDatatable())){
					mainService.getDatatable().setData([]);					
				}
				generateCharts();
			});
		}else{
			if(statsConfigReadSetsService.isData() && queriesConfigReadSetsService.queries.length > 0){
				statsConfigs = statsConfigReadSetsService.getData();
				queriesConfigs = queriesConfigReadSetsService.queries;
				generateCharts();
			}else{
				statsConfigs=[];
				queriesConfigs=[];
				charts=[];
				readsetDatatable=undefined;
			}
		}
	};

	var generateCharts = function() {
		readsetDatatable = datatable(datatableConfig);
		readsetDatatable.config.spinner.start = true;
		var properties = ["default"];
		for(var i = 0; i < statsConfigs.length; i++){
			//remove .value if present to manage correctly properties (single, list, etc.)
			if(statsConfigs[i].column.queryIncludeKeys && statsConfigs[i].column.queryIncludeKeys.length > 0){
				properties = properties.concat(statsConfigs[i].column.queryIncludeKeys);
			}else{
				properties.push(statsConfigs[i].column.property.replace('.value',''));	
			}				
		}
			
		var promises = [];
			for(var i = 0; i < queriesConfigs.length ; i++){
				var form = angular.copy(queriesConfigs[i].form);
				form.includes = properties;
				promises.push($http.get(jsRoutes.controllers.readsets.api.ReadSets.list().url,{params:form}));			
			}
			
			$q.all(promises).then(function(results){
				var values = {r:[]};
				angular.forEach(results, function(value, key){
					this.r = this.r.concat(value.data);
					
				}, values);	
				var data = values.r;
				readsetDatatable = datatable(datatableConfig);
				
				var statsConfigsU = statsConfigs.filter(function(statsConfig){
					if(!this[statsConfig.column.property]){
						this[statsConfig.column.property] = true;
						return true;
					}else{
						return false;
					}
				}, []);
				
				readsetDatatable.setColumnsConfig(defaultDatatableColumns.concat(statsConfigsU.map(function(statsConfig){
					var column = angular.copy(statsConfig.column);
					column.order=true;
					return column;
				})));
				readsetDatatable.setData(data, data.length);
				readsetDatatable.config.spinner.start = false;
				computeCharts();
				
			});
	};	
							
	var computeCharts = function() {	
		charts = [];
		excludeValues = [];
		for (var i = 0; i < statsConfigs.length; i++) {
			var statsConfig = statsConfigs[i];
			if ("z-score" === statsConfig.typeCode) {
				charts.push(getZScoreChart(statsConfig));
			} else if ("simple-value" === statsConfig.typeCode) {
				charts.push(getSimpleValueChart(statsConfig));
			} else {
				throw 'not manage'+ statsConfig.typeCode;
			}
		}
		// Same color for all charts
		var nbGroups = charts[0].series.length;
		if(nbGroups > 10){
			var colors = [];
			
			// Random colors with maths magic !
			for(var k = 0; k < nbGroups; k++){
				colors.push('#'+Math.floor(Math.random()*16777215).toString(16));
			}			
			for(var i = 0; i < charts.length; i++){
				for(var j = 0; j < charts[i].series.length; j++){
					charts[i].series[j].color = colors[j];
				}
			}
		}
	};

	var getProperty = function(column) {
		if (column.property) {
			var p = column.property
			if (column.filter) {
				p += '|' + column.filter;
			}
			// TODO format
			return p;
		} else {
			throw 'no property defined for column '	+ Messages(column.header);
		}
	};
	
	var getGroupValues = function(dataMustBeGroup){
		var groupValues = dataMustBeGroup.reduce(function(array, value){
			var groupValue = value._group;
			if(!array[groupValue]){
				array[groupValue]=[];
			}
			array[groupValue].push(value);
			return array;
		}, {});
		return groupValues;
	};
	
	var getZScoreChart = function(statsConfig) {
		var data = readsetDatatable.getData();
		
		if(readsetDatatable.config.group.by != undefined){
			var propertyGroupGetter = readsetDatatable.config.group.by.property;
			var groupGetter = $parse(propertyGroupGetter);
		}
		
		var property = getProperty(statsConfig.column);
		var getter = $parse(property);
		
		data = excludeData(data, getter, 'z-score : '	+ Messages(statsConfig.column.header));
		
		var statData = data.map(function(value) {
			return getter(value)
		});
		var mean = ss.mean(statData);
		var stdDev = ss.standard_deviation(statData);
		var i = 0;
		var zscoreData = data.map(function(x) {
			return {
				name : x.code,
				y : ss.z_score(getter(x), mean,stdDev),
				x : i++,
				_value : getter(x),
				_group : (groupGetter)?groupGetter(x):undefined
			};
		});

		if(readsetDatatable.config.group.by != undefined){
			zscoreData = getGroupValues(zscoreData);
		}else{
			zscoreData = {"z-score":zscoreData};
		}
		
		// Creating object allSeries that'll contain all our series, whether or not we're using group function on datatable
		var allSeries = [];
		for(var key in zscoreData){
			allSeries.push({
				point : {
					events : {
						// Redirects to valuation page of the clicked readset
						click : function() {
							$window.open(jsRoutes.controllers.readsets.tpl.ReadSets.get(this.name).url);
						}
					}
				},
				data : zscoreData[key],
				name : key,
				type : 'column',
				turboThreshold : 0
			});						
		}
		
		var chart = {
			chart : {
                zoomType : 'x',
				height : 770
			},
			title : {
				text : 'z-score : '	+ Messages(statsConfig.column.header)
			},
			tooltip : {
				formatter : function() {
					var s = '<b>' + this.point.name	+ '</b>';
					s += '<br/>'+ this.point.series.name+ ': ' + this.point.y;
					s += '<br/>'+ Messages(statsConfig.column.header)+ ': ' + this.point._value;
					return s;
				}
			},
			xAxis : {
				title : {
					text : 'Readsets',
				},
				labels : {
					enabled : false,
					rotation : -75
				},
				type : "category",
				tickPixelInterval : 1
			},

			yAxis : {
				title : {
					text : 'z-score'
				},
				tickInterval : 2,
				plotLines : [ {
					value : -2,
					color : 'green',
					dashStyle : 'shortdash',
					width : 2,
					label : {
						text : 'z-score = -2'
					}
				}, {
					value : 2,
					color : 'red',
					dashStyle : 'shortdash',
					width : 2,
					label : {
						text : 'z-score = 2'
					}
				} ]
			},
			series : allSeries,
			plotOptions : {column:{grouping:false}}
		};
		return chart;
	};
	
	var excludeData = function(data, getter, title){
		var excludeData = {
				title:title,
				data:[]
		};
		
		data = data.filter(function(elt){
			if(getter(elt) != undefined){
				return elt;
			}else{
				excludeData.data.push(elt);
			}
				
		});
		if(excludeData.data.length > 0){
			excludeValues.push(excludeData);
		}
		return data;
	};
	
	var getSimpleValueChart = function(statsConfig) {
		var data = readsetDatatable.getData();
		
		if(readsetDatatable.config.group.by != undefined){
			var propertyGroupGetter = readsetDatatable.config.group.by.property;
			var groupGetter = $parse(propertyGroupGetter);
		}
		var property = getProperty(statsConfig.column);
		var getter = $parse(property);
		
		data = excludeData(data, getter, Messages(statsConfig.column.header));
		
		var i = 0;
		var statData = data.map(function(x) {
			return {
				name : x.code,
				y : getter(x),
				x : i++,
				_group : (groupGetter)?groupGetter(x):undefined
			};
		});
				
		if(readsetDatatable.config.group.by != undefined){
			statData = getGroupValues(statData);
		}else{
			statData = {"simple-value":statData};
		}
		
		var allSeries = [];
		for(var key in statData){
			allSeries.push( {
				point : {
					events : {
						// Redirects to valuation page of the clicked readset
						click : function() {
							$window.open(jsRoutes.controllers.readsets.tpl.ReadSets.get(this.name).url);
						}
					} 
				},
				data : statData[key],
				name : key,
				turboThreshold : 0,
				type : 'column',
			});						
		}
		
		var chart = {
			chart : {
				zoomType : 'x',
				height : 770
			},
			title : {
				text : Messages(statsConfig.column.header)
			},
			tooltip : {
				formatter : function() {
					var s = '<b>' + this.point.name+ '</b>';
					s += '<br/>'+ this.point.series.name+ ': ' + this.point.y;
					return s;
				}
			},
			xAxis : {
				title : {
					text : 'Readsets',
				},
				labels : {
					enabled : false,
					rotation : -75
				},
				type : "category",
				tickPixelInterval : 1
			},
			yAxis : {
				title : {
					text : Messages(statsConfig.column.header)
				}
			},
			series : allSeries,
			plotOptions : {column:{grouping:false}}
		};
		return chart;
	}

	var chartService = {
		datatable : function() {return readsetDatatable;},
		charts : function() {
			return charts;
			},
		excludeValues : function(){
			return excludeValues;
		},
		lists : lists,
		reportingConfigurationCode : undefined,
		init : function() {
			this.lists.refresh.statsConfigs({pageCodes : ["readsets-show" ]});
			loadData();
		},
		changeConfig : function(){
			loadData();
		},
		queries:function() {
			return queriesConfigs;
		}
	};
	return chartService;

} ]);
