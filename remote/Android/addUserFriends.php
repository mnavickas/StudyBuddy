<?php
require "connect.php";

$friends = $_POST["friends"];
$user = $_POST["user"];


$query = 
sprintf
(
	"INSERT INTO AppFriends( username,friends_list ) VALUES('%s','%s') ON DUPLICATE KEY UPDATE friends_list = '%s'"
         ,$user,$friends,$friends
);


$result = mysqli_query($conn,$query);

if( mysqli_num_rows($result) > 0 )
{

}
mysqli_close($conn);
?>
