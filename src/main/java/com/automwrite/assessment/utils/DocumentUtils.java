package com.automwrite.assessment.utils;

import com.automwrite.assessment.service.DocumentStyle;

public class DocumentUtils {

  private DocumentUtils(){}

  public static String appendToneToFileName(String fileName, DocumentStyle style) {

    // Get the index of the last dot to separate the name and extension
    int dotIndex = fileName.lastIndexOf('.');
    String baseName = fileName.substring(0, dotIndex);
    String extension = fileName.substring(dotIndex); // Keep the .docx extension

    // Return the new file name with "-casual" appended
    return baseName + "-" + style.getStyleText() + extension;

  }

}
