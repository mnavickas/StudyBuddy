<?php
require "connect.php";

$user_to = $_POST["user_to"];



$query = 
sprintf
(
	"SELECT * from AndroidMessaging WHERE user_to = '%s' ", $user_to
);



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
	$to = $row['user_to'];
	$from = $row['user_from'];
	$msg = $row['msg'];
        $msg_id = $row['msg_id'];

	echo sprintf("%s,%s,%s,%d",$to, $from, $msg, $msg_id);

    }
}
else
{
     echo("");
}

$query2= sprintf( "DELETE FROM AndroidMessaging WHERE user_to ='%s' ", $user_to);

$result = mysqli_query($conn,$query2);

mysqli_close($conn);
?>

