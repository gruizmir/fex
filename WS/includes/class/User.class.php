<?php
	require_once("BaseDatos.class.php");
	
	class User extends BaseDatos
	{
		function User(){ }
		
		function setFbId($fbId){
			$this->fbId = $fbId;
		}
		function setEmail($email){
			$this->email=$email;
		}
		function setBlockedListId($blockedListId){
			$this->blockedListId=$blockedListId;
		}
		function setLatitude($latitude){
			$this->latitude = $latitude;
		}
		function setLongitude($longitude){
			$this->longitude = $longitude;
		}
		function setAvailable($avalaible){
			$this->available = $avalaible;
		}		
		function getBlockedListId(){
			return $this->blockedListId;
		}
		function getLatitude(){
			return $this->latitude;
		}
		function getLongitude(){
			return $this->longitude;
		}
		function getEmail(){
			return $this->email;
		}
		function isAvailable(){
			if($this->$available==0)
				return false;
			else
				return true;
		}		
		function insertar(){
			$consulta = "INSERT INTO User(fbId, BlockedId, Email, Latitude, Longitude, Available) VALUES ('$this->fbId','$this->blockedListId','$this->email','$this->latitude', '$this->longitude', 1);";
			return $this->insert($consulta);	
		}
		
		function getUser($fbId){
			require_once("includes/config.php");
			$sql="SELECT fbId, BlockedId, Latitude, Longitude, Available FROM User WHERE fbId='$fbId'";
			$this->conectar();
			$resultado = $this->query($sql);
			if($this->numRows($resultado) == 1){
				$row=mysql_fetch_array($resultado, MYSQL_NUM);
				$this->fbId=$fbId;
				$this->blockedListId=$row[1];
				$this->latitude=$row[2];
				$this->longitude=$row[3];
				$this->available=$row[4];
			}
			else
				return false;
		}
				
		function updateLoc(){
			$consulta = "UPDATE User SET Latitude='$this->latitude', Longitude='$this->longitude' WHERE fbId='$this->fbId';";
			return $this->insert($consulta);	
		}
		
		function updateBlock(){
			$consulta = "UPDATE User SET BlockedId='$this->blockedListId' WHERE fbId='$this->fbId';";
			return $this->insert($consulta);	
		}
		
		function updateAvailable(){
			if($this->available == "true")
				$consulta = "UPDATE User SET Available=1 WHERE fbId='$this->fbId';";
			else
				$consulta = "UPDATE User SET Available=0 WHERE fbId='$this->fbId';";
			return $this->insert($consulta);	
		}
	}
?>
