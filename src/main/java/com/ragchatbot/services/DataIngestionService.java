package com.ragchatbot.services;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.val;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class DataIngestionService {

  public void setupRagChatbot() {
    insertDocuments();
  }

  private QdrantClient getQdrantClient() {
    // Authentication Ref: https://qdrant.tech/documentation/cloud/quickstart-cloud/
    return new QdrantClient(
        QdrantGrpcClient.newBuilder(
                ChatConstants.QDRANT_GRPC_HOST,
                ChatConstants.QDRANT_GRPC_PORT,
                true)
            .withApiKey(ChatConstants.QDRANT_API_KEY)
            .build());
  }

  private void createCollection() {
    val client = getQdrantClient();
    try {
      client.createCollectionAsync(ChatConstants.COLLECTION_NAME,
          VectorParams.newBuilder().setDistance(Distance.Dot).setSize(
              ChatConstants.OPENAI_EMBEDDING_SIZE).build()).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private void insertDocuments() {
    EmbeddingModel embeddingModel = getEmbeddingModel();
    DocumentSplitter documentSplitter = DocumentSplitters.recursive(1000, 150);
    String fileContent = getFileContent();
    Document doc = Document.from(fileContent, Metadata.from("document-type", "history-document"));

    EmbeddingStore<TextSegment> embeddingStore = getEmbeddingStore();
    List<TextSegment> segments = documentSplitter.split(doc);
    Response<List<Embedding>> embeddingResponse = embeddingModel.embedAll(segments);
    List<Embedding> embeddings = embeddingResponse.content();
    embeddingStore.addAll(embeddings, segments);
  }

  private EmbeddingModel getEmbeddingModel() {
    String openaiApiKey = System.getenv("OPENAI_API_KEY");
    return OpenAiEmbeddingModel.withApiKey(openaiApiKey);
  }

  private static EmbeddingStore<TextSegment> getEmbeddingStore() {
    // Ref: https://qdrant.tech/documentation/frameworks/langchain4j/
    return QdrantEmbeddingStore.builder()
        .collectionName(ChatConstants.COLLECTION_NAME)
        .host(ChatConstants.QDRANT_GRPC_HOST)
        .port(ChatConstants.QDRANT_GRPC_PORT)
        .apiKey(ChatConstants.QDRANT_API_KEY)
        .useTls(true)
        .build();
  }

  /**
   * Read the data from the file
   */
  private String getFileContent() {
    Resource companyDataResource = new ClassPathResource("data/data.txt");
    try {
      File file = companyDataResource.getFile();
      String content = new String(Files.readAllBytes(file.toPath()));
      return content;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
 }
