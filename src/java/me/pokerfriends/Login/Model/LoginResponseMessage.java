/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.pokerfriends.Login.Model;

/**
 *
 * @author markrieth
 */
public class LoginResponseMessage {
  private boolean success;
  private String message;

  public LoginResponseMessage(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }
}
