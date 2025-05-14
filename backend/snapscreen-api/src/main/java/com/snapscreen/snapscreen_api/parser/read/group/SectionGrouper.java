package com.snapscreen.snapscreen_api.parser.read.group;

import com.snapscreen.snapscreen_api.model.resumeparser.ResumeLine;
import com.snapscreen.snapscreen_api.model.resumeparser.ResumeSection;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Identifies and groups lines into resume sections.
 * This is equivalent to step 3 of the open-resume solution.
 */
@Component
public class SectionGrouper {

    // Common section keywords to identify resume sections
    private static final Map<String, List<String>> SECTION_KEYWORDS = new HashMap<>();
    
    static {
        SECTION_KEYWORDS.put("profile", Arrays.asList("profile", "summary", "about", "objective", "personal"));
        SECTION_KEYWORDS.put("education", Arrays.asList("education", "academic", "degree", "university", "college", "school"));
        SECTION_KEYWORDS.put("experience", Arrays.asList("experience", "employment", "work", "history", "job"));
        SECTION_KEYWORDS.put("skills", Arrays.asList("skills", "expertise", "technologies", "core competencies", "competences", "technical"));
        SECTION_KEYWORDS.put("projects", Arrays.asList("projects", "portfolio", "academic projects", "personal projects"));
        SECTION_KEYWORDS.put("certifications", Arrays.asList("certifications", "certificates", "licenses", "courses"));
        SECTION_KEYWORDS.put("awards", Arrays.asList("awards", "honors", "achievements", "accomplishments"));
        SECTION_KEYWORDS.put("languages", Arrays.asList("languages", "language proficiency"));
        SECTION_KEYWORDS.put("interests", Arrays.asList("interests", "hobbies", "activities"));
        SECTION_KEYWORDS.put("references", Arrays.asList("references", "referees"));
        SECTION_KEYWORDS.put("publications", Arrays.asList("publications", "papers", "articles"));
        SECTION_KEYWORDS.put("volunteer", Arrays.asList("volunteer", "volunteering", "community service"));
    }

    /**
     * Group resume lines into sections
     * @param lines List of ResumeLine objects
     * @return Map of section names to lists of lines
     */
    public Map<String, ResumeSection> groupIntoSections(List<ResumeLine> lines) {
        Map<String, ResumeSection> sections = new HashMap<>();
        
        // Add a default "profile" section to catch header information
        ResumeSection profileSection = new ResumeSection();
        profileSection.setName("profile");
        profileSection.setLines(new ArrayList<>());
        sections.put("profile", profileSection);
        
        String currentSection = "profile";
        
        for (int i = 0; i < lines.size(); i++) {
            ResumeLine line = lines.get(i);
            String lineText = line.getText().toLowerCase().trim();
            
            // Skip empty lines
            if (lineText.isEmpty()) {
                continue;
            }
            
            // Check if this line is a section header
            String sectionName = identifySectionHeader(lineText);
            
            if (sectionName != null) {
                currentSection = sectionName;
                
                // Create new section if it doesn't exist
                if (!sections.containsKey(currentSection)) {
                    ResumeSection section = new ResumeSection();
                    section.setName(currentSection);
                    section.setLines(new ArrayList<>());
                    sections.put(currentSection, section);
                }
                
                // Don't add the section header line to the section content
                continue;
            }
            
            // Add the line to the current section
            sections.get(currentSection).getLines().add(line);
        }
        
        return sections;
    }
    
    /**
     * Identify if a line is a section header based on section keywords
     * @param lineText The text of the line
     * @return The section name if identified, null otherwise
     */
    private String identifySectionHeader(String lineText) {
        for (Map.Entry<String, List<String>> entry : SECTION_KEYWORDS.entrySet()) {
            String sectionName = entry.getKey();
            List<String> keywords = entry.getValue();
            
            // Check if line contains any of the section keywords
            for (String keyword : keywords) {
                // Match either the keyword as a whole word or as a standalone section header
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b", Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(lineText).find() && 
                    // Additional heuristic: section headers are usually short
                    lineText.split("\\s+").length <= 5) {
                    return sectionName;
                }
            }
        }
        
        return null;
    }
}
