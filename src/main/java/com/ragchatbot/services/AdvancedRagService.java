package com.ragchatbot.services;

import com.ragchatbot.models.QuestionAnsweringAgent;
import com.ragchatbot.dtos.ChatRequest;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AdvancedRagService extends RagService {

  QuestionAnsweringAgent agent;

  @PostConstruct
  public void init() {
    agent = advancedQuestionAnsweringAgent();
  }

  public String generateAnswer(ChatRequest chatRequest) {
    if (chatRequest.isNewChatThread()) {
      agent = advancedQuestionAnsweringAgent();
    }
    return agent.answer(chatRequest.getUserMsg());
  }

  private QuestionAnsweringAgent advancedQuestionAnsweringAgent() {
    ChatLanguageModel chatModel = getChatModel();

    // Chat memory to remember previous interactions
    ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

    ContentRetriever contentRetriever = getEmbeddingStoreContentRetriever();

    PromptTemplate promptTemplate = PromptTemplate.from(
        "You are a question answering bot. You will be given a QUESTION and a set of paragraphs in the CONTENT section. You need to answer the question using the text present in the CONTENT section."
        + "If the answer is not present in the CONTENT text then reply: `I don't have answer of this question` \n"
        + "CONTENT: " + "{{contents}}" + "\n" + "QUESTION: " + "{{userMessage}}" + "\n");

    ContentInjector contentInjector = DefaultContentInjector.builder()
         .promptTemplate(promptTemplate)
        .build();

    RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .contentRetriever(contentRetriever)
        .contentInjector(contentInjector)
        .build();

    return AiServices.builder(QuestionAnsweringAgent.class)
        .chatLanguageModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .chatMemory(chatMemory)
        .build();
  }
}
