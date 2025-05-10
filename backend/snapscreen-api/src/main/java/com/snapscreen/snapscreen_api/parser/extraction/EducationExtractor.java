package com.snapscreen.snapscreen_api.parser.extraction;

import com.snapscreen.snapscreen_api.model.resumeparser.ResumeLine;
import com.snapscreen.snapscreen_api.model.resumeparser.ResumeSection;
import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Education;
import com.snapscreen.snapscreen_api.parser.scoring.FeatureScoringSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Extracts education information from resume sections.
 * Adapts the extract-education.ts functionality from the open-resume solution.
 */
@Component
public class EducationExtractor {

    private final FeatureScoringSystem scoringSystem;
    private final SubsectionExtractor subsectionExtractor;
    
    // Common school keywords
    private static final List<String> SCHOOLS = Arrays.asList(
        "College", "University", "Institute", "School", "Academy", "BASIS", "Magnet"
    );
    
    // Common degree keywords
    private static final List<String> DEGREES = Arrays.asList(
        "Associate", "Bachelor", "Master", "PhD", "Ph."
    );
    
    // GPA pattern
    private static final Pattern GPA_PATTERN = Pattern.compile("[0-4]\\.(\\d{1,2})");
    
    @Autowired
    public EducationExtractor(FeatureScoringSystem scoringSystem, SubsectionExtractor subsectionExtractor) {
        this.scoringSystem = scoringSystem;
        this.subsectionExtractor = subsectionExtractor;
    }
    
    /**
     * Extract education information from resume sections
     * @param sections Map of section names to ResumeSection objects
     * @return List of Education objects
     */
    public List<Education> extractEducation(Map<String, ResumeSection> sections) {
        List<Education> educations = new ArrayList<>();
        ResumeSection educationSection = null;
        
        // Find the education section
        for (Map.Entry<String, ResumeSection> entry : sections.entrySet()) {
            String sectionName = entry.getKey().toLowerCase();
            if (sectionName.contains("education") || 
                sectionName.contains("academic") || 
                sectionName.contains("degree")) {
                educationSection = entry.getValue();
                break;
            }
        }
        
        if (educationSection == null || educationSection.getLines() == null || educationSection.getLines().isEmpty()) {
            return educations;
        }
        
        // Divide the section into subsections (one per school)
        List<List<ResumeLine>> subsections = subsectionExtractor.divideSectionIntoSubsections(educationSection.getLines());
        
        for (List<ResumeLine> subsection : subsections) {
            // Get all text items from the subsection
            List<TextItem> textItems = subsection.stream()
                .flatMap(line -> line.getTextItems().stream())
                .collect(Collectors.toList());
            
            // Extract education details
            String school = extractSchool(textItems);
            String degree = extractDegree(textItems);
            String gpa = extractGpa(textItems);
            String date = extractDate(textItems);
            
            // Extract descriptions
            List<String> descriptions = new ArrayList<>();
            int descriptionsLineIdx = subsectionExtractor.getDescriptionsLineIdx(subsection);
            if (descriptionsLineIdx != -1) {
                List<ResumeLine> descriptionLines = subsection.subList(descriptionsLineIdx, subsection.size());
                descriptions = subsectionExtractor.getBulletPointsFromLines(descriptionLines);
            }
            
            // Parse start and end dates
            String[] dateParts = parseDate(date);
            String startDate = dateParts[0];
            String endDate = dateParts[1];
            
            // Create the education object
            Education education = new Education();
            education.setSchool(school);
            education.setDegree(degree);
            education.setGpa(gpa);
            education.setStartDate(startDate);
            education.setEndDate(endDate);
            
            // Add descriptions
            for (String desc : descriptions) {
                education.addDescription(desc);
            }
            
            educations.add(education);
        }
        
        return educations;
    }
    
    /**
     * Extract school using feature scoring
     */
    private String extractSchool(List<TextItem> textItems) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        
        // School features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> SCHOOLS.stream().anyMatch(school -> 
                textItem.getText().contains(school)),
            4));
        
        // Negative features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> DEGREES.stream().anyMatch(degree -> 
                textItem.getText().contains(degree)),
            -4));
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.hasNumber(), -2));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets, false);
    }
    
    /**
     * Extract degree using feature scoring
     */
    private String extractDegree(List<TextItem> textItems) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        
        // Degree features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> DEGREES.stream().anyMatch(degree -> 
                textItem.getText().contains(degree)),
            4));
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> Pattern.compile("[ABM][A-Z\\.]").matcher(textItem.getText()).find(),
            3));
        
        // Negative features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> SCHOOLS.stream().anyMatch(school -> 
                textItem.getText().contains(school)),
            -4));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets, false);
    }
    
    /**
     * Extract GPA using feature scoring
     */
    private String extractGpa(List<TextItem> textItems) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        
        // GPA features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(GPA_PATTERN), 4, true));
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> {
                try {
                    float grade = Float.parseFloat(textItem.getText().trim());
                    return grade <= 4.0 && grade > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            },
            3, true));
        
        // Negative features
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.hasComma(), -3));
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.hasLetter(), -2));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets);
    }
    
    /**
     * Extract date using feature scoring (reusing date extraction from experience)
     */
    private String extractDate(List<TextItem> textItems) {
        // Common date patterns for extracting dates
        Pattern yearPattern = Pattern.compile("(?:19|20)\\d{2}");
        List<String> months = Arrays.asList(
            "January", "February", "March", "April", "May", "June", 
            "July", "August", "September", "October", "November", "December"
        );
        List<String> seasons = Arrays.asList(
            "Spring", "Summer", "Fall", "Winter"
        );
        
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        
        // Date features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(yearPattern), 2));
        
        // Month features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> months.stream().anyMatch(month -> 
                textItem.getText().contains(month) || 
                textItem.getText().contains(month.substring(0, 3))),
            2));
        
        // Season features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> seasons.stream().anyMatch(season -> 
                textItem.getText().contains(season)),
            1));
        
        // Present feature
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> textItem.getText().contains("Present"), 
            2));
        
        // Negative features
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.hasComma(), -1));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets, false);
    }
    
    /**
     * Parse a date string into start and end date parts
     */
    private String[] parseDate(String dateStr) {
        String startDate = "";
        String endDate = "";
        
        if (dateStr == null || dateStr.isEmpty()) {
            return new String[] { startDate, endDate };
        }
        
        // Check for common date formats
        if (dateStr.contains(" - ")) {
            String[] parts = dateStr.split(" - ");
            startDate = parts[0].trim();
            endDate = parts.length > 1 ? parts[1].trim() : "";
        } else if (dateStr.contains("-")) {
            String[] parts = dateStr.split("-");
            startDate = parts[0].trim();
            endDate = parts.length > 1 ? parts[1].trim() : "";
        } else if (dateStr.contains("–")) {
            String[] parts = dateStr.split("–");
            startDate = parts[0].trim();
            endDate = parts.length > 1 ? parts[1].trim() : "";
        } else if (dateStr.contains("to")) {
            String[] parts = dateStr.split("to");
            startDate = parts[0].trim();
            endDate = parts.length > 1 ? parts[1].trim() : "";
        } else {
            // If no clear delimiter, assume it's a single date (could be start or end)
            // Extract years to determine if it's likely a start or end date
            Pattern yearPattern = Pattern.compile("(19|20)\\d{2}");
            java.util.regex.Matcher matcher = yearPattern.matcher(dateStr);
            List<String> years = new ArrayList<>();
            while (matcher.find()) {
                years.add(matcher.group());
            }
            
            if (years.size() == 2) {
                startDate = years.get(0);
                endDate = years.get(1);
            } else if (years.size() == 1) {
                // Single year could be either start or end
                // If "present" is in the string, it's likely an end date
                if (dateStr.toLowerCase().contains("present")) {
                    startDate = years.get(0);
                    endDate = "Present";
                } else {
                    // Default to assuming it's a start date
                    startDate = years.get(0);
                }
            } else {
                // No years found, just use the whole string as is
                startDate = dateStr;
            }
        }
        
        return new String[] { startDate, endDate };
    }
} 