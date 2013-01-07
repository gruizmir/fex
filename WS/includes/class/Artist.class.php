<?php
	require_once("BaseDatos.class.php");
	
	class Artist extends BaseDatos
	{
		function Artist(){ }
		
		function setArtista($artista){
			$this->artista=$artista;
		}
		function setVideo1($video1){
			$this->video1=$video1;
		}
		function setVideo2($video2){
			$this->video2=$video2;
		}
		function setVideo3($video3){
			$this->video3=$video3;
		}
		function setVideoName1($videoName1){
			$this->videoName1=$videoName1;
		}
		function setVideoName2($videoName2){
			$this->videoName2=$videoName2;
		}
		function setVideoName3($videoName3){
			$this->videoName3=$videoName3;
		}
		function setFestival($festival){
			$this->festival = $festival;
		}		
		function getId(){
			return $this->id;
		}				
		function getArtista(){
			return $this->artista;
		}
		function getVideo1(){
			return $this->video1;
		}
		function getVideo2(){
			return $this->video2;
		}
		function getVideo3(){
			return $this->video3;
		}
		function getVideoName1(){
			return $this->videoName1;
		}
		function getVideoName2(){
			return $this->videoName2;
		}
		function getVideoName3(){
			return $this->videoName3;
		}
		function getFestival(){
			return $this->festival;
		}		
		function insertar(){
			$consulta = "INSERT INTO Artist(artist, video1, video2, video3, videoname1, videoname2, videoname3, festival) VALUES ('$this->artista','$this->video1','$this->video2','$this->video3', '$this->videoName1', '$this->videoName2', '$this->videoName3', '$this->festival');";
			return $this->insert($consulta);	
		}
		
		function getArtist($id){
			require_once("includes/config.php");
			$sql="SELECT id, artist, video1, videoname1, video2, videoname2, video3, videoname3, festival FROM Artist WHERE id='$id'";
			$this->conectar();
			$resultado = $this->query($sql);
			if($this->numRows($resultado) == 1){
				$row=mysql_fetch_array($resultado, MYSQL_NUM);
				$this->id=$id;
				$this->artist=$row[1];
				$this->video1=$row[2];
				$this->videoName1=$row[3];
				$this->video2=$row[4];
				$this->videoName2=$row[5];
				$this->video3=$row[6];
				$this->videoName3=$row[7];
				$this->festival=$row[8];
			}
			else
				return false;
		}
		
		function getIds($festival){
			require_once("includes/config.php");
			$sql="SELECT id, artist FROM Artist WHERE festival='$festival' ORDER BY artist";
			$this->conectar();
			return $this->query($sql);
		}
	}
?>
