package com.automwrite.assessment.controller;

import com.automwrite.assessment.service.DocumentService;
import com.automwrite.assessment.service.DocumentStyle;
import com.automwrite.assessment.service.LlmService;
import com.automwrite.assessment.utils.DocumentUtils;
import com.automwrite.assessment.utils.ResponseUtils;
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
  private final DocumentService documentService;

  /**
   * You should extract the tone from the `toneFile` and update the `contentFile` to convey the same
   * content but using the extracted tone.
   *
   * @param toneFile    File to extract the tone from
   * @param contentFile File to apply the tone to
   * @return A response indicating that the processing has completed
   */
  @PostMapping("/transfer-style")
  public ResponseEntity<String> test(@RequestParam MultipartFile toneFile,
      @RequestParam MultipartFile contentFile) throws IOException {

    XWPFDocument contentDocument = new XWPFDocument(contentFile.getInputStream());

    String plainTextToneDocument = documentService.getDocumentContentAsText(
        new XWPFDocument(toneFile.getInputStream()));
    String response = llmService.extractDocumentTone(plainTextToneDocument);


    DocumentStyle documentStyle = DocumentStyle.getFromResponse(response);
    if (documentStyle.equals(DocumentStyle.UNKNOWN)) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT)
          .body("Cannot determine style from contentDocument");
    }

    String plainTextContentDocument = documentService.getDocumentContentAsJson(contentDocument);
    String updatedToneResponse = llmService.getUpdatedTone(plainTextContentDocument, documentStyle);

    documentService.updateDocumentToneAndSaveCopy(contentDocument,
        ResponseUtils.parseMappings(updatedToneResponse), DocumentUtils.appendToneToFileName(contentFile.getOriginalFilename(), documentStyle));

    // Simple response to indicate that everything completed
    return ResponseEntity.ok("File successfully uploaded, processing completed");
  }
}
