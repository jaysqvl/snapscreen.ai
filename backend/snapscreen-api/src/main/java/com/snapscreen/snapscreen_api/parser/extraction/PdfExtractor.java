package com.snapscreen.snapscreen_api.parser.extraction;

import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extracts text content from PDF documents, preserving layout and formatting information.
 * This is equivalent to step 1 of the open-resume solution.
 */
@Component
public class PdfExtractor {

    /**
     * Extract TextItems from a PDF file
     * @param pdfFile the PDF file to extract from
     * @return List of TextItem objects with position and formatting information
     */
    public List<TextItem> extractTextFromPdf(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return extractTextFromDocument(document);
        }
    }

    /**
     * Extract TextItems from a PDF input stream
     * @param inputStream PDF input stream
     * @return List of TextItem objects with position and formatting information
     */
    public List<TextItem> extractTextFromPdf(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            return extractTextFromDocument(document);
        }
    }

    private List<TextItem> extractTextFromDocument(PDDocument document) throws IOException {
        List<TextItem> textItems = new ArrayList<>();
        
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            PDPage page = document.getPage(i);
            int pageNumber = i + 1;
            
            CustomTextStripper stripper = new CustomTextStripper(pageNumber);
            stripper.setStartPage(pageNumber);
            stripper.setEndPage(pageNumber);
            stripper.getText(document);
            
            textItems.addAll(stripper.getTextItems());
        }
        
        return textItems;
    }
    
    /**
     * Custom PDF text stripper that preserves text position and formatting
     */
    private static class CustomTextStripper extends PDFTextStripper {
        private final List<TextItem> textItems = new ArrayList<>();
        private final Map<String, Boolean> fontIsBold = new HashMap<>();
        private final int pageNumber;
        
        public CustomTextStripper(int pageNumber) throws IOException {
            this.pageNumber = pageNumber;
        }
        
        @Override
        protected void processTextPosition(TextPosition text) {
            super.processTextPosition(text);
            
            if (text.getUnicode().trim().isEmpty()) {
                return;
            }
            
            String fontName = text.getFont().getName();
            boolean isBold = isFontBold(fontName);
            
            TextItem item = new TextItem(
                    text.getUnicode(), 
                    text.getXDirAdj(), 
                    text.getXDirAdj() + text.getWidth(), 
                    text.getYDirAdj(),
                    isBold,
                    false,  // isNewLine will be set in post-processing
                    pageNumber
            );
            
            textItems.add(item);
        }
        
        // Basic heuristic to determine if a font is bold
        private boolean isFontBold(String fontName) {
            if (fontIsBold.containsKey(fontName)) {
                return fontIsBold.get(fontName);
            }
            
            boolean isBold = fontName.toLowerCase().contains("bold");
            fontIsBold.put(fontName, isBold);
            return isBold;
        }
        
        public List<TextItem> getTextItems() {
            // Post-process to mark new lines
            if (!textItems.isEmpty()) {
                // Sort by Y position (top to bottom), then X position (left to right)
                textItems.sort((a, b) -> {
                    if (Math.abs(a.getY() - b.getY()) < 2.0f) {
                        return Float.compare(a.getX1(), b.getX1());
                    }
                    return Float.compare(b.getY(), a.getY());  // Reverse because PDF coordinates start from bottom
                });
                
                // Mark new lines
                float lastY = textItems.get(0).getY();
                textItems.get(0).setNewLine(true);
                
                for (int i = 1; i < textItems.size(); i++) {
                    TextItem item = textItems.get(i);
                    if (Math.abs(item.getY() - lastY) > 2.0f) {
                        item.setNewLine(true);
                        lastY = item.getY();
                    }
                }
            }
            
            return textItems;
        }
    }
}
