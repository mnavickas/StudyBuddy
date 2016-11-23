<?php
require "connect.php";

$user_name = $_POST["user_name"];
$user_pass = $_POST["password"];

$query = sprintf("SELECT password FROM AndroidApp WHERE username = '%s'",$user_name);

$result = mysqli_query($conn,$query);

if( mysqli_num_rows($result) > 0 )
{
	$row = mysqli_fetch_assoc($result);
	$pass = $row['password'];
	
	if( $pass === SHA1($user_pass) )
	{
		echo ("Success");
	}
	else
	{
		echo ("Failure_Pass");
	}
}
else
{
	echo ("Failure_User");
}

mysqli_close($conn);
?>
