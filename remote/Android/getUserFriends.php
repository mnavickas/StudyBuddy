<?php
require "connect.php";

$username = $_POST["user_name"];
$query = sprintf("SELECT friends_list FROM AppFriends WHERE username = '%s' ",$username);

$result = mysqli_query($conn,$query);

if( mysqli_num_rows($result) > 0 )
{
    $row = mysqli_fetch_assoc($result);
    $list = $row['friends_list'];
    echo ( $list );
}
else
{
     echo("");
}

mysqli_close($conn);
?>
