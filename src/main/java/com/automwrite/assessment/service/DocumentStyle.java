package com.automwrite.assessment.service;

public enum DocumentStyle {
  CASUAL("Casual"), FORMAL("Formal"), GRANDILOQUENT("Grandiloquent"), UNKNOWN("Unknown");

  private final String styleText;

  DocumentStyle(String styleText) {
    this.styleText = styleText;
  }

  public static DocumentStyle getFromResponse(String response) {
    if (response.toLowerCase().contains(CASUAL.styleText.toLowerCase())) {
      return CASUAL;
    }
    if (response.toLowerCase().contains(FORMAL.styleText.toLowerCase())) {
      return FORMAL;
    }
    if (response.toLowerCase().contains(GRANDILOQUENT.styleText.toLowerCase())) {
      return GRANDILOQUENT;
    }

    return UNKNOWN;

  }

  public String getStyleText() {
    return styleText;
  }

}
