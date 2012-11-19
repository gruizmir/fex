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
		function setLatitude($latitude){
			$this->latitude = $latitude;
		}
		function setLongitude($longitude){
			$this->longitude = $longitude;
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
		
		function insertar(){
			$consulta = "INSERT INTO User(fbId, Email, Latitude, Longitude) VALUES ('$this->fbId','$this->email','$this->latitude', '$this->longitude');";
			return $this->insert($consulta);	
		}
		
		function getUser($fbId){
			require_once("includes/config.php");
			$sql="SELECT fbId, Latitude, Longitude FROM User WHERE fbId='$fbId'";
			$this->conectar();
			$resultado = $this->query($sql);
			if($this->numRows($resultado) == 1){
				$row=mysql_fetch_array($resultado, MYSQL_NUM);
				$this->fbId=$fbId;
				$this->latitude=$row[1];
				$this->longitude=$row[2];
			}
			else
				return false;
		}
		
				
		function update(){
			$consulta = "UPDATE User SET Email='$this->email', Latitude='$this->latitude', Longitude='$this->longitude' WHERE fbId='$this->fbId';";
			return $this->insert($consulta);	
			
		}
	}
	
?>
