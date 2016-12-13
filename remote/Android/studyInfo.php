<?php
require "connect.php";

$course = $_POST["course"];
$subject = $_POST["subject"];
$comments = $_POST["comments"];
$user = $_POST["user"];

$query = 
sprintf
(
	"INSERT INTO AppLocations( username ,CourseNum ,Subject,Comments) VALUES('%s','%s','%s', '%s') ON DUPLICATE KEY UPDATE CourseNum = '%s',Subject = '%s', Comments ='%s' "
         ,$user, $course, $subject, $comments, $course, $subject,$comments
);


$result = mysqli_query($conn,$query);

if( mysqli_num_rows($result) > 0 )
{

}
mysqli_close($conn);
?>
