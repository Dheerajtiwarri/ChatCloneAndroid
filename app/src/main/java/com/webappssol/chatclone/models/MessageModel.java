package com.webappssol.chatclone.models;

import java.util.ArrayList;

/**
 * Created by Dheeraj on 09,December,2019
 * Webapps Solution Pvt. Ltd.,
 * Kolkata, India.
 */
public class MessageModel {
  String messageId,
      senderId,
      message;

  ArrayList<String> urlList;

  public MessageModel(String messageId, String senderId, String message, ArrayList<String> urlList) {
    this.messageId = messageId;
    this.senderId = senderId;
    this.message = message;
    this.urlList = urlList;
  }

  public ArrayList<String> getUrlList() {
    return urlList;
  }

  public String getMessageId() {
    return messageId;
  }

  public String getSenderId() {
    return senderId;
  }

  public String getMessage() {
    return message;
  }
}
