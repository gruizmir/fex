<?php
	require_once("BaseDatos.class.php");
	
	class Message extends BaseDatos
	{
		function Message(){ }
		
		function setId($id){
			$this->id = $id;
		}
		function setSender($sender){
			$this->sender=$sender;
		}
		function setReceiver($receiver){
			$this->receiver = $receiver;
		}
		function setMessage($message){
			$this->message = $message;
		}
		function setFecha($fecha){
			$this->fecha = $fecha;
		}
		
		function getId(){
			return $this->id;
		}
		function getSender(){
			return $this->sender;
		}
		function getReceiver(){
			return $this->receiver;
		}
		function getMessage(){
			return $this->message;
		}
		function getFecha(){
			return $this->fecha;
		}
		
		function insertar(){
			$consulta = "INSERT INTO Message(sender, receiver, msg, fecha) VALUES ('$this->sender','$this->receiver','$this->message', '$this->fecha');";
			return $this->insert($consulta);	
		}
		
		function getMessages($receiver, $id){
			require_once("includes/config.php");
			$sql="SELECT msg, fecha FROM Message WHERE id>'$id' AND receiver='$receiver'";
			$this->conectar();
			$resultado = $this->query($sql);
			if($this->numRows($resultado) >= 1){
				return $resultado;
			}
			else
				return false;
		}
		
	}
?>
