package com.ragchatbot.services;

import com.ragchatbot.models.QuestionAnsweringAgent;
import com.ragchatbot.dtos.ChatRequest;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class BasicRagService extends RagService {
  QuestionAnsweringAgent agent;

  @PostConstruct
  public void init() {
    agent = basicQuestionAnsweringAgent();
  }

  /**
   * The answer method of QuestionAnsweringAgent is called which internally calls the invoke method in DefaultAiServices class.
   */
  public String generateAnswer(ChatRequest chatRequest) {
    if (chatRequest.isNewChatThread()) {
      agent = basicQuestionAnsweringAgent();
    }
    return agent.answer(chatRequest.getUserMsg());
  }

  private QuestionAnsweringAgent basicQuestionAnsweringAgent() {
    // Code Ref: https://github.com/langchain4j/langchain4j-examples/blob/main/rag-examples/src/main/java/_01_Naive_RAG.java
    ChatLanguageModel chatModel = getChatModel();

    // Chat memory to remember previous interactions
    ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

    // The content retriever is responsible for retrieving relevant content
    // from Vector DB based on a user query.
    ContentRetriever contentRetriever = getEmbeddingStoreContentRetriever();

    return AiServices.builder(QuestionAnsweringAgent.class)
        .chatLanguageModel(chatModel)
        .contentRetriever(contentRetriever)
        .chatMemory(chatMemory)
        .build();
  }
}
