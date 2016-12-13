<?php
require "connect.php";

$query = "SELECT * FROM AppLocations";

$result = mysqli_query($conn,$query);

$first = true;
if( mysqli_num_rows($result) > 0 )
{
    while ($row = mysqli_fetch_assoc($result))
    {

	if($first != true)
	{
	     echo('&');	
	}
	else
        {
	     $first = false;
        }
	$name = $row['username'];
	$lat = $row['latitude'];
	$lng = $row['longitude'];
        $time = $row['time'];
	$course = $row['CourseNum'];
        $subject = $row['Subject'];
        $comments = $row['Comments'];

	echo sprintf("%s,%.10f,%.10f,%s,%s,%s,%s",$name, $lat, $lng, $time,$course,$subject,$comments);

    }


}
else
{
     echo("");
}
mysqli_close($conn);
?>
