<?php
require "connect.php";

$user_from = $_POST["user_from"];
$user_to = $_POST["user_to"];
$msg = $_POST["msg"];
$msg_id = $_POST["msg_id"];


$query = 
sprintf
(
	"INSERT INTO AndroidMessaging ( user_to, user_from, msg, msg_id ) VALUES('%s', '%s', '%s', '%s')"
         , $user_from, $user_to, $msg, $msg_id
);


$result = mysqli_query($conn,$query);

if( mysqli_num_rows($result) > 0 )
{

}
mysqli_close($conn);
?>

