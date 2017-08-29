/**
 *
 * @author Mark Rieth
 */
package me.pokerfriends.Login.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.pokerfriends.Database.DbManager;
import com.google.gson.Gson;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import me.pokerfriends.Login.Model.LoginResponseMessage;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login-servlet"})
public class LoginServlet extends HttpServlet {
  private final DbManager dbManager = new DbManager();
  private final Gson gson = new Gson();
  private static final Logger LOGGER = Logger.getLogger(DbManager.class.getName());
  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
   * methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    String username = request.getParameter("login-username");
    String password = request.getParameter("login-password");
    String isRegistered = request.getParameter("is-registered");
    String responseStr = null;
    PrintWriter out = response.getWriter();
    LoginResponseMessage responseMsg = null;
    
    if (("true").equalsIgnoreCase(isRegistered)) {
      if (dbManager.isValidCredentials(username, password)) {
        responseMsg = new LoginResponseMessage(true, "Success. Logged in!");
      } else {
        responseMsg = new LoginResponseMessage(false, "Invalid username/password combo");
      }
    } else {
      //The user is registering
      String email = request.getParameter("email");
      if (username.length() > dbManager.USERNAME_MAX_LEN) {
        responseMsg = new LoginResponseMessage(false, "Max username length is 16");
      }
      int updateStatus = dbManager.registerUser(username, password, email);
      if (updateStatus == 0) {
        responseMsg = new LoginResponseMessage(false, "Failed to update DB, status: " + updateStatus);
      } else {
        responseMsg = new LoginResponseMessage(true, "Success. Registered!");
      }
    }
    if (responseMsg != null && responseMsg.isSuccess()) {
      String pfSessionId = UUID.randomUUID().toString();
      response.addCookie(new Cookie("PFSessionId", pfSessionId));
      dbManager.addPfSessionId(username, pfSessionId);
    }
    responseStr = gson.toJson(responseMsg);
    out.print(responseStr);
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>

}
