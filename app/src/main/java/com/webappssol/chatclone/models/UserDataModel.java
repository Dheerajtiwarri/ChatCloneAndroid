package com.webappssol.chatclone.models;

/**
 * Created by Dheeraj on 05,December,2019
 * Webapps Solution Pvt. Ltd.,
 * Kolkata, India.
 */
public class UserDataModel {
  private String uId,name,
      phoneNo;

  public UserDataModel(String uId,String name, String phoneNo) {
    this.uId = uId;
    this.name = name;
    this.phoneNo = phoneNo;
  }

  public String getuId() {
    return uId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoneNo() {
    return phoneNo;
  }
}
