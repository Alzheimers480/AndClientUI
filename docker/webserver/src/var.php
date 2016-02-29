<?php
require "index.php";
$fail=false;
session_start();
$username = $_POST["USERNAME"];
$conn = connectToDB();
if(!$conn){
	echo "conn failure";
}
$stmnt=$conn->prepare("SELECT VERIFYED FROM USERS2 WHERE USER_UID = ?");
$stmnt->bind_param('s',$username);
$stmnt->execute();
$stmnt->bind_result($ver);
$stmnt->fetch();
$stmnt->close();
echo $ver;
?>
