package com.automwrite.assessment.service.impl;

import com.automwrite.assessment.service.DocumentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;

@Service
public class DocumentServiceImpl implements DocumentService {

  @Override
  public String getDocumentContentAsText(XWPFDocument document) {
    return document.getParagraphs().stream().map(XWPFParagraph::getParagraphText)
        .collect(Collectors.joining("\n"));
  }

  @Override
  public String getDocumentContentAsTextIncludingTables(XWPFDocument document) {
    StringBuilder stringBuilder = new StringBuilder();

    for (IBodyElement elem : document.getBodyElements()) {
      if (elem.getElementType().equals(BodyElementType.PARAGRAPH)) {
        XWPFParagraph paragraph = ((XWPFParagraph) (elem));
        stringBuilder.append(paragraph.getText());
        stringBuilder.append("\n");

      } else if (elem.getElementType().equals(BodyElementType.TABLE)) {
        XWPFTable table = ((XWPFTable) elem);

        for (XWPFTableRow row : table.getRows()) {
          for (int cellIndex = 0; cellIndex < row.getTableCells().size(); cellIndex++) {
            XWPFTableCell cell = row.getCell(cellIndex);
            stringBuilder.append(cell.getText());
            if (cellIndex < row.getTableCells().size() - 1) {
              stringBuilder.append("\t"); // or use " " for a space
            }
          }
          stringBuilder.append("\n");

        }

      }
    }

    return stringBuilder.toString();
  }

  @Override
  public String getDocumentContentAsJson(XWPFDocument document) throws JsonProcessingException {

    List<Object> contentList = new ArrayList<>();

    for (IBodyElement elem : document.getBodyElements()) {
      if (elem.getElementType().equals(BodyElementType.PARAGRAPH)) {
        XWPFParagraph paragraph = ((XWPFParagraph) (elem));

        Map<String, Object> paragraphMap = new HashMap<>();
        paragraphMap.put("type", "paragraph");
        paragraphMap.put("text", paragraph.getText());
        contentList.add(paragraphMap);

      } else if (elem.getElementType().equals(BodyElementType.TABLE)) {
        XWPFTable table = ((XWPFTable) elem);

        Map<String, Object> tableMap = new HashMap<>();
        tableMap.put("type", "table");
        List<List<String>> rowsList = new ArrayList<>();

        for (XWPFTableRow row : table.getRows()) {
          List<String> cellList = new ArrayList<>();
          for (XWPFTableCell cell : row.getTableCells()) {
            cellList.add(cell.getText());
          }
          rowsList.add(cellList);
        }
        tableMap.put("rows", rowsList);
        contentList.add(tableMap);

      }
    }

    // Prepare JSON structure
    HashMap<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("content", contentList);

    // Convert to JSON
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMap);


  }

  @Override
  public void updateDocumentToneAndSaveCopy(XWPFDocument document,
      Map<String, String> toneUpdateMapping, String outputFileName) throws IOException {

    // Iterate through paragraphs
    for (XWPFParagraph paragraph : document.getParagraphs()) {

      StringBuilder paragraphText = new StringBuilder();
      List<XWPFRun> runs = paragraph.getRuns();

      // Concatenate all text in the paragraph
      for (XWPFRun run : runs) {
        paragraphText.append(run.getText(0));
      }

      String fullText = paragraphText.toString();
      if (toneUpdateMapping.containsKey(fullText)) {
        updateContent(toneUpdateMapping, paragraph, runs, fullText);

      }


    }

    // Iterate through tables
    for (XWPFTable table : document.getTables()) {
      for (XWPFTableRow row : table.getRows()) {
        for (XWPFTableCell cell : row.getTableCells()) {
          List<XWPFParagraph> cellParagraphs = cell.getParagraphs();

          for (XWPFParagraph paragraph : cellParagraphs) {
            StringBuilder paragraphText = new StringBuilder();
            List<XWPFRun> runs = paragraph.getRuns();

            // Concatenate all text in the paragraph
            for (XWPFRun run : runs) {
              paragraphText.append(run.getText(0));
            }

            String fullText = paragraphText.toString();
            if (toneUpdateMapping.containsKey(fullText)) {
              updateContent(toneUpdateMapping, paragraph, runs, fullText);

            }

          }

        }
      }
    }

    // Save the modified document
    try (FileOutputStream fos = new FileOutputStream(
        new File("different tones/" + outputFileName))) {
      document.write(fos);
    }

  }

  private static void updateContent(Map<String, String> toneUpdateMapping, XWPFParagraph paragraph,
      List<XWPFRun> runs, String fullText) {
    // Capture the formatting of the last run (or relevant runs)
    XWPFRun lastRunFormatting = runs.isEmpty() ? null : runs.get(runs.size() - 1);
    String lastRunFormattingData =
        lastRunFormatting != null ? getRunFormattingData(lastRunFormatting) : "";

    // Clear existing runs by removing them one by one
    for (int i = runs.size() - 1; i >= 0; i--) {
      paragraph.removeRun(i);
    }

    // Add the text before the replacement
    XWPFRun newRun = paragraph.createRun();
    newRun.setText(toneUpdateMapping.get(fullText));

    applyRunFormatting(newRun, lastRunFormattingData);
  }


  private static String getRunFormattingData(XWPFRun run) {
    StringBuilder formattingData = new StringBuilder();
    formattingData.append(run.isBold()).append(",");
    formattingData.append(run.isItalic()).append(",");
    formattingData.append(run.getUnderline()).append(",");

    formattingData.append(run.getFontFamily()).append(",");

    double fontSize = run.getFontSizeAsDouble() != null ? run.getFontSizeAsDouble() : -1;
    formattingData.append(fontSize).append(","); // Default size if not set
    formattingData.append(run.isStrikeThrough()).append(",");
    return formattingData.toString();
  }

  private static void applyRunFormatting(XWPFRun run, String formattingData) {
    String[] data = formattingData.split(",");
    if (data.length > 0) {
      run.setBold(Boolean.parseBoolean(data[0]));
      run.setItalic(Boolean.parseBoolean(data[1]));
      run.setUnderline(UnderlinePatterns.valueOf(data[2]));
      run.setFontFamily(data[3]);
      run.setFontSize(data[4] != null ? Double.parseDouble(data[4]) : -1);
      run.setStrikeThrough(Boolean.parseBoolean(data[5]));
    }
  }

}
