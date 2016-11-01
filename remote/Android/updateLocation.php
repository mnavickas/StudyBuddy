<?php
require "connect.php";

$user_name = $_POST["user_name"];
$lat = $_POST["lat"];
$lng = $_POST["lng"];


$query = 
sprintf
(
	"INSERT INTO AppLocations( username ,latitude ,longitude) VALUES('%s','%s','%s') ON DUPLICATE KEY UPDATE latitude = '%s',longitude = '%s', time = NOW() "
         , $user_name, $lat, $lng,$lat,$lng
);


$result = mysqli_query($conn,$query);

if( mysqli_num_rows($result) > 0 )
{

}
mysqli_close($conn);
?>