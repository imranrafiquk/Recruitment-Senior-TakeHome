package com.automwrite.assessment.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.automwrite.assessment.service.DocumentService;
import com.automwrite.assessment.service.DocumentStyle;
import com.automwrite.assessment.utils.DocumentUtils;
import com.automwrite.assessment.utils.ResponseUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DocumentServiceImplTest {

  @Autowired
  DocumentService documentService;

  @Test
  void testUpdateDocument() throws IOException {

    // Load the DOCX file
    File contentFile = new File("different tones/automwrite - C - Grandiloquent tone.docx");
    FileInputStream fis = new FileInputStream(contentFile);
    XWPFDocument document = new XWPFDocument(fis);

    String outputFileName = DocumentUtils.appendToneToFileName(contentFile.getName(),
        DocumentStyle.CASUAL);
    documentService.updateDocumentToneAndSaveCopy(document,
        ResponseUtils.parseMappings(getMockResponse()),
        outputFileName);

    assertTrue(Files.exists(Paths.get("different tones/" + outputFileName)));

  }

  static String getMockResponse() {
    return """
        I'll provide the mapping of formal to casual text for paragraphs and table cells that need rewording. Empty paragraphs and unchanged elements will be skipped.
        
        MAPPINGS:
        
        "RE: Your recommendation letter" -> "About your financial review"
        
        "Dearest Bob," -> "Hi Bob,"
        
        "I trust you are well. My team and I wish that you and Sheryl are having a lovely Autumn. It is with tremendous pleasure that I write to provide you with a review of your well-performing financial assets." -> "Hope you and Sheryl are enjoying the fall! I wanted to give you an update on how your investments are doing."
        
        "When we first met we discussed at great length your wishes for your financial objectives, the big picture if you will for your finances over the next 20 years. You were strongly minded to consider the following:" -> "Remember when we met and talked about your financial goals for the next 20 years? Here's what you wanted to focus on:"
        
        "A "big picture" overview of your circumstances and vista into your future requirements" -> "A look at your current situation and future needs"
        
        "A deep dive into your risk profile, how we propagate risk within our portfolios and how we carefully ascertain the most suitable home for your investments." -> "Understanding how much risk you're comfortable with and finding the right investments for you"
        
        "An intricate review of your existing assets and savings" -> "A review of what you currently have saved and invested"
        
        "A proposal based upon the information you have given us which includes a specific recommendation as to products" -> "Specific recommendations based on what you've told us"
        
        "Firstly, permit me to explain the nature of this letter, it is intended to act as a guide and reference to my advice to you. While this letter is certainly detailed, it has been written with care to ensure that no stone is left unturned in a thorough examination of your finances. Allow me to iterate how it is my team's first and utmost priority to ensure that you receive the highest quality advice and we are deeply endeavored to this, as such should you require any clarification please do contact me or my team at your earliest convenience so that we may assist." -> "This letter outlines my advice to you. I've included lots of details to make sure we cover everything. If anything isn't clear, just give me or my team a call - we're here to help!"
        
        "Before providing a more in-depth review of your assets, here is a summary of my financial advice to you contained within this letter:" -> "Here's a quick summary of my advice before we get into the details:"
        
        Table cell: "Consolidate with your new Aviva personal pension with which you have already transferred your Royal London pension." -> "Move this into your new Aviva pension, where you've already transferred your Royal London pension."
        
        "It is important to understand the context in which my advice is given, the "big picture" and your long-term view on your investments is paramount, as such here is an overview of your overall finances:" -> "Let's look at the bigger picture of your finances:"
        
        "I suggest that once we have completed the work outlined in this letter that we meet again to discuss the outstanding items listed above." -> "Once we've finished what's in this letter, let's meet up to talk about these other things."
        
        "Should you have any further questions, please do contact my office so we may book a time to discuss. My office number is 02 324 23423." -> "Got questions? Just call me at 02 324 23423 and we can set up a time to chat."
        
        "Sincerely yours," -> "Best wishes,"
        """;
  }

}