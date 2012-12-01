<?php
require_once("includes/class/Message.class.php");		
if($_GET['action'] == "sendnew"){
	$msg = new Message();
	$msg->setSender($_POST['sender']);
	$msg->setReceiver($_POST['receiver']);
	$msg->setMessage($_POST['message']);
	$msg->setFecha($_POST['date']);
	if($msg->insertar())
		echo "OK";
	else
		echo "ERROR";
}

if($_GET['action'] == "getmessages"){
	$msg = new Message();
	$result = $msg->getMessages($_POST['receiver'], $_POST['minid']);
	while($row = mysql_fetch_array($result, MYSQL_NUM)){
		//0=msg, 1=fecha
		echo $row[0]."%%".$row[1];
	}
}

?>
