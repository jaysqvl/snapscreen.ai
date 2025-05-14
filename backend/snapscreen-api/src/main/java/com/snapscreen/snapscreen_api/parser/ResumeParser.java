package com.snapscreen.snapscreen_api.parser;

import com.snapscreen.snapscreen_api.model.resumeparser.ParsedResume;
import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import com.snapscreen.snapscreen_api.model.resumeparser.ResumeLine;
import com.snapscreen.snapscreen_api.model.resumeparser.ResumeSection;
import com.snapscreen.snapscreen_api.parser.read.PdfReader;
import com.snapscreen.snapscreen_api.parser.read.group.LineGrouper;
import com.snapscreen.snapscreen_api.parser.read.group.SectionGrouper;
import com.snapscreen.snapscreen_api.parser.read.group.extract.ResumeExtractor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Main resume parser that orchestrates the parsing process.
 * Follows the same step-by-step approach as the original implementation:
 * 1. Read PDF into text items
 * 2. Group text items into lines
 * 3. Group lines into sections
 * 4. Extract resume from sections
 */
@Component
public class ResumeParser {
    private final PdfReader pdfReader;
    private final LineGrouper lineGrouper;
    private final SectionGrouper sectionGrouper;
    private final ResumeExtractor resumeExtractor;

    public ResumeParser(
        PdfReader pdfReader,
        LineGrouper lineGrouper,
        SectionGrouper sectionGrouper,
        ResumeExtractor resumeExtractor
    ) {
        this.pdfReader = pdfReader;
        this.lineGrouper = lineGrouper;
        this.sectionGrouper = sectionGrouper;
        this.resumeExtractor = resumeExtractor;
    }

    /**
     * Parse a resume from a PDF file
     * @param fileUrl URL of the PDF file to parse
     * @return Parsed Resume object
     */
    public ParsedResume parseResumeFromPdf(String fileUrl) {
        // Step 1: Read PDF into text items
        java.io.File pdfFile = new java.io.File(fileUrl);
        List<TextItem> textItems;
        try {
            textItems = pdfReader.extractTextFromPdf(pdfFile);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read PDF file: " + fileUrl, e);
        }

        // Step 2: Group text items into lines
        List<ResumeLine> lines = lineGrouper.groupIntoLines(textItems);

        // Step 3: Group lines into sections
        Map<String, ResumeSection> sections = sectionGrouper.groupIntoSections(lines);

        // Step 4: Extract resume from sections
        ParsedResume parsedResume = resumeExtractor.extractResumeFromSections(sections);
        return parsedResume;
    }
} 