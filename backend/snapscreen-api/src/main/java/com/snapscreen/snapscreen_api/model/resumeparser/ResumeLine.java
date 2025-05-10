package com.snapscreen.snapscreen_api.model.resumeparser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a single line of text in a resume.
 * A line is composed of one or more TextItem objects that have been grouped together.
 */
public class ResumeLine {
    
    private List<TextItem> textItems = new ArrayList<>();
    private float y;              // Y-coordinate of the line
    private int pageNumber;       // Page number this line appears on
    private boolean isSectionTitle = false;  // Whether this line is a section title
    
    // Default constructor
    public ResumeLine() {
    }
    
    // Constructor with initial text item
    public ResumeLine(TextItem initialItem) {
        this.textItems.add(initialItem);
        this.y = initialItem.getY();
        this.pageNumber = initialItem.getPageNumber();
    }
    
    // Add a text item to this line
    public void addTextItem(TextItem item) {
        textItems.add(item);
    }
    
    // Get the complete text content of this line
    public String getLineContent() {
        return textItems.stream()
                .sorted((a, b) -> Float.compare(a.getX1(), b.getX1()))
                .map(TextItem::getText)
                .collect(Collectors.joining(" ")).trim();
    }
    
    // Check if this line is likely a section title
    public boolean detectSectionTitle() {
        // Section title heuristics:
        // 1. Contains only one text item
        // 2. Text is bolded
        // 3. Text is all uppercase
        
        if (textItems.size() != 1) {
            return false;
        }
        
        TextItem item = textItems.get(0);
        if (!item.isBold()) {
            return false;
        }
        
        return item.isUppercase();
    }
    
    // Calculate line height from bottom of page
    public float getLineHeight() {
        return y;
    }
    
    // Get the left-most x-coordinate of this line
    public float getLeftX() {
        return textItems.stream()
                .map(TextItem::getX1)
                .min(Float::compare)
                .orElse(0f);
    }
    
    // Get the right-most x-coordinate of this line
    public float getRightX() {
        return textItems.stream()
                .map(TextItem::getX2)
                .max(Float::compare)
                .orElse(0f);
    }
    
    // Check if line contains bold text
    public boolean containsBoldText() {
        return textItems.stream().anyMatch(TextItem::isBold);
    }
    
    // Getters and setters
    public List<TextItem> getTextItems() {
        return textItems;
    }
    
    public void setTextItems(List<TextItem> textItems) {
        this.textItems = textItems;
    }
    
    public float getY() {
        return y;
    }
    
    public void setY(float y) {
        this.y = y;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public boolean isSectionTitle() {
        return isSectionTitle;
    }
    
    public void setSectionTitle(boolean sectionTitle) {
        isSectionTitle = sectionTitle;
    }
    
    // Add getText() method - alias for getLineContent()
    public String getText() {
        return getLineContent();
    }
    
    // Add setText() method 
    public void setText(String text) {
        // Clear existing items and add a new one with the text
        this.textItems.clear();
        TextItem item = new TextItem(text, 0, 0, 0, false, false, this.pageNumber);
        this.textItems.add(item);
    }
    
    @Override
    public String toString() {
        return "ResumeLine{" +
                "content='" + getLineContent() + '\'' +
                ", isSectionTitle=" + isSectionTitle +
                ", y=" + y +
                ", pageNumber=" + pageNumber +
                '}';
    }
} 