"use strict";
angular.module('home', ['datatableServices','commonsServices','biCommonsServices', '$strap.directives', 'ui.bootstrap'], function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/readsets/search/home', {
		templateUrl : '/tpl/readsets/search/default',
		controller : 'SearchCtrl'
	});
	$routeProvider.when('/readsets/valuation/home', {
		templateUrl : '/tpl/readsets/search/valuation',
		controller : 'SearchValuationCtrl'
	});
	$routeProvider.when('/readsets/state/home', {
		templateUrl : '/tpl/readsets/search/state',
		controller : 'SearchStateCtrl'
	});
	$routeProvider.when('/readsets/:code', {
		templateUrl : '/tpl/readsets/details',
		controller : 'DetailsCtrl'
	});
	$routeProvider.when('/readsets/:code/:page', {
		templateUrl : '/tpl/readsets/details',
		controller : 'DetailsCtrl'
	});
	
	$routeProvider.otherwise({redirectTo: '/readsets/search/home'});

	// configure html5 to get links working with bookmarked
	$locationProvider.html5Mode(true);
});

