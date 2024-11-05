package com.automwrite.assessment.service;

import java.util.concurrent.CompletableFuture;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public interface LlmService {

    DocumentStyle extractDocumentTone(XWPFDocument toneFile);

    String updateDocumentTone(XWPFDocument document, DocumentStyle documentStyle, String originalFilename);

    CompletableFuture<DocumentStyle>  extractDocumentToneAsync(XWPFDocument contentDocument);

    CompletableFuture<String> updateDocumentToneAsync(XWPFDocument document, DocumentStyle documentStyle,
        String originalFilename);

}
