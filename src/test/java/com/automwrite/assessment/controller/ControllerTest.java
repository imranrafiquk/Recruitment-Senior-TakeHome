package com.automwrite.assessment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.automwrite.assessment.service.DocumentStyle;
import com.automwrite.assessment.service.LlmService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(Controller.class)
@ExtendWith(MockitoExtension.class)
class ControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private LlmService llmService;

  @Test
  void testKnownValidResponses() throws Exception {
    when(llmService.extractDocumentToneAsync(any())).thenReturn(
        CompletableFuture.completedFuture(DocumentStyle.CASUAL));

    when(llmService.updateDocumentToneAsync(any(), any(), any())).thenReturn(
        CompletableFuture.completedFuture("Updated document"));

    MockMultipartFile toneFile = getMultiPartFile(
        "different tones/automwrite - A - Casual tone.docx", "toneFile");
    MockMultipartFile contentFile = getMultiPartFile(
        "different tones/automwrite - B - Formal tone.docx", "contentFile");

    mockMvc.perform(multipart("/api/transfer-style").file(toneFile).file(contentFile)
        .contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isOk());

  }

  private static MockMultipartFile getMultiPartFile(String filePath, String name)
      throws IOException {
    File toneFile = new File(filePath);
    byte[] toneFileContent = Files.readAllBytes(toneFile.toPath());

    return new MockMultipartFile(name, toneFile.getName(), MediaType.APPLICATION_OCTET_STREAM_VALUE,
        toneFileContent);
  }


}
