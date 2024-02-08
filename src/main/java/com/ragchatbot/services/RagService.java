package com.ragchatbot.services;

import static com.ragchatbot.services.ChatConstants.COLLECTION_NAME;
import static com.ragchatbot.services.ChatConstants.OPENAI_API_KEY;
import static com.ragchatbot.services.ChatConstants.QDRANT_API_KEY;
import static com.ragchatbot.services.ChatConstants.QDRANT_GRPC_HOST;
import static com.ragchatbot.services.ChatConstants.QDRANT_GRPC_PORT;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_3_5_TURBO;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;

public class RagService {
  protected ContentRetriever getEmbeddingStoreContentRetriever() {
    EmbeddingStore<TextSegment> embeddingStore =
        QdrantEmbeddingStore.builder()
            .collectionName(COLLECTION_NAME)
            .host(QDRANT_GRPC_HOST)
            .port(QDRANT_GRPC_PORT)
            .apiKey(QDRANT_API_KEY)
            .useTls(true)
            .build();

    EmbeddingModel embeddingModel = OpenAiEmbeddingModel.withApiKey(OPENAI_API_KEY);

    return EmbeddingStoreContentRetriever.builder()
        .embeddingStore(embeddingStore)
        .embeddingModel(embeddingModel)
        .maxResults(2)
        .minScore(0.6)
        .build();
  }

  protected ChatLanguageModel getChatModel() {
    return OpenAiChatModel.builder()
        .apiKey(OPENAI_API_KEY)
        .modelName(GPT_3_5_TURBO)
        .build();
  }
}
