<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>  
<!DOCTYPE html>
<html lang="" ng-app="searchApp">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Result</title>

	<!-- Bootstrap CSS -->
	<link
	href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css"
	rel="stylesheet">
	<link href="/css/search/result.css" rel="stylesheet">

</head>
<body>
	<nav class="navbar navbar-default navbar-fixed-top" role="navigation" ng-controller="navbarController">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<a class="navbar-brand" href="index.html">Woogle</a>
			</div>
			<div class="collapse navbar-collapse">

				<form class="navbar-form navbar-right" role="search">				
					<span style="font-weight: bold;">為了提供更好地搜尋體驗，若此為不佳的搜尋結果，請按右邊按鈕幫忙舉報，感謝！</span>
					<button type="submit" class="btn btn-danger" ng-show="reported==false" ng-click="reportPoorResult()">此為不佳的搜尋結果。</button>
					<button type="submit" class="btn btn-success" ng-show="reported==true">感謝您的回覆！</button>
				</form>
			</div>
		</div>
	</nav>
	<div class="container" ng-controller="resultController" style=" padding-top: 60px;">
		<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2"></div>
		<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">
			<div ng-repeat="content in result">
				<div class="title">
					<a href="/document/{{content.id}}.html"><h4
						ng-bind-html="content.publishDate + ' [' + content.category + '] ' +content.title"></h4></a>
					</div>
					<pre ng-bind-html="content.content"></pre>
				</div>
				<div class="text-center">
					<ul class="pagination">

					<%-- <li ng-class="{'disabled': page <= 1}"><a
					ng-disabled="page <= 1" href="?page={{page-1}}&amp;q={{q}}">&laquo;</a></li> --%>
					

					<li ng-class="{'active': (i) == page}"
					ng-repeat="i in getNumber() track by $index"><a
					href="?q={{q}}&amp;page={{i}}">{{i}} <span class="sr-only">(current)</span></a></li>
					
					<!-- <li><a href="?page={{page+1}}&amp;q={{q}}">&raquo;</a></li> -->
				</ul>
			</div>
		</div>
		<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2"></div>
	</div>
	<!-- jQuery -->
	<script src="//code.jquery.com/jquery.js"></script>
	<!-- Bootstrap JavaScript -->
	<script
	src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
	<script
	src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.23/angular.min.js"></script>
	<script
	src="//cdnjs.cloudflare.com/ajax/libs/angular.js/1.2.20/angular-sanitize.min.js"></script>
	<script type="text/javascript">
		var result =
		<%=request.getAttribute("result")%>
		;

		var numOfPages =
		<%=request.getAttribute("numOfPages")%>
		;

		function getQueryStrings() {
			var assoc = {};
			var decode = function(s) {
				return decodeURIComponent(s.replace(/\+/g, " "));
			};
			var queryString = location.search.substring(1);
			var keyValues = queryString.split('&');

			for ( var i in keyValues) {
				var key = keyValues[i].split('=');
				if (key.length > 1) {
					assoc[decode(key[0])] = decode(key[1]);
				}
			}
			return assoc;
		}

		var queryParams = getQueryStrings();

		var app = angular.module('searchApp', [ 'ngSanitize' ]);
		app.controller(
			'resultController',
			[
			'$scope',
			function($scope) {
				$scope.q = queryParams['q'];
				$scope.page = queryParams['page'] == undefined ? 1
				: parseInt(queryParams['page']);
				$scope.result = result;
				$scope.getNumber = function() {
					var pages = []
					var currentPage = parseInt($scope.page) - 5;
					while (pages.length < 10
						&& currentPage <= numOfPages) {
						if (currentPage > 0) {
							pages.push(currentPage);
						}
						currentPage++;
					}
					return pages;
				}
				$scope.formatContent = function(content) {
					return content.substring(0, 300);

				}
			} ]);
		app.controller(
			'navbarController',
			[
			'$scope','$http',
			function($scope, $http) {
				$scope.reported = false;
				$scope.reportPoorResult = function(){
					$http({
						method: 'POST',
						url: 'reportPoorResult.html',
						data: {searchTerm: queryParams['q']}
					}).success(function(){
						$scope.reported=true;
					})
				};				
			} ]);
		</script>
		<script>
			(function(i, s, o, g, r, a, m) {
				i['GoogleAnalyticsObject'] = r;
				i[r] = i[r] || function() {
					(i[r].q = i[r].q || []).push(arguments)
				}, i[r].l = 1 * new Date();
				a = s.createElement(o), m = s.getElementsByTagName(o)[0];
				a.async = 1;
				a.src = g;
				m.parentNode.insertBefore(a, m)
			})(window, document, 'script',
			'//www.google-analytics.com/analytics.js', 'ga');
			<%if (request.getAttribute("user") != null) {%>
				ga('create', 'UA-54565647-1', {
					'userId' : '${user}'
				});
				<%} else {%>
					ga('create', 'UA-54565647-1', 'auto');
					<%}%>
					ga('send', 'pageview');
				</script>
			</body>
			</html>