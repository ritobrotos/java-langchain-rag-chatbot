package com.ragchatbot.controllers;

import com.ragchatbot.services.ChatService;
import com.ragchatbot.dtos.ChatRequest;
import com.ragchatbot.dtos.ChatResponse;
import com.ragchatbot.services.DataIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

  @Autowired
  ChatService chatService;

  @Autowired
  DataIngestionService dataIngestionService;

  @PostMapping
  @CrossOrigin(origins = "http://localhost:3000")
  public ChatResponse processMsg(@RequestBody ChatRequest chatRequest) {
    System.out.println(chatRequest.getUserMsg());
    var aiMessage = chatService.rag(chatRequest);
    var response = ChatResponse.builder().aiMsg(aiMessage).build();
    return response;
  }

  @GetMapping
  public String welcome() {
    return "Welcome to Chat Bot";
  }

  @PostMapping("/setup")
  public void processMsg() {
    dataIngestionService.setupRagChatbot();
  }
}
