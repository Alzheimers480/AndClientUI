<?php
require "index.php";

function newRelation($userId, $acquId, $relation, $message){
	if(empty($userId)|empty($acquId)|empty($relation)|empty($message)){
		echo "one fo the fields was blank ";
		return false;
	}
	$conn = connectToDB();
	if(!$conn){
		echo "conn failure ";
		return false;
	}
	$stmnt=$conn->prepare("SELECT * FROM USERS2 WHERE USER_UID = ?;");
	$stmnt->bind_param('s',$userId);
	$stmnt->execute();
	$stmnt->store_result();
	$amount = $stmnt->num_rows;
	if($amount==0){
		echo "user does not exists ";
		return false;
	}
	$stmnt->close();
	$stmnt2=$conn->prepare("SELECT * FROM ACQUAINTANCE WHERE ACQUAINTANCE_UID = ?;");
        $stmnt2->bind_param('s',$acquId);
        $stmnt2->execute();
        $stmnt2->store_result();
        $amount = $stmnt2->num_rows;
	if($amount==0){
                echo "Acuqintance ID does not exists ";
                return false;
        }
	$stmnt2->close();
	$stmnt3=$conn->prepare("INSERT INTO RELATIONSHIP(USER_UID, ACQUAINTANCE_UID, RELATION, DESCRIPTION) VALUES(?,?,?,?);");
	$stmnt3->bind_param('ssss', $userId, $acquId, $relation, $message);
	$stmnt3->execute();
	$stmnt3->close();
	$conn->close();
	return true;
	

}
$fail = false;
session_start();
if(newRelation($_POST["USERNAME"], $_POST["ACQUNAME"], $_POST["RELATION"], $_POST["MESSAGE"])){
	echo "True";
}
else{
	echo "False";
}
?>
