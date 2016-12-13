<?php
require "connect.php";

$userTo = $_POST["user"];


$query = sprintf( "SELECT msg,time,user_to,user_from FROM AndroidMessaging WHERE (user_to = '%s') OR (user_from = '%s') ", $userTo, $userTo);

$result = mysqli_query($conn,$query);

if( mysqli_num_rows($result) > 0 )
{
    while ($row = mysqli_fetch_assoc($result))
    {
       $msg = $row['msg'];
       echo ($msg);
       echo (",");

       $time = $row['time'];
       echo ($time);
       echo (",");

       $to= $row['user_to'];
       echo ($to);
       echo (",");

       $from= $row['user_from'];
       echo ($from);

       echo ("$");
    }

}
else
{
     echo("");
}

mysqli_close($conn);
?>
