<!DOCTYPE html>
<html lang="">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Title Page</title>

	<!-- Bootstrap CSS -->
	<link href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css" rel="stylesheet">
	<link href="/search/index.css">	

	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
		<!--[if lt IE 9]>
			<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
			<script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
			<![endif]-->
		</head>
		<body>
			<nav class="navbar navbar-default" role="navigation">
				<div class="container-fluid ">
					<div >
						<ul class="nav navbar-nav pull-right">
							<li>
								<p class="navbar-btn">
									<a href="/j_spring_security_logout" class="btn btn-default btn-danger">Logout</a>
								</p>
							</li>
						</ul>
					</div>					
				</div>
			</nav>	
			<div class="container">
				<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">

				</div>
				<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">

					
					<form action="" method="GET" class="form col-xs-12 col-sm-12 col-md-12 col-lg-12" role="form">
						<h1>Woogle</h1>
						<div class="form-group">
							<label class="sr-only" for="">label</label>
							<input type="text" class="form-control" id="" placeholder="" name="q">							
						</div>			
						
						<button type="submit" class="btn btn-default btn-sm center-block">Search</button>				
						
					</form>
				</div>
				<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">

				</div>
			</div>


			<!-- jQuery -->
			<script src="//code.jquery.com/jquery.js"></script>
			<!-- Bootstrap JavaScript -->
			<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
			<script>
				(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
					(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
					m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
				})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

				<#if user??>
				ga('create', 'UA-54565647-1', { 'userId': '${user}' });
				<#else>
				ga('create', 'UA-54565647-1', 'auto');
				</#if>
				ga('send', 'pageview');

			</script>
		</body>
		</html>