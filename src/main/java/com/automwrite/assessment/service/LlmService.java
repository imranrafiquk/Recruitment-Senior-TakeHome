package com.automwrite.assessment.service;

import java.util.concurrent.CompletableFuture;

public interface LlmService {

    String generateText(String prompt);

    String extractDocumentTone(String document);

    String getUpdatedTone(String  document, DocumentStyle documentStyle);

    CompletableFuture<String> generateTextAsync(String prompt);
}
