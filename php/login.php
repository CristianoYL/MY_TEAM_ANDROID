<?PHP
$email = "test@gmail.com";
$password = "123456789";
if ( !isSet($_POST['dbHost']) || !isSet($_POST['dbName'])
	|| !isSet($_POST['dbUsername']) || !isSet($_POST['dbPassword']) ) {
	echo "connection credentials not set!";
} else {
	$DB_HOST = $_POST['dbHost'];
	$DB_USER = $_POST['dbUsername'];
	$DB_PASSWORD = $_POST['dbPassword'];
	$DB_NAME = $_POST['dbName'];
	$connection = mysqli_connect($DB_HOST, $DB_USER, $DB_PASSWORD, $DB_NAME);	// connect to DB
	if ( mysqli_connect_errno($connection) ) {	// connection failed
		echo "unable to connect to Database: " . mysqli_connect_error();
	} else {	// connection succeeded
		if( !isset($_POST['email']) || !isset($_POST['password']) ) {
			echo "account credential incomplete!";
		} else {
			$email = $_POST['email'];
			$password = $_POST['password'];
			$query = "SELECT * FROM USER WHERE `email` = '".$email."'";	// check if user exists first
			$result = mysqli_query($connection,$query);
			if($result->num_rows > 0){
				$query = "SELECT * FROM USER WHERE `email` = '".$email."' and `password` = '".$password."'";	// check if credentials match
				$result = mysqli_query($connection,$query);
				if($result->num_rows == 1){
					echo "Login success";
				} else {
					echo "email & password not matching, try again.";
				}
			} else {
				echo "User [".$email."] does not exists! Try to register?";
			}
		}
	}
}
?>
