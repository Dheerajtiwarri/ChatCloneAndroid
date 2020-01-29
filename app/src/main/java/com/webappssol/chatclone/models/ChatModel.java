package com.webappssol.chatclone.models;

/**
 * Created by Dheeraj on 09,December,2019
 * Webapps Solution Pvt. Ltd.,
 * Kolkata, India.
 */
public class ChatModel {
  private String chatId;

  public ChatModel(String chatId) {
    this.chatId = chatId;
  }

  public String getChatId() {
    return chatId;
  }
}
