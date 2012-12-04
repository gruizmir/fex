<?php
require_once("includes/class/Artist.class.php");		
if($_GET['action'] == "register"){
	$art = new Artist();
	$art->setArtista($_POST['artist']);
	$art->setVideo1($_POST['video1']);
	$art->setVideoName1($_POST['videoname1']);
	$art->setVideo2($_POST['video2']);
	$art->setVideoName2($_POST['videoname2']);
	$art->setVideo3($_POST['video3']);
	$art->setVideoName3($_POST['videoname3']);
	$art->setFestival($_POST['festival']);
	if($art->insertar())
		echo "OK";
	else
		echo "ERROR";
}

if($_GET['action'] == "getids"){
	$art = new Artist();
	$result = $art->getIds($_POST['festival']);
	while($row = mysql_fetch_array($result, MYSQL_NUM)){
		//0=msg, 1=fecha
		echo $row[0]."%%".utf8_decode($row[1])."</br>";
	}
}

if($_GET['action'] == "getvideos"){
	$art = new Artist();
	$art->getArtist($_POST['id']);
	echo $art->getVideo1()."++".$art->getVideoName1().",".$art->getVideo2()."++".$art->getVideoName2().",".$art->getVideo3()."++".$art->getVideoName2();
}
?>
