<?php
require "index.php";
$fail=false;
session_start();
$email = $_GET["USERNAME"];
$conn = connectToDB();
if(!$conn){
	echo "conn failure";
}
$stmnt = $conn->prepare("UPDATE USERS2 SET VERIFYED=1 WHERE USER_UID = ?;");
$stmnt->bind_param('s',$email);
$stmnt->execute();
?>
