<?php
function connectToDB() {
	$host = getenv("DB_PORT_3306_TCP_ADDR");
	$user = "root";
	$pass = "my-secret";
	$db = "morty";
	$conn = new mysqli($host, $user, $pass, $db);
	if($conn->connect_error) {
		return null;
	}
	return $conn;
}
?>