package com.automwrite.assessment.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseUtils {

  private ResponseUtils () {}

  public static Map<String, String> parseMappings(String input) {
    HashMap<String, String> mappings = new HashMap<>();
    // Regex pattern to match "key" -> "value"
    String regex = "\"(.*?)(?<!\\\\)\" -> \"(.*?)(?<!\\\\)\"";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(input);


    // Find and add matches to the HashMap
    while (matcher.find()) {


      String key = matcher.group(1); // Group 1 is the formal text
      String value = matcher.group(2); // Group 2 is the casual text
      mappings.put(key, value);
    }

    return mappings;
  }

}
