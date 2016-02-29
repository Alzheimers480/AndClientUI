<?php


require "index.php";


function authUser($username, $password) {

	if(empty($username) || empty($password)){
		//empty username and password
		return false;
	}
	$conn = connectToDB();
	if(!$conn){
		echo "conn failure ";
		return false;
	}


	$stmnt=$conn->prepare("SELECT USER_PWDSALT FROM USERS2 WHERE USER_UID = ?");//We use Prepared Statements to protect against SQL injection attacks
	$stmnt->bind_param('s',$username);

	$stmnt->execute();
	$stmnt->bind_result($salt);

	$stmnt->fetch();
	$stmnt->close();
	if(!$salt) {
		echo "user wasn't found ";
		return false;
	}

	$phash = crypt($password,$salt);
	

	$stmnt = $conn->prepare("SELECT USER_UID FROM USERS2 WHERE USER_UID = ? AND USER_PWDHSH = ?");
	$stmnt->bind_param('ss',$username,$phash);

	$stmnt->execute();
	$stmnt->bind_result($uid);

	while($stmnt->fetch()) {
		if($uid == $username) {
			$stmnt->close();
			return true;

		} else {
			echo "password incorrect";
			return false;
		}

	}
	return false;
}


$fail=false;
session_start();
if(authUser($_POST["USERNAME"], $_POST["PASSWORD"])) {
	echo "True";
}
else {
	echo "False";
}
	



?>
