package com.automwrite.assessment.service.impl;

import com.automwrite.assessment.service.DocumentStyle;
import com.automwrite.assessment.service.LlmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class LlmServiceImpl implements LlmService {

  private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final String apiKey;

  public LlmServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper,
      @Value("${anthropic.api.key}") String apiKey) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.apiKey = apiKey;
  }

  @Override
  public String generateText(String prompt) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("x-api-key", apiKey);
      headers.set("anthropic-version", "2023-06-01");

      Map<String, Object> requestBody = Map.of("model", "claude-3-5-sonnet-20241022", "max_tokens",
          1024,  // Claude supports up to 8192 output tokens
          "messages", new Object[]{Map.of("role", "user", "content", prompt)});

      var response = restTemplate.postForObject(ANTHROPIC_API_URL,
          new HttpEntity<>(requestBody, headers), Map.class);

      if (response != null && response.containsKey("content")) {
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        if (!content.isEmpty()) {
          return (String) content.get(0).get("text");
        }
      }

      log.error("Unexpected response format: {}", response);
      return "";
    } catch (Exception e) {
      log.error("Error generating text", e);
      return "";
    }
  }

  @Override
  public String extractDocumentTone(String document) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("x-api-key", apiKey);
      headers.set("anthropic-version", "2023-06-01");

      Map<String, Object> requestBody = Map.of("model", "claude-3-5-sonnet-20241022", "max_tokens",
          1024,  // Claude supports up to 8192 output tokens
          "system",
          "Extract the tone of the supplied document as one of the following categories, Casual, Formal or Grandiloquent",
          "messages", new Object[]{Map.of("role", "user", "content", document)});

      var response = restTemplate.postForObject(ANTHROPIC_API_URL,
          new HttpEntity<>(requestBody, headers), Map.class);

      if (response != null && response.containsKey("content")) {
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        if (!content.isEmpty()) {
          return (String) content.get(0).get("text");
        }
      }

      log.error("Unexpected response format: {}", response);
      return "";
    } catch (Exception e) {
      log.error("Error generating text", e);
      return "";
    }
  }


  @Override
  public String getUpdatedTone(String document, DocumentStyle documentStyle) {

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("x-api-key", apiKey);
      headers.set("anthropic-version", "2023-06-01");

      Map<String, Object> requestBody = Map.of("model", "claude-3-5-sonnet-20241022", "max_tokens",
          1024,  // Claude supports up to 8192 output tokens
          "system", "Rewrite the paragraphs and table cells in the following in a : "
              + documentStyle.toString()
              + " style. I want the output to show the mapping from old to new so I can replace text in the existing document.",
          "messages", new Object[]{Map.of("role", "user", "content", document)});

      var response = restTemplate.postForObject(ANTHROPIC_API_URL,
          new HttpEntity<>(requestBody, headers), Map.class);

      if (response != null && response.containsKey("content")) {
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        if (!content.isEmpty()) {
          return (String) content.get(0).get("text");
        }
      }

      log.error("Unexpected response format: {}", response);
      return "";
    } catch (Exception e) {
      log.error("Error generating text", e);
      return "";
    }

  }

  @Override
  public CompletableFuture<String> generateTextAsync(String prompt) {
    return CompletableFuture.supplyAsync(() -> generateText(prompt));
  }

}