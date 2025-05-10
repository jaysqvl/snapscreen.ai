package com.snapscreen.snapscreen_api.parser.extraction;

import com.snapscreen.snapscreen_api.model.resumeparser.ResumeSection;
import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Profile;
import com.snapscreen.snapscreen_api.parser.scoring.FeatureScoringSystem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Extracts profile information from resume sections.
 * Adapts the extract-profile.ts functionality from the open-resume solution.
 */
@Component
public class ProfileExtractor {

    private final FeatureScoringSystem scoringSystem;
    
    // Regex patterns for profile information
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\S+@\\S+\\.\\S+");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\(?\\d{3}\\)?[\\s-]?\\d{3}[\\s-]?\\d{4}");
    private static final Pattern CITY_STATE_PATTERN = Pattern.compile("[A-Z][a-zA-Z\\s]+, [A-Z]{2}");
    private static final Pattern URL_PATTERN = Pattern.compile("\\S+\\.[a-z]+\\/\\S+");
    private static final Pattern URL_HTTP_PATTERN = Pattern.compile("https?:\\/\\/\\S+\\.\\S+");
    private static final Pattern URL_WWW_PATTERN = Pattern.compile("www\\.\\S+\\.\\S+");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s\\.]+$");
    
    public ProfileExtractor(FeatureScoringSystem scoringSystem) {
        this.scoringSystem = scoringSystem;
    }
    
    /**
     * Extract profile information from resume sections
     * @param sections Map of section names to ResumeSection objects
     * @return Extracted Profile object
     */
    public Profile extractProfile(Map<String, ResumeSection> sections) {
        ResumeSection profileSection = sections.getOrDefault("profile", null);
        
        if (profileSection == null || profileSection.getLines() == null || profileSection.getLines().isEmpty()) {
            return new Profile();
        }
        
        // Get all text items from the profile section
        List<TextItem> textItems = profileSection.getLines().stream()
            .flatMap(line -> line.getTextItems().stream())
            .collect(Collectors.toList());
        
        // Extract profile information using feature scoring
        String name = extractName(textItems);
        String email = extractEmail(textItems);
        String phone = extractPhone(textItems);
        String location = extractLocation(textItems);
        String url = extractUrl(textItems);
        String summary = extractSummary(textItems);
        
        // Check for dedicated summary section
        ResumeSection summarySection = sections.getOrDefault("summary", null);
        if (summarySection != null && !summarySection.getLines().isEmpty()) {
            summary = summarySection.getLines().stream()
                .map(line -> line.getText())
                .collect(Collectors.joining(" "));
        }
        
        // Check for objective section as fallback for summary
        ResumeSection objectiveSection = sections.getOrDefault("objective", null);
        if ((summary == null || summary.isEmpty()) && 
            objectiveSection != null && !objectiveSection.getLines().isEmpty()) {
            summary = objectiveSection.getLines().stream()
                .map(line -> line.getText())
                .collect(Collectors.joining(" "));
        }
        
        // Create and return profile
        Profile profile = new Profile();
        profile.setName(name);
        profile.setEmail(email);
        profile.setPhone(phone);
        profile.setLocation(location);
        profile.setWebsite(url);
        profile.setSummary(summary);
        
        return profile;
    }
    
    /**
     * Extract name using feature scoring
     */
    private String extractName(List<TextItem> textItems) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(NAME_PATTERN), 3, true));
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.isBold(), 2));
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.isAllUppercase(), 2));
        
        // Negative features to avoid mismatches
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(EMAIL_PATTERN), -4));
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.hasNumber(), -4));
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.hasComma(), -4));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets);
    }
    
    /**
     * Extract email using feature scoring
     */
    private String extractEmail(List<TextItem> textItems) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(EMAIL_PATTERN), 4, true));
        
        // Negative features
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.isBold(), -1));
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.hasComma(), -2));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets);
    }
    
    /**
     * Extract phone using feature scoring
     */
    private String extractPhone(List<TextItem> textItems) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(PHONE_PATTERN), 4, true));
        
        // Negative features
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.hasLetter(), -4));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets);
    }
    
    /**
     * Extract location using feature scoring
     */
    private String extractLocation(List<TextItem> textItems) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(CITY_STATE_PATTERN), 4, true));
        
        // Negative features
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.isBold(), -1));
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(EMAIL_PATTERN), -4));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets);
    }
    
    /**
     * Extract URL using feature scoring
     */
    private String extractUrl(List<TextItem> textItems) {
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(URL_PATTERN), 4, true));
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(URL_HTTP_PATTERN), 3, true));
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(URL_WWW_PATTERN), 3, true));
        
        // Negative features
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.isBold(), -1));
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(EMAIL_PATTERN), -4));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets);
    }
    
    /**
     * Extract summary using simple heuristics
     */
    private String extractSummary(List<TextItem> textItems) {
        // Define a feature function for text with 4+ words
        FeatureScoringSystem.FeatureSet has4OrMoreWords = new FeatureScoringSystem.FeatureSet(
            textItem -> textItem.getText().split("\\s+").length >= 4, 4);
        
        List<FeatureScoringSystem.FeatureSet> featureSets = new ArrayList<>();
        featureSets.add(has4OrMoreWords);
        
        // Negative features
        featureSets.add(new FeatureScoringSystem.FeatureSet(FeatureScoringSystem.isBold(), -1));
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(EMAIL_PATTERN), -4));
        featureSets.add(new FeatureScoringSystem.FeatureSet(
            FeatureScoringSystem.matchesPattern(CITY_STATE_PATTERN), -4, false));
        
        return scoringSystem.getTextWithHighestFeatureScore(textItems, featureSets);
    }
} 