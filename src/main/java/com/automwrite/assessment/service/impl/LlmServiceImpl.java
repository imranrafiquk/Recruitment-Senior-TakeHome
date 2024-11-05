package com.automwrite.assessment.service.impl;

import com.automwrite.assessment.service.DocumentService;
import com.automwrite.assessment.service.DocumentStyle;
import com.automwrite.assessment.service.LlmService;
import com.automwrite.assessment.utils.DocumentUtils;
import com.automwrite.assessment.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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
  private final String apiKey;
  private final DocumentService documentService;

  public LlmServiceImpl(RestTemplate restTemplate,
      @Value("${anthropic.api.key}") String apiKey, DocumentService documentService) {
    this.restTemplate = restTemplate;
    this.apiKey = apiKey;
    this.documentService = documentService;
  }

  @Override
  public DocumentStyle extractDocumentTone(XWPFDocument toneFile) {

    String plainTextToneDocument = documentService.getDocumentContentAsText(toneFile);


    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("x-api-key", apiKey);
      headers.set("anthropic-version", "2023-06-01");

      Map<String, Object> requestBody = Map.of("model", "claude-3-5-sonnet-20241022", "max_tokens",
          1024,  // Claude supports up to 8192 output tokens
          "system",
          "Extract the tone of the supplied document as one of the following categories, Casual, Formal or Grandiloquent",
          "messages", new Object[]{Map.of("role", "user", "content", plainTextToneDocument)});

      var response = restTemplate.postForObject(ANTHROPIC_API_URL,
          new HttpEntity<>(requestBody, headers), Map.class);

      if (response != null && response.containsKey("content")) {
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        if (!content.isEmpty()) {
          return DocumentStyle.getFromResponse(((String) content.get(0).get("text")));
        }
      }

      log.error("Unexpected response format: {}", response);
      return DocumentStyle.UNKNOWN;
    } catch (Exception e) {
      log.error("Error generating text", e);
      return DocumentStyle.UNKNOWN;
    }
  }


  @Override
  public String updateDocumentTone(XWPFDocument contentDocument, DocumentStyle documentStyle, String originalFilename) {
    String document = documentService.getDocumentContentAsJson(contentDocument);
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
          String responseContent = (String) content.get(0).get("text");
          documentService.updateDocumentToneAndSaveCopy(contentDocument,
              ResponseUtils.parseMappings(responseContent),
              DocumentUtils.appendToneToFileName(originalFilename, documentStyle));
        }
        return "File successfully uploaded, processing completed";

      }

      log.error("Unexpected response format: {}", response);
      return "";
    } catch (Exception e) {
      log.error("Error generating text", e);
      return "";
    }


  }

  @Override
  public CompletableFuture<DocumentStyle> extractDocumentToneAsync(XWPFDocument toneFile) {
    return CompletableFuture.supplyAsync(() -> extractDocumentTone(toneFile));
  }

  @Override
  public CompletableFuture<String> updateDocumentToneAsync(XWPFDocument document,
      DocumentStyle documentStyle, String originalFilename) {
    return CompletableFuture.supplyAsync(() -> this.updateDocumentTone(document, documentStyle, originalFilename));
  }

}