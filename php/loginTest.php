<?PHP
$username = "root";
$password = "";
$url = "localhost";
$database = "myteam";
$connection =  mysqli_connect($url, $username, $password, $database);
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
				echo "login success";
			} else {
				echo "email & password not matching, try again.";
			}
		} else {
			echo "User [".$email."] does not exist! Try to register?";
		}
	}
}
?>
