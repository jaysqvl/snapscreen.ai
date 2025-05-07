package com.snapscreen.snapscreen_api.model.resumeparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a section in a resume (e.g., EDUCATION, EXPERIENCE, SKILLS).
 * A section consists of a title line and content lines.
 * This is used during the parsing process to group related lines.
 */
public class ResumeSection {
    
    private String title;                      // Section title (e.g., "EDUCATION", "EXPERIENCE")
    private ResumeLine titleLine;              // The line containing the section title
    private List<ResumeLine> contentLines;     // Content lines in this section
    
    // Constructor
    public ResumeSection(ResumeLine titleLine) {
        this.titleLine = titleLine;
        this.title = titleLine.getLineContent().trim();
        this.contentLines = new ArrayList<>();
    }
    
    // Add a content line to this section
    public void addContentLine(ResumeLine line) {
        contentLines.add(line);
    }
    
    // Check if this section has any content
    public boolean hasContent() {
        return !contentLines.isEmpty();
    }
    
    // Get section content as plain text
    public String getSectionText() {
        StringBuilder builder = new StringBuilder();
        for (ResumeLine line : contentLines) {
            builder.append(line.getLineContent()).append("\n");
        }
        return builder.toString().trim();
    }
    
    // Detect subsections based on line spacing or formatting
    public List<List<ResumeLine>> detectSubsections() {
        List<List<ResumeLine>> subsections = new ArrayList<>();
        if (contentLines.isEmpty()) {
            return subsections;
        }
        
        List<ResumeLine> currentSubsection = new ArrayList<>();
        subsections.add(currentSubsection);
        
        // Add first line to first subsection
        currentSubsection.add(contentLines.get(0));
        
        // Group remaining lines by detecting breaks between subsections
        for (int i = 1; i < contentLines.size(); i++) {
            ResumeLine previousLine = contentLines.get(i - 1);
            ResumeLine currentLine = contentLines.get(i);
            
            // Detect subsection break using:
            // 1. Vertical spacing between lines that's larger than typical
            // 2. Bold formatting often indicates a new subsection
            
            float typicalLineGap = 14.0f; // This would be calculated dynamically in practice
            float lineGap = previousLine.getY() - currentLine.getY();
            
            boolean isNewSubsection = lineGap > (typicalLineGap * 1.4) || 
                                     (currentLine.containsBoldText() && !previousLine.containsBoldText());
            
            if (isNewSubsection) {
                // Start a new subsection
                currentSubsection = new ArrayList<>();
                subsections.add(currentSubsection);
            }
            
            currentSubsection.add(currentLine);
        }
        
        return subsections;
    }
    
    // Getters and setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public ResumeLine getTitleLine() {
        return titleLine;
    }
    
    public void setTitleLine(ResumeLine titleLine) {
        this.titleLine = titleLine;
        this.title = titleLine.getLineContent().trim();
    }
    
    public List<ResumeLine> getContentLines() {
        return contentLines;
    }
    
    public void setContentLines(List<ResumeLine> contentLines) {
        this.contentLines = contentLines;
    }
    
    @Override
    public String toString() {
        return "ResumeSection{" +
                "title='" + title + '\'' +
                ", contentLines=" + contentLines.size() +
                '}';
    }
} 