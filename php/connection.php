<?php
	$username = "root";
	$password = "dedicatedtomyundergrad";
	$url = "localhost";
	$database = "myteam";
	$conn =  mysqli_connect($url, $username, $password, $database);
	if(!$conn){
		die("connection_failed:" .mysqli_connect_error());
	}
?>
