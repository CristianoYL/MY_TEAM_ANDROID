<?PHP
include_once("connection.php");
$email = "test@gmail.com2";
$password = "123456789";
$query = "INSERT INTO USER VALUES ('".$email."','".$password."')";
$result = mysqli_query($conn,$query);
if($result == 1){
	echo "Register succeeded!";
} else {
	echo 'Register failed! User ['.$email.'] already exists!';
}?>
