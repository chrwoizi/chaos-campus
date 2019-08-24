<?PHP



if (!defined( "JPHPC" )) {
	die( "THIS FILE MUST NOT BE USED IN THIS WAY!" );
}



class CLASS_DB {

    var $db;

    function sql_open() {
        global $jphpc;

        // DEBUG
        $this->db = @mysql_connect(
        		$jphpc[ "cfg" ][ "server" ],
        		$jphpc[ "cfg" ][ "user" ],
        		$jphpc[ "cfg" ][ "password" ] ) or die( "Connecting to server was not successful!" );
        $db_select = @mysql_select_db( $jphpc[ "cfg" ][ "database" ] ) or die( "Choosing the database was not successful!" );

        return $this->create_tables();
    }


    function create_tables() {

        return $this->sql_set( "CREATE TABLE IF NOT EXISTS jphpc_tilesets (
                                                            tileset_id        bigint(20) unsigned NOT NULL,
                                                            tileset_filename  text NOT NULL default '',
                                                            tileset_md5       text NOT NULL default '',
                                                            tileset_desc      text NOT NULL default '',
                                                            PRIMARY KEY (tileset_id)
                                                        ) ENGINE=MyISAM DEFAULT CHARACTER SET latin1 COLLATE latin1_german2_ci;" );
	}


//    public function sql_get( $query ) { // PHP5
    function sql_get( $query ) {

        $result = mysql_query( $query );
        $mysql_error = mysql_errno();
        if ($mysql_error > 1000)
            die( "<pre>Fehler: " . mysql_error() . "\n" . $query );
        $content = array();
        if (mysql_num_rows( $result ) > 0)
            while ($result != "") {
                $temp = mysql_fetch_array( $result, MYSQL_ASSOC );
                if($temp != null)
                    $content[] = $temp;
                else break;
            }

        if (isset( $content ))
            return $content;
    }


//    public function sql_set( $query ) { // PHP5
    function sql_set( $query ) {

        mysql_query( $query );

        return mysql_affected_rows( $this->db );
    }


//    public function sql_get_array( $query ) { PHP5
    function sql_get_array( $query ) {

        $result = mysql_query( $query );
        if (mysql_num_rows( $result ) > 0)
            while ($result != "") {
                $temp = mysql_fetch_row( $result );
                if($temp != null)
                    $content[] = $temp;
                else break;
            }
        if (isset( $content ))
            return $content;
    }


//    public function sql_close() { PHP5
    function sql_close() {
        $db_close = @mysql_close( $this->db ) or die( "Die Verbindung zur Datenbank konnte nicht geschlossen werden!" );
    }
}



$jphpc[ "db" ] = new CLASS_DB();



?>