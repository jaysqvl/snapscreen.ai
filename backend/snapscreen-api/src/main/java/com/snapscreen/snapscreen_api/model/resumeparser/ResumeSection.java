package com.snapscreen.snapscreen_api.model.resumeparser;

import java.util.ArrayList;
import java.util.List;

public class ResumeSection {
    private String title;
    private List<ResumeLine> lines;
    
    // Default constructor
    public ResumeSection() {
    }

    public ResumeSection(String title) {
        this.title = title;
        this.lines = new ArrayList<>();
    }

    public ResumeSection(String title, List<ResumeLine> lines) {
        this.title = title;
        this.lines = lines;
    }
    

    // Detects if the section is a section title
    public boolean isSectionTitle() {
        return lines.stream().anyMatch(ResumeLine::isSectionTitle);
    }

    // Getters and Setters
    public void addLine(ResumeLine line) {
        lines.add(line);
    }

    public void setLines(List<ResumeLine> lines) {
        this.lines = lines;
    }

    public String getTitle() {
        return title;
    }
    
    public List<ResumeLine> getLines() {
        return lines;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
