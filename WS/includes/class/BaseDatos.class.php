<?php
require_once("../config.php");
class BaseDatos {
        function BaseDatos() {  }

        function conectar() {
                #envia la conexion a la BD. Variables globales se ven en toda la clase.
                global $strings,$conexion,$databaseType,$db;
                $conexion= mysql_connect(MYSERVER, MYLOGIN, MYPASSWORD) or die($strings["error_server"]);
                $db = mysql_select_db(MYDATABASE);
        }

        #recibe la consulta que se va ingresar a la BD.

        function query($sql) {
                global $conexion, $result;
                $this->sql = $sql;
                $this->result = mysql_query($sql,$conexion);
                return $this->result;
        }

        #Crea el arreglo con los datos.
        function fetch() {
				global $columna;
                if (!$this->result) {
                        echo "Un Error ha ocurrido.\n";
                        echo "<font color=red><b>".pg_result_error($columna)."</b></font><br><br><br>";
                        echo "<font color=red><b>La consulta es: \"".$this->sql."\"</b></font><br>";
                        exit;
                }
                $this->columna=mysql_fetch_row($this->result,MYSQL_NUM);
                return $this->columna;
        }

        #libera la memo del arreglo result, y cierra la conexion.
		function close() {
                global $conexion;
                @mysql_free_result($this->result);
                @mysql_close($conexion);
        }

        #Sirve para insertar y para borrar-     
        function insert($consulta){
                global $conexion, $columna, $sql;
                $this->conectar();
                $sql = $consulta;
                $result = $this->query($sql);
                $this->close();
                if ($result){
                        return true;
                }else{
                        global $msg; 
                        $msg = $sql;
                        return false;
                }
		}

        function update($sqlquery){
                global $conexion, $columna;
                $this->conectar();
                global $sql;
                $sql = $sqlquery;
                $index = $this->query($sql);
                $this->close();
                if($this->result){
                        return true;
                }else{
                        return false;
                }
        }       

        function delete($sqlquery){
                return $this->insertbd($sqlquery);
        }

        function numRows($sqlquery){         
        		global $conexion;
                $count = mysql_num_rows($sqlquery);
                return $count;
        }
}

?>

