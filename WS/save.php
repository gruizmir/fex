<?php
require_once("includes/class/User.class.php");		
if($_GET['action'] == "register"){
	$user = new User();
	$user->setFbId($_POST['fbid']);
	$user->setEmail($_POST['email']);
	$user->setLatitude($_POST['latitude']);
	$user->setLongitude($_POST['longitude']);
	if($user->insertar())
		echo "OK";
	else
		echo "ERROR";
}

if($_GET['action'] == "updateloc"){
	$user = new User();
	$user->setFbId($_POST['fbid']);
	$user->setLatitude($_POST['latitude']);
	$user->setLongitude($_POST['longitude']);
	if($user->updateLoc())
		echo "OK";
	else
		echo "ERROR";
}	

if($_GET['action'] == "setblockedlistid"){
	$user = new User();
	$user->setFbId($_POST['fbid']);
	$user->setBlockedListId($_POST['blockedlistid']);
	if($user->updateBlock())
		echo "OK";
	else
		echo "ERROR";
}
	
if($_GET['action'] == "getloc"){
	$user = new User();
	$user->getUser($_POST['fbid']);
	echo $user->getLatitude()."-".$user->getLongitude();
}

if($_GET['action'] == "getblockedlistid"){
	$user = new User();
	$user->getUser($_POST['fbid']);
	echo $user->getBlockedListId();
}
?>
