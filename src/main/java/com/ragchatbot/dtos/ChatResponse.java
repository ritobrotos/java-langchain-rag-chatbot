package com.ragchatbot.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponse {
  private String aiMsg;
}
