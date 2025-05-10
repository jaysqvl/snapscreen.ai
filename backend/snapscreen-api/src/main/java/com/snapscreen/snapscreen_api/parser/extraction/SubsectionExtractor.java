package com.snapscreen.snapscreen_api.parser.extraction;

import com.snapscreen.snapscreen_api.model.resumeparser.ResumeLine;
import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for dividing resume sections into subsections and
 * handling bullet points, combining functionality from the subsections.ts
 * and bullet-points.ts files in the open-resume solution.
 */
@Component
public class SubsectionExtractor {

    // List of bullet point characters to detect
    private static final List<String> BULLET_POINTS = Arrays.asList(
        "⋅", "∙", "🞄", "•", "⦁", "⚫︎", "●", "⬤", "⚬", "○", 
        "-", "○", "▪", "■", "◦", "»", "►", "✓", "✔", "★", "☆", "*"
    );
    
    /**
     * Divide lines into subsections based on formatting and spacing
     * @param lines List of resume lines
     * @return List of subsections, where each subsection is a list of lines
     */
    public List<List<ResumeLine>> divideSectionIntoSubsections(List<ResumeLine> lines) {
        if (lines.isEmpty()) {
            return new ArrayList<>();
        }
        
        // First try to divide by line gaps
        List<List<ResumeLine>> subsections = divideByLineGap(lines);
        
        // If only one subsection was found, try to divide by bold text
        if (subsections.size() == 1) {
            subsections = divideByBoldText(lines);
        }
        
        return subsections;
    }
    
    /**
     * Divide lines into subsections based on line gap (vertical spacing)
     */
    private List<List<ResumeLine>> divideByLineGap(List<ResumeLine> lines) {
        List<List<ResumeLine>> subsections = new ArrayList<>();
        
        // Calculate typical line gap
        float typicalLineGap = calculateTypicalLineGap(lines);
        float subsectionThreshold = typicalLineGap * 1.4f;
        
        List<ResumeLine> currentSubsection = new ArrayList<>();
        currentSubsection.add(lines.get(0));
        
        for (int i = 1; i < lines.size(); i++) {
            ResumeLine prevLine = lines.get(i-1);
            ResumeLine currentLine = lines.get(i);
            
            // Calculate line gap
            float lineGap = Math.abs(prevLine.getY() - currentLine.getY());
            
            // If gap is larger than threshold, start a new subsection
            if (lineGap > subsectionThreshold) {
                subsections.add(currentSubsection);
                currentSubsection = new ArrayList<>();
            }
            
            currentSubsection.add(currentLine);
        }
        
        // Add the last subsection
        if (!currentSubsection.isEmpty()) {
            subsections.add(currentSubsection);
        }
        
        return subsections;
    }
    
    /**
     * Calculate the typical line gap in a set of lines
     */
    private float calculateTypicalLineGap(List<ResumeLine> lines) {
        // Extract y-positions
        List<Float> lineYs = lines.stream()
            .map(ResumeLine::getY)
            .collect(Collectors.toList());
        
        // Calculate gaps between consecutive lines
        Map<Float, Integer> gapCounts = new HashMap<>();
        float mostCommonGap = 0;
        int maxCount = 0;
        
        for (int i = 1; i < lineYs.size(); i++) {
            float gap = Math.abs(lineYs.get(i) - lineYs.get(i-1));
            gap = Math.round(gap);
            
            gapCounts.put(gap, gapCounts.getOrDefault(gap, 0) + 1);
            
            if (gapCounts.get(gap) > maxCount) {
                maxCount = gapCounts.get(gap);
                mostCommonGap = gap;
            }
        }
        
        return mostCommonGap > 0 ? mostCommonGap : 10.0f; // Default to 10pt if can't determine
    }
    
    /**
     * Divide lines into subsections based on bold text
     */
    private List<List<ResumeLine>> divideByBoldText(List<ResumeLine> lines) {
        List<List<ResumeLine>> subsections = new ArrayList<>();
        List<ResumeLine> currentSubsection = new ArrayList<>();
        
        for (int i = 0; i < lines.size(); i++) {
            ResumeLine line = lines.get(i);
            
            // Check if line starts with bold text and is not a bullet point
            boolean startsWithBold = !line.getTextItems().isEmpty() && 
                                    line.getTextItems().get(0).isBold() &&
                                    !containsBulletPoint(line.getText());
            
            if (startsWithBold && !currentSubsection.isEmpty()) {
                // Start a new subsection
                subsections.add(currentSubsection);
                currentSubsection = new ArrayList<>();
            }
            
            currentSubsection.add(line);
        }
        
        // Add the last subsection
        if (!currentSubsection.isEmpty()) {
            subsections.add(currentSubsection);
        }
        
        return subsections;
    }
    
    /**
     * Check if text contains a bullet point
     */
    private boolean containsBulletPoint(String text) {
        return BULLET_POINTS.stream().anyMatch(text::contains);
    }
    
    /**
     * Get the index of the line where descriptions/bullet points start
     * @param lines List of resume lines
     * @return Index of the first description line, or -1 if not found
     */
    public int getDescriptionsLineIdx(List<ResumeLine> lines) {
        // First try to find by bullet points
        for (int i = 0; i < lines.size(); i++) {
            if (containsBulletPoint(lines.get(i).getText())) {
                return i;
            }
        }
        
        // Fallback: look for lines with at least 8 words
        for (int i = 0; i < lines.size(); i++) {
            String lineText = lines.get(i).getText();
            if (lineText.split("\\s+").length >= 8) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Extract bullet points from a set of lines
     * @param lines List of lines that contain bullet points
     * @return List of extracted bullet point text
     */
    public List<String> getBulletPointsFromLines(List<ResumeLine> lines) {
        // Find the most common bullet point character
        String mostCommonBulletPoint = findMostCommonBulletPoint(lines);
        
        // If no bullet points found, just return the text of each line
        if (mostCommonBulletPoint == null) {
            return lines.stream()
                .map(ResumeLine::getText)
                .filter(text -> !text.trim().isEmpty())
                .collect(Collectors.toList());
        }
        
        // Combine all text
        String allText = lines.stream()
            .map(ResumeLine::getText)
            .collect(Collectors.joining(" "));
        
        // Split by bullet point
        String[] parts = allText.split(java.util.regex.Pattern.quote(mostCommonBulletPoint));
        
        // Process and clean up bullet points
        return Arrays.stream(parts)
            .map(String::trim)
            .filter(text -> !text.isEmpty())
            .collect(Collectors.toList());
    }
    
    /**
     * Find the most common bullet point character in a set of lines
     */
    private String findMostCommonBulletPoint(List<ResumeLine> lines) {
        Map<String, Integer> bulletCounts = new HashMap<>();
        
        // Count all bullet points
        for (ResumeLine line : lines) {
            String text = line.getText();
            for (String bullet : BULLET_POINTS) {
                if (text.contains(bullet)) {
                    bulletCounts.put(bullet, bulletCounts.getOrDefault(bullet, 0) + 1);
                }
            }
        }
        
        // Find the most common
        return bulletCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
} 