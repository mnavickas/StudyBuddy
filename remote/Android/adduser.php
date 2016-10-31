<?php
require "connect.php";

$user_name = $_POST["user_name"];
$user_pass = $_POST["password"];

$hashed = SHA1($user_pass);

$query = sprintf("INSERT INTO AndroidApp (username,password) VALUES ('%s','%s') ", $user_name, $hashed);

mysqli_query($conn,$query);

?>