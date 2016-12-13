<?php
require "connect.php";

$user_name = $_POST["user_name"];
$lat = $_POST["lat"];
$lng = $_POST["lng"];
$date = $_POST["time"];


$query = 
sprintf
(
	"INSERT INTO AppLocations( username ,latitude ,longitude, time) VALUES('%s','%s','%s', '%s') ON DUPLICATE KEY UPDATE latitude = '%s',longitude = '%s', time ='%s' "
         , $user_name, $lat, $lng, $date,$lat,$lng,$date
);


$result = mysqli_query($conn,$query);

if( mysqli_num_rows($result) > 0 )
{

}
mysqli_close($conn);
?>

