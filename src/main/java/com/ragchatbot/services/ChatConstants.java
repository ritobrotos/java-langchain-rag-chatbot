package com.ragchatbot.services;

public interface ChatConstants {
  String COLLECTION_NAME = "world_history_collection";
  Integer QDRANT_GRPC_PORT = 6334;
  Integer OPENAI_EMBEDDING_SIZE = 1536;
  String QDRANT_GRPC_HOST = System.getenv("QDRANT_GRPC_HOST");
  String QDRANT_API_KEY = System.getenv("QDRANT_API_KEY");
  String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
}
