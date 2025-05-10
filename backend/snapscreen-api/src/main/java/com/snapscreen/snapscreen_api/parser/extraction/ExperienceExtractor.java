package com.snapscreen.snapscreen_api.parser.extraction;

import com.snapscreen.snapscreen_api.model.resumeparser.ResumeLine;
import com.snapscreen.snapscreen_api.model.resumeparser.ResumeSection;
import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Experience;
import com.snapscreen.snapscreen_api.parser.scoring.FeatureScoringSystem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Extracts work experience information from resume sections.
 * Adapts the extract-work-experience.ts functionality from the open-resume solution.
 */
@Component
public class ExperienceExtractor {

    private final FeatureScoringSystem scoringSystem;
    private final SubsectionExtractor subsectionExtractor;
    
    // Common date patterns for extracting dates
    private static final Pattern YEAR_PATTERN = Pattern.compile("(?:19|20)\\d{2}");
    private static final List<String> MONTHS = Arrays.asList(
        "January", "February", "March", "April", "May", "June", 
        "July", "August", "September", "October", "November", "December"
    );
    private static final List<String> SEASONS = Arrays.asList(
        "Spring", "Summer", "Fall", "Winter"
    );
    
    // Common job titles
    private static final List<String> JOB_TITLES = Arrays.asList(
        "Accountant", "Administrator", "Advisor", "Agent", "Analyst", "Apprentice", 
        "Architect", "Assistant", "Associate", "Auditor", "Bartender", "Biologist", 
        "Bookkeeper", "Buyer", "Carpenter", "Cashier", "CEO", "Clerk", "Co-op", 
        "Co-Founder", "Consultant", "Coordinator", "CTO", "Developer", "Designer", 
        "Director", "Driver", "Editor", "Electrician", "Engineer", "Extern", "Founder", 
        "Freelancer", "Head", "Intern", "Janitor", "Journalist", "Laborer", "Lawyer", 
        "Lead", "Manager", "Mechanic", "Member", "Nurse", "Officer", "Operator", 
        "Operation", "Photographer", "President", "Producer", "Recruiter", "Representative", 
        "Researcher", "Sales", "Server", "Scientist", "Specialist", "Supervisor", 
        "Teacher", "Technician", "Trader", "Trainee", "Treasurer", "Tutor", "Vice", 
        "VP", "Volunteer", "Webmaster", "Worker"
    );
    
    public ExperienceExtractor(FeatureScoringSystem scoringSystem, SubsectionExtractor subsectionExtractor) {
        this.scoringSystem = scoringSystem;
        this.subsectionExtractor = subsectionExtractor;
    }
    
    /**
     * Extract work experience information from resume sections
     * @param sections Map of section names to ResumeSection objects
     * @return List of Experience objects
     */
    public List<Experience> extractExperience(Map<String, ResumeSection> sections) {
        List<Experience> experiences = new ArrayList<>();
        ResumeSection experienceSection = null;
        
        // Find the experience section
        for (Map.Entry<String, ResumeSection> entry : sections.entrySet()) {
            String sectionName = entry.getKey().toLowerCase();
            if (sectionName.contains("experience") || 
                sectionName.contains("employment") || 
                sectionName.contains("work") || 
                sectionName.contains("history") || 
                sectionName.contains("job")) {
                experienceSection = entry.getValue();
                break;
            }
        }
        
        if (experienceSection == null || experienceSection.getLines() == null || experienceSection.getLines().isEmpty()) {
            return experiences;
        }
        
        // Divide the section into subsections (one per job)
        List<List<ResumeLine>> subsections = subsectionExtractor.divideSectionIntoSubsections(experienceSection.getLines());
        
        for (List<ResumeLine> subsection : subsections) {
            // Get the descriptive lines and bullet points
            int descriptionsLineIdx = subsectionExtractor.getDescriptionsLineIdx(subsection);
            if (descriptionsLineIdx == -1) {
                descriptionsLineIdx = Math.min(2, subsection.size());
            }
            
            // Get header text items
            List<TextItem> headerTextItems = new ArrayList<>();
            for (int i = 0; i < descriptionsLineIdx && i < subsection.size(); i++) {
                headerTextItems.addAll(subsection.get(i).getTextItems());
            }
            
            // Extract job details
            String date = extractDate(headerTextItems);
            String jobTitle = extractJobTitle(headerTextItems);
            String company = extractCompany(headerTextItems, date, jobTitle);
            
            // Extract descriptions (bullet points)
            List<String> descriptions = new ArrayList<>();
            if (descriptionsLineIdx < subsection.size()) {
                List<ResumeLine> descriptionLines = subsection.subList(descriptionsLineIdx, subsection.size());
                descriptions = subsectionExtractor.getBulletPointsFromLines(descriptionLines);
            }
            
            // Parse start and end dates
            String[] dateParts = parseDate(date);
            String startDate = dateParts[0];
            String endDate = dateParts[1];
            
            // Create the experience object
            Experience experience = new Experience();
            experience.setCompany(company);
            experience.setTitle(jobTitle);
            experience.setStartDate(startDate);
            experience.setEndDate(endDate);
            experience.setDescription(String.join("\n", descriptions));
            
            // Add individual responsibility bullet points
            for (String desc : descriptions) {
                experience.addResponsibility(desc);
            }
            
            experiences.add(experience);
        }
        
        return experiences;
    }
    
    /**
     * Extract date using feature scoring
     */
    private String extractDate(List<TextItem> textItems) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        
        // Date features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(YEAR_PATTERN), 2));
        
        // Month features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> MONTHS.stream().anyMatch(month -> 
                textItem.getText().contains(month) || 
                textItem.getText().contains(month.substring(0, 3))),
            2));
        
        // Season features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> SEASONS.stream().anyMatch(season -> 
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
     * Extract job title using feature scoring
     */
    private String extractJobTitle(List<TextItem> textItems) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        
        // Job title features
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> {
                String text = textItem.getText();
                return JOB_TITLES.stream().anyMatch(title -> 
                    Arrays.stream(text.split("\\s+"))
                        .anyMatch(word -> word.equals(title)));
            },
            4));
        
        // Negative features
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.hasNumber(), -4));
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            textItem -> textItem.getText().split("\\s+").length > 5, 
            -2));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets, false);
    }
    
    /**
     * Extract company using feature scoring
     */
    private String extractCompany(List<TextItem> textItems, String date, String jobTitle) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        
        // Company features
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.isBold(), 2));
        
        // Negative features - avoid date and job title
        if (date != null && !date.isEmpty()) {
            featureSets.add(new FeatureScoringSystem.FeatureSet(
                FeatureScoringSystem.containsText(date), -4));
        }
        
        if (jobTitle != null && !jobTitle.isEmpty()) {
            featureSets.add(new FeatureScoringSystem.FeatureSet(
                FeatureScoringSystem.containsText(jobTitle), -4));
        }
        
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