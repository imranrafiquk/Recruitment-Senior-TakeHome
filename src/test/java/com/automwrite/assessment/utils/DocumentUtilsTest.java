package com.automwrite.assessment.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.automwrite.assessment.service.DocumentStyle;
import org.junit.jupiter.api.Test;

class DocumentUtilsTest {

  @Test
  void testAppendToFileNameWithCasualStyle() {
    String input = "Report.docx";
    String expected = "Report-Casual.docx";
    String actual = DocumentUtils.appendToneToFileName(input, DocumentStyle.CASUAL);
    assertEquals(expected, actual, "The file name should have '-Casual' appended before the extension.");
  }

  @Test
  void testAppendToFileNameWithFormalStyle() {
    String input = "Summary.docx";
    String expected = "Summary-Formal.docx";
    String actual = DocumentUtils.appendToneToFileName(input, DocumentStyle.FORMAL);
    assertEquals(expected, actual, "The file name should have '-Formal' appended before the extension.");
  }


}