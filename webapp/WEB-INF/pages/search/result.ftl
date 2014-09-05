<!DOCTYPE html>
<html lang="" ng-app="searchApp">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Result</title>

	<!-- Bootstrap CSS -->
	<link href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css" rel="stylesheet">
	<link href="/css/search/result.css" rel="stylesheet">

</head>
<body>			
	<nav class="navbar navbar-default" role="navigation">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">      
				<a class="navbar-brand" href="index.html">Woogle</a>
			</div>
		</div>
	</nav>	
	<div class="container" ng-controller="resultController">
		<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">			
		</div>
		<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">
			<div ng-repeat="content in result">
				<div class="title"><a href="/document/{{content.id}}.html" ><h4>{{content.title}}</h4></a></div>
				<pre ng-bind-html="content.content"></pre>				
			</div>
			<div class="text-center">
				<ul class="pagination">
					<#-- <li ng-class="{'disabled': page == 1}"><a ng-disabled= "page == 1" href="?page={{page-1}}&amp;q={{q}}">&laquo;</a></li> -->
					<li ng-class="{'active': ($index+1) == page}" ng-repeat="i in getNumber() track by $index" ><a href="?q={{q}}&amp;page={{$index+1}}">{{$index+1}} <span class="sr-only">(current)</span></a></li>				
					<#-- <li><a href="?page={{page+1}}&amp;q={{q}}">&raquo;</a></li> -->
				</ul>
			</div>
		</div>
		<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
			
		</div>
	</div>
	<!-- jQuery -->
	<script src="//code.jquery.com/jquery.js"></script>
	<!-- Bootstrap JavaScript -->
	<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.23/angular.min.js"></script>
	<script src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.2.20/angular-sanitize.min.js"></script>	
	<script type="text/javascript">		
		var result = ${result};
		var numOfPages = ${numOfPages};

		function getQueryStrings() { 
			var assoc  = {};
			var decode = function (s) { return decodeURIComponent(s.replace(/\+/g, " ")); };
			var queryString = location.search.substring(1); 
			var keyValues = queryString.split('&'); 

			for(var i in keyValues) { 
				var key = keyValues[i].split('=');
				if (key.length > 1) {
					assoc[decode(key[0])] = decode(key[1]);
				}
			} 
			return assoc; 
		} 			

		var queryParams = getQueryStrings();		

		var app = angular.module('searchApp', ['ngSanitize']);
		app.controller('resultController', ['$scope', function($scope){
			$scope.q = queryParams['q'];
			$scope.page = queryParams['page']==undefined?1:queryParams['page'];
			$scope.result = result;
			$scope.getNumber = function(){
				return new Array(numOfPages);
			}
			$scope.formatContent = function(content){				
				return content.substring(0, 300);

			}			
		}])
	</script>
</body>
</html>