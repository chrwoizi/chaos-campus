<?PHP



if (!defined( "JPHPC" )) {
	die( "THIS FILE MUST NOT BE USED IN THIS WAY!" );
}



$arraykey = array_keys( $_GET );
$arrayvalues = array_values( $_GET );
for ($i = 0; $i < count( $_GET ); $i++)
    if (isset( $jphpc[ "get" ][ $arraykey[ $i ] ] ))
        $jphpc[ "get" ][ $arraykey[ $i ] ] = array_merge( $jphpc[ "get" ][ $arraykey[ $i ] ], $arrayvalues[ $i ] );
    else $jphpc[ "get" ][ $arraykey[ $i ] ] = $arrayvalues[ $i ];


// POST Variablen in $jphpc[ "get_post" ] zur Verfügung stellen
$arraykey = array_keys( $_POST );
$arrayvalues = array_values( $_POST );
for ($i = 0; $i < count( $_POST ); $i++)
    if (isset( $jphpc[ "get" ][ $arraykey[ $i ] ] ))
        $jphpc[ "get" ][ $arraykey[ $i ] ] = array_merge( $jphpc[ "get" ][ $arraykey[ $i ] ], $arrayvalues[ $i ] );
    else $jphpc[ "get" ][ $arraykey[ $i ] ] = $arrayvalues[ $i ];


?>