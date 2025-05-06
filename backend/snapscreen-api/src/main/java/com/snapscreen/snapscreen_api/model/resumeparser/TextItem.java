package com.snapscreen.snapscreen_api.model.resumeparser;

/**
 * Represents a single text item extracted from a PDF document.
 * Contains the text content along with its metadata (position, formatting).
 */
public class TextItem {
    
    private String text;         // The actual text content
    private float x1;            // Starting x position
    private float x2;            // Ending x position
    private float y;             // Y position (from bottom of page)
    private boolean isBold;      // Whether the text is bold
    private boolean isNewLine;   // Whether the text starts a new line
    private int pageNumber;      // The page number this text appears on
    
    // Default constructor
    public TextItem() {
    }
    
    // Full constructor
    public TextItem(String text, float x1, float x2, float y, boolean isBold, boolean isNewLine, int pageNumber) {
        this.text = text;
        this.x1 = x1;
        this.x2 = x2;
        this.y = y;
        this.isBold = isBold;
        this.isNewLine = isNewLine;
        this.pageNumber = pageNumber;
    }
    
    // Calculate the width of this text item
    public float getWidth() {
        return x2 - x1;
    }
    
    // Check if this text item is adjacent to another text item
    public boolean isAdjacentTo(TextItem other) {
        if (this.pageNumber != other.pageNumber) {
            return false;
        }
        
        // Check if they're on the same line (approximate y-coordinate)
        float yTolerance = 2.0f; // Allow small differences in y-coordinate
        if (Math.abs(this.y - other.y) > yTolerance) {
            return false;
        }
        
        // Check if they're horizontally adjacent
        return this.x2 < other.x1;
    }
    
    // Calculate distance to another text item (if they're adjacent)
    public float distanceTo(TextItem other) {
        if (!isAdjacentTo(other)) {
            return Float.MAX_VALUE;
        }
        return other.x1 - this.x2;
    }
    
    // Check if this text item is uppercase
    public boolean isUppercase() {
        return text != null && text.equals(text.toUpperCase()) && !text.equals(text.toLowerCase());
    }
    
    // Getters and setters
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public float getX1() {
        return x1;
    }
    
    public void setX1(float x1) {
        this.x1 = x1;
    }
    
    public float getX2() {
        return x2;
    }
    
    public void setX2(float x2) {
        this.x2 = x2;
    }
    
    public float getY() {
        return y;
    }
    
    public void setY(float y) {
        this.y = y;
    }
    
    public boolean isBold() {
        return isBold;
    }
    
    public void setBold(boolean bold) {
        isBold = bold;
    }
    
    public boolean isNewLine() {
        return isNewLine;
    }
    
    public void setNewLine(boolean newLine) {
        isNewLine = newLine;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    @Override
    public String toString() {
        return "TextItem{" +
                "text='" + text + '\'' +
                ", x1=" + x1 +
                ", x2=" + x2 +
                ", y=" + y +
                ", isBold=" + isBold +
                ", isNewLine=" + isNewLine +
                ", pageNumber=" + pageNumber +
                '}';
    }
} 