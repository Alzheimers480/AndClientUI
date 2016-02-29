<?php
require "index.php";
$fail=false;
session_start();
$id = $_POST["USERNAME"];
$fname = $_POST["FNAME"];
$lname = $_POST["LNAME"];

function newAcqua($username, $fname, $lname){
	if(empty($username) | empty($fname) | empty($lname)){
		echo "one or more paramters missing ";
		return false;
	}
	$conn = connectToDB();
	if(!$conn){
                echo "conn failure ";
                return false;
        }
	$stmnt2=$conn->prepare("SELECT * FROM ACQUAINTANCE WHERE ACQUAINTANCE_UID = ?;");
        $stmnt2->bind_param('s', $username);
        $stmnt2->execute();
        $stmnt2->store_result();
        $amount = $stmnt2->num_rows;
	if($amount>=1){
                echo "Acquaintance already exists ";
                return false;
        }
        $stmnt2->close();
	$stmnt = $conn->prepare("INSERT INTO ACQUAINTANCE(ACQUAINTANCE_UID, ACQUAINTANCE_FNAME, ACQUAINTANCE_LNAME, PICTURE_SET) VALUES(?,?,?,'/SECS/home/s/scnolton/facePics/$username')");
	$stmnt->bind_param('sss', $username, $fname, $lname);
	$stmnt->execute();
	$stmnt->close();
	$conn->close();
	return true;
}



$fail = false;
session_start();
if(newAcqua($id, $fname, $lname)){
	echo "New Acquaintance Created";
	$dir = "/SECS/home/s/scnolton/facePics/$id";
	if (!file_exists($dir)){
		$oldmask = umask(0);
		mkdir ($dir, 0777);
	}
	else{
		echo " dir already exists!";
	}
}
else{
	echo "Unable to Create New Acquaintance";
}

?>
