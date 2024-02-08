package com.ragchatbot.dtos;

import lombok.Data;

@Data
public class ChatRequest {
  private String userMsg;
  private boolean newChatThread;
}
