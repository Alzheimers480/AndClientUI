<?php
require "index.php";
function deleteUser($user){
	if(empty($user)){
                //empty username and password
                return false;
        }
        $conn = connectToDB();
        if(!$conn){
                echo "conn failure ";
                return false;
        }
	$stmnt=$conn->prepare("DELETE FROM RELATIONSHIP WHERE USER_UID = ?;");
	$stmnt->bind_param('s', $user);
	$stmnt->execute();
	$stmnt->close();
	$stmnt2=$conn->prepare("DELETE FROM USERS2 WHERE USER_UID = ?;");
	$stmnt2->bind_param('s', $user);
	$stmnt2->execute();
	$stmnt2->close();
	$conn->close();
	return true;
}

$fail=false;
session_start();
if(deleteUser($_POST["USERNAME"])){
	echo "True";
}
else{
	echo "False";
}

?>
