/* 
 * @author Mark Rieth
 */

/* These are the varaibles associated with a user's identity */
var username = null;
var pfSessionId = null;

/* Checks that the user is logged in, and displays login form if not */
function checkLogin() {
  var isLoggedIn = false;
  pfSessionId = readCookie("PFSessionId"); 
  if (pfSessionId !== null) {
    isLoggedIn = true;
  } else {
    /* The PFSessionId Cookie was never created, so the user is not logged in */
    $("#lobby-content").hide();
    $("#login-dialog").dialog();
  }
    return isLoggedIn;
}

$(document).ready(function(){
  if (true === checkLogin()) {
    $("#lobby-content").show();
    $("#login-dialog").hide();
  }
  /*
   * The submit-login button sends the login form data to the servlet for 
   * validation, and updates the error message. 
   */
  $("#login-submit").on("click",function(){
    var response = null;
    username = $("#username").val();
    password = $.md5($("#password").val());
    if ($("input[name='register-checkbox']").is(":checked") === false) {
      var serializedLoginString = "login-username=" + username
        + "&login-password=" + password + "&is-registered=true";
      $.get("login-servlet?" + serializedLoginString , (data, status) => {
        console.log(data);
        response = JSON.parse(data);
        if (response.success === true) {
          window.location = "index.html"
        }
        $(".login-error").text(response.message);
      });
    } else {
      /* The register-checkbox is checked */
      var email = $("#email").val();
      var serializedRegisterString = "register-username=" + username + "&register-password="
        + password + "&email=" + email + "&is-registered=false";
      $.get("login-servlet?" + serializedRegisterString, (data,status) => {
        response = JSON.parse(data);
        if (response.success === true) {
          window.location = "index.html";
        }
        $(".login-error").text(response.message);
      });
    } 
  });

  $("#register-checkbox").change(function() {
    $(".toggle-email").toggle();  
  });
});