<?php
if (isset($_POST["email"]) && isset($_POST["code"])){
   $to = $_POST["email"];
   $subject = "Verification Code";
   $message = "Here is your Verification Code: ", $_POST["code"];
   mail($to, $subject, $message);
   echo "Send Successfully!";
}
?>