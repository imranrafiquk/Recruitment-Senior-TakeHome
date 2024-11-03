package com.automwrite.assessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public interface DocumentService {

  String getDocumentContentAsText(XWPFDocument document);

  String getDocumentContentAsTextIncludingTables(XWPFDocument document);

  String getDocumentContentAsJson(XWPFDocument document) throws JsonProcessingException;

  void updateDocumentToneAndSaveCopy(XWPFDocument document, Map<String, String> toneUpdateMapping,
      String outputFileName)
      throws IOException;
}
