package com.snapscreen.snapscreen_api.parser.extraction;

import com.snapscreen.snapscreen_api.model.resumeparser.ResumeLine;
import com.snapscreen.snapscreen_api.model.resumeparser.ResumeSection;
import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Skills;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Extracts skills information from resume sections.
 * Adapts the extract-skills.ts functionality from the open-resume solution.
 */
@Component
public class SkillsExtractor {

    private final SubsectionExtractor subsectionExtractor;
    
    // Common skill keywords to identify technical skills
    private static final List<String> TECHNICAL_SKILL_KEYWORDS = Arrays.asList(
        "programming", "languages", "technical", "technologies", "tools", "frameworks", "software"
    );
    
    // Common language keywords to identify language skills
    private static final List<String> LANGUAGE_SKILL_KEYWORDS = Arrays.asList(
        "language", "languages", "fluent", "native", "bilingual", "multilingual"
    );
    
    // Common certification keywords to identify certifications
    private static final List<String> CERTIFICATION_KEYWORDS = Arrays.asList(
        "certification", "certificate", "certified", "license", "accredited"
    );
    
    @Autowired
    public SkillsExtractor(SubsectionExtractor subsectionExtractor) {
        this.subsectionExtractor = subsectionExtractor;
    }
    
    /**
     * Extract skills information from resume sections
     * @param sections Map of section names to ResumeSection objects
     * @return Skills object containing skill categories
     */
    public Skills extractSkills(Map<String, ResumeSection> sections) {
        Skills skills = new Skills();
        
        // Find skills section
        ResumeSection skillsSection = null;
        for (Map.Entry<String, ResumeSection> entry : sections.entrySet()) {
            String sectionName = entry.getKey().toLowerCase();
            if (sectionName.contains("skill") || 
                sectionName.contains("technical") || 
                sectionName.contains("technologies") ||
                sectionName.contains("competencies")) {
                skillsSection = entry.getValue();
                break;
            }
        }
        
        if (skillsSection != null && skillsSection.getLines() != null && !skillsSection.getLines().isEmpty()) {
            extractSkillsFromSection(skills, skillsSection);
        }
        
        // Find languages section
        ResumeSection languagesSection = null;
        for (Map.Entry<String, ResumeSection> entry : sections.entrySet()) {
            String sectionName = entry.getKey().toLowerCase();
            if (sectionName.contains("language") && !sectionName.contains("programming")) {
                languagesSection = entry.getValue();
                break;
            }
        }
        
        if (languagesSection != null && languagesSection.getLines() != null && !languagesSection.getLines().isEmpty()) {
            extractLanguagesFromSection(skills, languagesSection);
        }
        
        // Find certifications section
        ResumeSection certificationsSection = null;
        for (Map.Entry<String, ResumeSection> entry : sections.entrySet()) {
            String sectionName = entry.getKey().toLowerCase();
            if (sectionName.contains("certification") || sectionName.contains("certificate")) {
                certificationsSection = entry.getValue();
                break;
            }
        }
        
        if (certificationsSection != null && certificationsSection.getLines() != null && !certificationsSection.getLines().isEmpty()) {
            extractCertificationsFromSection(skills, certificationsSection);
        }
        
        return skills;
    }
    
    /**
     * Extract skills from a skills section
     */
    private void extractSkillsFromSection(Skills skills, ResumeSection section) {
        List<String> skillsList = new ArrayList<>();
        
        // Check for bullet points and descriptions
        int descriptionsLineIdx = subsectionExtractor.getDescriptionsLineIdx(section.getLines());
        if (descriptionsLineIdx != -1) {
            List<ResumeLine> descriptionLines = section.getLines().subList(descriptionsLineIdx, section.getLines().size());
            skillsList = subsectionExtractor.getBulletPointsFromLines(descriptionLines);
        } else {
            // If no bullet points, try to extract skills from the text
            skillsList = extractSkillsFromText(section.getLines());
        }
        
        // Clean up skills
        Set<String> processedSkills = new HashSet<>();
        for (String skill : skillsList) {
            // Split by commas, semicolons, and similar separators
            String[] parts = skill.split("[,;|/]");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty() && trimmed.length() > 1) {
                    processedSkills.add(trimmed);
                }
            }
        }
        
        // Add skills to the Skills object
        for (String skill : processedSkills) {
            if (isTechnicalSkill(skill)) {
                skills.addSkill(skill);
            } else if (isLanguage(skill)) {
                skills.addLanguage(skill);
            } else if (isCertification(skill)) {
                skills.addCertification(skill);
            } else {
                // Default to regular skill
                skills.addSkill(skill);
            }
        }
    }
    
    /**
     * Extract language skills from a languages section
     */
    private void extractLanguagesFromSection(Skills skills, ResumeSection section) {
        List<String> languagesList = new ArrayList<>();
        
        // Check for bullet points
        int descriptionsLineIdx = subsectionExtractor.getDescriptionsLineIdx(section.getLines());
        if (descriptionsLineIdx != -1) {
            List<ResumeLine> descriptionLines = section.getLines().subList(descriptionsLineIdx, section.getLines().size());
            languagesList = subsectionExtractor.getBulletPointsFromLines(descriptionLines);
        } else {
            // If no bullet points, try to extract skills from the text
            languagesList = extractSkillsFromText(section.getLines());
        }
        
        // Clean up and add languages
        for (String language : languagesList) {
            // Split by commas, semicolons, and similar separators
            String[] parts = language.split("[,;|/]");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty() && trimmed.length() > 1) {
                    // Extract just the language name if proficiency level is included
                    String[] langParts = trimmed.split("[:-]");
                    skills.addLanguage(langParts[0].trim());
                }
            }
        }
    }
    
    /**
     * Extract certifications from a certifications section
     */
    private void extractCertificationsFromSection(Skills skills, ResumeSection section) {
        List<String> certificationsList = new ArrayList<>();
        
        // Check for bullet points
        int descriptionsLineIdx = subsectionExtractor.getDescriptionsLineIdx(section.getLines());
        if (descriptionsLineIdx != -1) {
            List<ResumeLine> descriptionLines = section.getLines().subList(descriptionsLineIdx, section.getLines().size());
            certificationsList = subsectionExtractor.getBulletPointsFromLines(descriptionLines);
        } else {
            // If no bullet points, try to extract from the text
            certificationsList = extractSkillsFromText(section.getLines());
        }
        
        // Add certifications
        for (String certification : certificationsList) {
            skills.addCertification(certification.trim());
        }
    }
    
    /**
     * Extract skills from text without bullet points
     */
    private List<String> extractSkillsFromText(List<ResumeLine> lines) {
        // Join lines and split by common delimiters
        String text = lines.stream()
            .map(line -> {
                StringBuilder sb = new StringBuilder();
                for (TextItem item : line.getTextItems()) {
                    sb.append(item.getText()).append(" ");
                }
                return sb.toString().trim();
            })
            .collect(Collectors.joining(" "));
        
        // Split by common skill separators
        String[] parts = text.split("\\s*[,;|/]\\s*|\\s+and\\s+|\\s+\\+\\s+");
        return Arrays.stream(parts)
            .map(String::trim)
            .filter(s -> !s.isEmpty() && s.length() > 1)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if a skill is a technical skill
     */
    private boolean isTechnicalSkill(String skill) {
        // Check common programming languages and technologies
        List<String> techKeywords = Arrays.asList(
            "java", "python", "javascript", "typescript", "c++", "c#", "ruby", "php",
            "html", "css", "sql", "nosql", "react", "angular", "vue", "node",
            "express", "django", "spring", "aws", "azure", "gcp", "docker", "kubernetes",
            "linux", "unix", "git", "github", "agile", "scrum", "rest", "api",
            "database", "mongodb", "mysql", "postgresql", "oracle", "algorithm"
        );
        
        String lowerSkill = skill.toLowerCase();
        return techKeywords.stream().anyMatch(lowerSkill::contains);
    }
    
    /**
     * Check if a skill is a language
     */
    private boolean isLanguage(String skill) {
        // Common languages
        List<String> languages = Arrays.asList(
            "english", "spanish", "french", "german", "italian", "chinese", "japanese",
            "korean", "russian", "arabic", "hindi", "portuguese", "dutch", "swedish",
            "danish", "finnish", "norwegian", "greek", "turkish", "polish", "czech",
            "vietnamese", "thai", "indonesian", "malay", "tagalog", "hebrew"
        );
        
        String lowerSkill = skill.toLowerCase();
        return languages.stream().anyMatch(lowerSkill::contains) ||
               LANGUAGE_SKILL_KEYWORDS.stream().anyMatch(lowerSkill::contains);
    }
    
    /**
     * Check if a skill is a certification
     */
    private boolean isCertification(String skill) {
        String lowerSkill = skill.toLowerCase();
        return CERTIFICATION_KEYWORDS.stream().anyMatch(lowerSkill::contains) ||
               lowerSkill.contains("certified") ||
               Pattern.compile("\\b[a-z]{2,5}\\b", Pattern.CASE_INSENSITIVE).matcher(lowerSkill).find();
    }
} 