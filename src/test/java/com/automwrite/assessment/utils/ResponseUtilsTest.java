package com.automwrite.assessment.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ResponseUtilsTest {

  @Test
  void testMappingExtraction() {
    // Sample input string for testing
    String input = """
            "Dearest Bob," -> "Hi Bob,"
            "I trust you are well." -> "Hope you're well."
            "Best regards," -> "Cheers,"
            """;

    // Expected map after processing the input, ignoring the first mapping
    Map<String, String> expectedMap = new HashMap<>();
    expectedMap.put("Dearest Bob,", "Hi Bob,");
    expectedMap.put("I trust you are well.", "Hope you're well.");
    expectedMap.put("Best regards,", "Cheers,");

    // Actual map extracted from the input
    Map<String, String> actualMap = extractMapping(input);

    // Verify the size of the map
    assertEquals(expectedMap.size(), actualMap.size());

    // Verify the content of the map
    for (Map.Entry<String, String> entry : expectedMap.entrySet()) {
      assertEquals(entry.getValue(), actualMap.get(entry.getKey()));
    }
  }



  @Test
  void testEmptyInput() {
    String input = "";

    Map<String, String> actualMap = extractMapping(input);

    assertTrue(actualMap.isEmpty(), "Expected empty map for empty input.");
  }


  // Method to simulate the extraction logic from the original class
  private Map<String, String> extractMapping(String input) {
    return ResponseUtils.parseMappings(input);
  }

}