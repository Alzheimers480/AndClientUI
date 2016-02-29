<?php

require "index.php";
	
function changePass($user, $password, $password2){
	 if(empty($user) || empty($password)||empty($password2)){
                //empty username and password
                return false;
        }
	if($password!=$password2){
		echo "the passwords didn't match ";
		return false;
	}
        $conn = connectToDB();
        if(!$conn){
                echo "conn failure ";
                return false;
        }
	$stmnt2=$conn->prepare("SELECT * FROM USERS2 WHERE USER_UID = ?;");
        $stmnt2->bind_param('s', $user);
        $stmnt2->execute();
        $stmnt2->store_result();
        $amount = $stmnt2->num_rows;
        if($amount==0){
                echo "user does not exists ";
                return false;
        }
        $stmnt2->close();
	$stmnt=$conn->prepare("UPDATE USERS2 SET USER_PWDHSH=?, USER_PWDSALT= ? WHERE USER_UID = ?;");
        $salt = file_get_contents('/dev/urandom', false, null, 0, 64);
        $options = array(
               'salt' => $salt
       );
        $phash=crypt($password,$salt);
        $stmnt->bind_param('sss',$phash,$salt,$user);
        $stmnt->execute();
        $stmnt->close();
        $conn->close();
        return true;
}

$fail=false;
session_start();
if(changePass($_POST["USERNAME"], $_POST["PASSWORD"], $_POST["PASSWORD2"])){
	echo "True";
}
else{
	echo "False";
}
?>
