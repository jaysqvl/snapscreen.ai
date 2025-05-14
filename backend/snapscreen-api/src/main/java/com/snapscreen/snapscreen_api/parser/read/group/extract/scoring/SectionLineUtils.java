package com.snapscreen.snapscreen_api.parser.read.group.extract.scoring;

import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for handling section lines in the resume.
 * Provides methods to extract and process lines from different sections.
 */
@Component
public class SectionLineUtils {
    
    /**
     * Get all lines from a specific section
     */
    public List<String> getSectionLines(Map<String, List<TextItem>> sections, String sectionName) {
        return sections.getOrDefault(sectionName, new ArrayList<>())
            .stream()
            .map(TextItem::getText)
            .collect(Collectors.toList());
    }

    /**
     * Get all text items from a specific section
     */
    public List<TextItem> getSectionItems(Map<String, List<TextItem>> sections, String sectionName) {
        return new ArrayList<>(sections.getOrDefault(sectionName, new ArrayList<>()));
    }

    /**
     * Check if a section exists
     */
    public boolean hasSection(Map<String, List<TextItem>> sections, String sectionName) {
        return sections.containsKey(sectionName) && !sections.get(sectionName).isEmpty();
    }

    /**
     * Get the first line from a section
     */
    public String getFirstLine(Map<String, List<TextItem>> sections, String sectionName) {
        List<TextItem> items = sections.getOrDefault(sectionName, new ArrayList<>());
        return items.isEmpty() ? "" : items.get(0).getText();
    }

    /**
     * Get all section names
     */
    public List<String> getSectionNames(Map<String, List<TextItem>> sections) {
        return new ArrayList<>(sections.keySet());
    }

    /**
     * Get the number of lines in a section
     */
    public int getSectionLineCount(Map<String, List<TextItem>> sections, String sectionName) {
        return sections.getOrDefault(sectionName, new ArrayList<>()).size();
    }
} 