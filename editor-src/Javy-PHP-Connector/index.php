<?PHP

error_reporting( E_ALL );

define( "JPHPC", TRUE );

include( "config.php" );
include( "functions.php" );
include( "class_db.php" );

$jphpc[ "db" ]->sql_open();

// TODO:
// Passwort-Abfrage für einfügen und löschen



if (isset( $jphpc[ "get" ][ "action" ] ) && ($jphpc[ "get" ][ "action" ] == "get_xml")) {

	$href_base = "http://".$_SERVER[ "SERVER_NAME" ].dirname( $_SERVER[ "SCRIPT_NAME" ] )."/";
    $tilesets = $jphpc[ "db" ]->sql_get( "SELECT * FROM jphpc_tilesets" );

    header( "Content-Type: text/xml; name=\"tilesets.xml\"" );
	echo( "ï»¿<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" );
	echo( "<!--Javy Leveleditor Tileset Configuration File-->\n" );
	echo( "<!--This file stores all mapped tilesets for the editor.-->\n" );
	echo( "<TilesetConfiguration FormatVersion=\"1.0\">" );
	echo( "  <TilesetsList Count=\"".count( $tilesets )."\">\n" );
    foreach ($tilesets as $tileset) {
        echo( "    <Tileset Index=\"".$tileset[ "tileset_id" ]."\" Href=\"".$href_base.$tileset[ "tileset_filename" ]."\" Filename=\"".$tileset[ "tileset_filename" ]."\" Description=\"".$tileset[ "tileset_desc" ]."\" Md5=\"".$tileset[ "tileset_md5" ]."\"/>\n" );
    }
    echo( "  </TilesetsList>\n" );
    echo( "</TilesetConfiguration>" );

    die();

} elseif (isset( $jphpc[ "get" ][ "action" ] ) && ($jphpc[ "get" ][ "action" ] == "add")) {

    $file_name = (!empty( $HTTP_POST_FILES[ "tileset_file" ][ "name" ] )) ? $HTTP_POST_FILES[ "tileset_file" ][ "name" ] : "";
    $file_tmpname = ($HTTP_POST_FILES[ "tileset_file" ][ "tmp_name" ] != "none") ? $HTTP_POST_FILES[ "tileset_file" ][ "tmp_name" ] : "";
    $file_type = (!empty( $HTTP_POST_FILES[ "tileset_file" ][ "type" ] )) ? $HTTP_POST_FILES[ "tileset_file" ][ "type" ] : "";

	if ($jphpc[ "cfg" ][ "add_password" ] != $jphpc[ "get" ][ "password" ]) {
		$jphpc[ "action" ][ "error" ][ "password" ] = "The password is wrong. Please enter the right one.";
	}

	if (file_exists( "tilesets/".$file_name )) {
		$jphpc[ "action" ][ "error" ][ "tileset_file" ] = "There is already a file with the filename \"$file_name\"! Please rename the file an upload it again.";
	}

	if ($file_name == "") {
		$jphpc[ "action" ][ "error" ][ "tileset_file" ] = "Please choose a file to load up.";
	}

	if ($jphpc[ "get" ][ "tileset_desc" ] == "") {
		$jphpc[ "action" ][ "error" ][ "tileset_desc" ] = "Please enter a description for your tileset.";
	}

	if (!isset( $jphpc[ "action" ][ "error" ] )) {

        $file_tmp = fopen( $file_tmpname, "r" );
        $file_tmp_content = fread( $file_tmp, filesize( $file_tmpname ) );
        fclose( $file_tmp );

        $file_local = fopen( "tilesets/".$file_name, "w" );
        fwrite( $file_local, $file_tmp_content );
        $file_md5 = md5( $file_tmp_content );
        fclose( $file_local );

        $result = $jphpc[ "db" ]->sql_get( "SELECT tileset_id FROM jphpc_tilesets ORDER BY tileset_id ASC" );
        $tileset_id = 1;
        for ($i = 0; $i < count( $result ); $i++)
            if ($tileset_id == $result[ $i ][ "tileset_id" ])
              $tileset_id++;
        	
        $jphpc[ "db" ]->sql_set( "INSERT INTO jphpc_tilesets SET
        		tileset_id=\"$tileset_id\",
                tileset_filename=\"$file_name\",
                tileset_md5=\"$file_md5\",
                tileset_desc=\"".$jphpc[ "get" ][ "tileset_desc" ]."\"" );
        $jphpc[ "action" ][ "message" ] = "Tileset successfully added.";

    } else {

		// Show the "Add tileset"-page with errors.
		$jphpc[ "get" ][ "site" ] = "add_tileset";

    }

} elseif (isset( $jphpc[ "get" ][ "action" ] ) && ($jphpc[ "get" ][ "action" ] == "delete")) {

	if (!isset( $jphpc[ "get" ][ "delete_acknowledge" ] ) || ($jphpc[ "get" ][ "delete_acknowledge" ] != "delete")) {

		$jphpc[ "get" ][ "site" ] = "delete_acknowledge";

	} else {

        if ($jphpc[ "cfg" ][ "delete_password" ] != $jphpc[ "get" ][ "password" ]) {
            $jphpc[ "action" ][ "error" ][ "password" ] = "The password is wrong. Please enter the right one.";
        }

		if (!isset( $jphpc[ "action" ][ "error" ] )) {

			$tileset = $jphpc[ "db" ]->sql_get( "SELECT * FROM jphpc_tilesets WHERE tileset_id=\"".$jphpc[ "get" ][ "tileset_id" ]."\"" );
			unlink( "tilesets/".$tileset[ 0 ][ "tileset_filename" ] );
			$jphpc[ "db" ]->sql_set( "DELETE FROM jphpc_tilesets WHERE tileset_id=\"".$jphpc[ "get" ][ "tileset_id" ]."\"" );
			$jphpc[ "action" ][ "message" ] = "Tileset successfully deleted.";

		} else {
			$jphpc[ "get" ][ "site" ] = "delete_acknowledge";
		}

	}

} elseif (isset( $jphpc[ "get" ][ "action" ] ) && ($jphpc[ "get" ][ "action" ] == "add")) {

    $file_name = (!empty( $HTTP_POST_FILES[ "tileset_file" ][ "name" ] )) ? $HTTP_POST_FILES[ "tileset_file" ][ "name" ] : "";
    $file_tmpname = ($HTTP_POST_FILES[ "tileset_file" ][ "tmp_name" ] != "none") ? $HTTP_POST_FILES[ "tileset_file" ][ "tmp_name" ] : "";
    $file_type = (!empty( $HTTP_POST_FILES[ "tileset_file" ][ "type" ] )) ? $HTTP_POST_FILES[ "tileset_file" ][ "type" ] : "";

	if (file_exists( "tilesets/".$file_name )) {
		$jphpc[ "action" ][ "error" ][ "tileset_file" ] = "There is already a file with the filename \"$file_name\"! Please rename the file an upload it again.";
	}

	if ($file_name == "") {
		$jphpc[ "action" ][ "error" ][ "tileset_file" ] = "Please choose a file to load up.";
	}

	if ($jphpc[ "get" ][ "tileset_desc" ] == "") {
		$jphpc[ "action" ][ "error" ][ "tileset_desc" ] = "Please enter a description for your tileset.";
	}

	if (!isset( $jphpc[ "action" ][ "error" ] )) {

        $file_tmp = fopen( $file_tmpname, "r" );
        $file_tmp_content = fread( $file_tmp, filesize( $file_tmpname ) );
        fclose( $file_tmp );

        $file_local = fopen( "tilesets/".$file_name, "w" );
        fwrite( $file_local, $file_tmp_content );
        $file_md5 = md5( $file_tmp_content );
        fclose( $file_local );

        $jphpc[ "db" ]->sql_set( "INSERT INTO jphpc_tilesets SET
                tileset_filename=\"".$file_name."\",
                tileset_md5=\"".$file_md5."\",
                tileset_desc=\"".$jphpc[ "get" ][ "tileset_desc" ]."\"" );

    } else {

		// Show the "Add tileset"-page with errors.
		$jphpc[ "get" ][ "site" ] = "add_tileset";

    }
}



echo( "<html>
    <body>" );



if (!isset( $jphpc[ "get" ][ "site" ] ) || ($jphpc[ "get" ][ "site" ] == "")) {

	$message = (isset( $jphpc[ "action" ][ "message" ] )) ? $jphpc[ "action" ][ "message" ] : "";

	echo( "
		<div style=\"font-weight: bold; font-color: #0f0;\">$message</div>
		<a href=\"?action=get_xml\">Getting the XML-File</a> <br />
		<a href=\"?site=add_tileset\">Adding a Tileset</a> <br />
		<a href=\"?site=delete_tileset\">Deleting a Tileset</a> <br />" );

} elseif (isset( $jphpc[ "get" ][ "site" ] ) && ($jphpc[ "get" ][ "site" ] == "add_tileset")) {

	$old_tileset_file = (isset( $jphpc[ "get" ][ "tileset_file" ] ))? $jphpc[ "get" ][ "tileset_file" ] : "";
	$old_tileset_desc = (isset( $jphpc[ "get" ][ "tileset_desc" ] ))? $jphpc[ "get" ][ "tileset_desc" ] : "";

	$error_tileset_file = (isset( $jphpc[ "action" ][ "error" ][ "tileset_file" ] ))? $jphpc[ "action" ][ "error" ][ "tileset_file" ] : "";
	$error_tileset_desc = (isset( $jphpc[ "action" ][ "error" ][ "tileset_desc" ] ))? $jphpc[ "action" ][ "error" ][ "tileset_desc" ] : "";
	$error_password = (isset( $jphpc[ "action" ][ "error" ][ "password" ] )) ? $jphpc[ "action" ][ "error" ][ "password" ] : "";

	echo( "
		<form action=\"?action=add\" method=\"post\" enctype=\"multipart/form-data\" accept=\"*.exe\">
			File: <input type=\"file\" name=\"tileset_file\" value=\"$old_tileset_file\" />
				<div style=\"font-color: #f00; font-weight: bold\">$error_tileset_file</div> <br />
			Description: <input type=\"text\" name=\"tileset_desc\" value=\"$old_tileset_desc\" />
				<div style=\"font-color: #f00; font-weight: bold\">$error_tileset_desc</div> <br />
			Password: <input type=\"password\" name=\"password\" />
				<div style=\"font-color: #f00; font-weight: bold\">$error_password</div> <br />
			<input type=\"submit\" />
		</form>" );

} elseif (isset( $jphpc[ "get" ][ "site" ] ) && ($jphpc[ "get" ][ "site" ] == "delete_tileset")) {

	echo( "
		<table>
			<tr>
				<th>ID</th>
				<th>Filename</th>
				<th>Descritption</th>
				<th>MD5</th>
			</tr>" );
    $tilesets = $jphpc[ "db" ]->sql_get( "SELECT * FROM jphpc_tilesets" );
    foreach ($tilesets as $tileset) {
        echo( "<tr>
    		<td>".$tileset[ "tileset_id" ]."</td>
    		<td><a href=\"?action=delete&amp;tileset_id=".$tileset[ "tileset_id" ]."&amp;site=none\">".$tileset[ "tileset_filename" ]."</a></td>
    		<td>".$tileset[ "tileset_desc" ]."</td>
    		<td>".$tileset[ "tileset_md5" ]."</td>
    	</tr>" );
    }
	echo( "</table>" );

} elseif (isset( $jphpc[ "get" ][ "site" ] ) && ($jphpc[ "get" ][ "site" ] == "delete_acknowledge")) {

	$error_password = (isset( $jphpc[ "action" ][ "error" ][ "password" ] )) ? $jphpc[ "action" ][ "error" ][ "password" ] : "";
    echo( "
        Do you really want to delete the following tilset? <br />
        <form action=\"?site=\">
            <input type=\"submit\" value=\"No\" />
        </form>
        <form action=\"?action=delete&amp;tileset_id=".$jphpc[ "get" ][ "tileset_id" ]."&amp;delete_acknowledge=delete\">
            <input type=\"hidden\" name=\"action\" value=\"delete\" />
            <input type=\"hidden\" name=\"tileset_id\" value=\"".$jphpc[ "get" ][ "tileset_id" ]."\" />
            <input type=\"hidden\" name=\"delete_acknowledge\" value=\"delete\" />
            Password: <input type=\"password\" name=\"password\" />
				<div style=\"font-color: #f00; font-weight: bold\">$error_password</div> <br />
            <input type=\"submit\" value=\"Yes\" />
        </form>" );

} elseif (isset( $jphpc[ "get" ][ "site" ] ) && ($jphpc[ "get" ][ "site" ] == "none")) {

	echo( "" );

}



$jphpc[ "db" ]->sql_close();



echo( "
	</body>
</html>" );



?>