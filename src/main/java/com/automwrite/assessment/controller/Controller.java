package com.automwrite.assessment.controller;

import com.automwrite.assessment.service.LlmService;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class Controller {

  private final LlmService llmService;

  /**
   * You should extract the tone from the `toneFile` and update the `contentFile` to convey the same
   * content but using the extracted tone.
   *
   * @param toneFile    File to extract the tone from
   * @param contentFile File to apply the tone to
   * @return A response indicating that the processing has completed
   */
  @PostMapping("/transfer-style")
  public CompletableFuture<ResponseEntity<String>> test(@RequestParam MultipartFile toneFile,
      @RequestParam MultipartFile contentFile) throws IOException {

    XWPFDocument toneDocument = new XWPFDocument(toneFile.getInputStream());
    XWPFDocument contentDocument = new XWPFDocument(contentFile.getInputStream());

    return llmService.extractDocumentToneAsync(toneDocument).thenCompose(
        toneResponse -> llmService.updateDocumentToneAsync(contentDocument, toneResponse,
            contentFile.getOriginalFilename())).thenApply(ResponseEntity::ok).exceptionally(ex -> {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error processing document: " + ex.getMessage());
    });


  }
}
