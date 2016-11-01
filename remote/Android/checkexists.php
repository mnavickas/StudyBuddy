<?php
require "connect.php";

$user_name = $_POST["user_name"];
$user_pass = $_POST["password"];

$query = sprintf("SELECT * FROM AndroidApp WHERE username = '%s'",$user_name);

$result = mysqli_query($conn,$query);

if( mysqli_num_rows($result) > 0 )
{
	
	echo ("Success");

}
else
{
	echo ("Failure_User");
}
mysqli_close($conn);
?>