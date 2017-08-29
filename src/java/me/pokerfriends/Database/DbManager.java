/**
 *
 * @author Mark Rieth
 */
package me.pokerfriends.Database;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;


public class DbManager {
  
  private FileReader fileReader;
  public static final int USERNAME_MAX_LEN = 16;
  private static final Logger LOGGER = Logger.getLogger(DbManager.class.getName());
  private static String USERNAME;
  private static String PASSWORD;
  private static String HOST;
  private static String DB;
  private static String PORT;
  private static String DB_NAME;
  private static String DB_URL;
  private Connection connection;
  private DatabaseMetaData dbMetaData;
  private PreparedStatement statement;
  
  public DbManager() {
    try {
      //Reads and sets the credentials
      String dbInfoStr = new String(Files.readAllBytes(Paths.get("/var/www/dbinfo.txt")));
      String[] dbinfo = dbInfoStr.split("\n");
      USERNAME = dbinfo[0];
      PASSWORD = dbinfo[1];
      HOST = dbinfo[2];
      DB = dbinfo[3];
      PORT = dbinfo[4];
      DB_NAME = dbinfo[5];
      DB_URL = "jdbc:" + DB + "://" + HOST + ":" + PORT + "/" + DB_NAME;
      //Ensures that the class exists
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
      dbMetaData = connection.getMetaData();
    } catch (ClassNotFoundException | SQLException | IOException e) {
      LOGGER.log(Level.SEVERE, e.toString(), e);
    }
  }
  
  public String getUsernameBySessionId(String pfSessionId) {
    String username = null;
    //Grabs the username associated with the current PFSessionId from the database
    try {
      statement = connection.prepareStatement("SELECT user.username FROM user "
        + "JOIN session ON session.user_id = user.id "
        + "WHERE session.session_id = ?;"); 
      statement.setString(1, pfSessionId);
      ResultSet resultSet = statement.executeQuery();
      username = resultSet.getString("username");
      resultSet.close();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.toString(), e);
    } finally {
      return username;
    }
  }
  
  public boolean isValidCredentials(String username, String password) {
    boolean success = false;
    try {
      statement = connection.prepareStatement("SELECT user.username, user.id"
          + " FROM user WHERE user.username = ? and user.password = ?;");
      statement.setString(1, username);
      statement.setString(2, password);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) { 
        //Non-empty resultset means the username/password combo exists
        success = true;
      } 
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.toString(), e);
    } finally {
      return success;
    }
  }
  
  public int registerUser(String username, String password, String email) {
    int updateStatus = 0; 
    try {
      statement = connection.prepareStatement("INSERT INTO user (username, password, email)"
          + " VALUES (?, ?, ?);");
      statement.setString(1, username);
      statement.setString(2, password);
      statement.setString(3, email);
      updateStatus = statement.executeUpdate();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.toString(), e);
    } finally {
      return updateStatus;
    }
  }

  public int addPfSessionId(String username, String pfSessionId) {
    int updateStatus = 0;
    int userId;
    try {
      //Gets the user id associated with the username
      statement = connection.prepareStatement("SELECT user.id"
          + " FROM user WHERE user.username = ?;");
      statement.setString(1, username);
      ResultSet result = statement.executeQuery();
      if (result.next()) {
        userId = result.getInt("id");
        //Maps the user id to the pfSessionId
        statement = connection.prepareStatement("INSERT INTO session (user_id, session_id) "
            + "VALUES (?, ?);");
        statement.setInt(1, userId);
        statement.setString(2, pfSessionId);
        updateStatus = statement.executeUpdate();
      } 
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.toString(), e);
    } finally {
      return updateStatus;
    }
  }
}
