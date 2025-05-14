package com.snapscreen.snapscreen_api.parser.read.group.extract.scoring;

import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Handles subsections within resume sections.
 * Identifies and groups related content into subsections.
 */
@Component
public class SubsectionHandler {
    private static final Pattern SUBSECTION_PATTERN = Pattern.compile("^[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+\\d{4}");

    /**
     * Check if a text item is a subsection header
     */
    public boolean isSubsectionHeader(TextItem item) {
        String text = item.getText().trim();
        return SUBSECTION_PATTERN.matcher(text).matches() && 
               !DATE_PATTERN.matcher(text).matches();
    }

    /**
     * Group text items into subsections
     */
    public Map<String, List<TextItem>> groupIntoSections(List<TextItem> textItems) {
        Map<String, List<TextItem>> sections = new HashMap<>();
        String currentSection = "default";
        List<TextItem> currentItems = new ArrayList<>();
        sections.put(currentSection, currentItems);

        for (TextItem item : textItems) {
            if (isSubsectionHeader(item)) {
                currentSection = item.getText().trim();
                currentItems = new ArrayList<>();
                sections.put(currentSection, currentItems);
            } else {
                currentItems.add(item);
            }
        }

        return sections;
    }

    /**
     * Get text items for a specific subsection
     */
    public List<TextItem> getSubsectionItems(Map<String, List<TextItem>> sections, String subsectionName) {
        return sections.getOrDefault(subsectionName, new ArrayList<>());
    }

    /**
     * Get all subsection names
     */
    public List<String> getSubsectionNames(Map<String, List<TextItem>> sections) {
        return new ArrayList<>(sections.keySet());
    }
} 