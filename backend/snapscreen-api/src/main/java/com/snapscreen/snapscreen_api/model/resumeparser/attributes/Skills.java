package com.snapscreen.snapscreen_api.model.resumeparser.attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the skills section of a parsed resume.
 */
public class Skills implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<String> skills = new ArrayList<>();
    private List<String> languages = new ArrayList<>();
    private List<String> certifications = new ArrayList<>();
    
    // Default constructor
    public Skills() {
    }
    
    // Add a skill
    public void addSkill(String skill) {
        this.skills.add(skill);
    }
    
    // Add a language
    public void addLanguage(String language) {
        this.languages.add(language);
    }
    
    // Add a certification
    public void addCertification(String certification) {
        this.certifications.add(certification);
    }
    
    // Getters and setters
    public List<String> getSkills() {
        return skills;
    }
    
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
    
    public List<String> getLanguages() {
        return languages;
    }
    
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
    
    public List<String> getCertifications() {
        return certifications;
    }
    
    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }
    
    @Override
    public String toString() {
        return "Skills{" +
                "skills=[" + String.join(", ", skills) + "]" +
                ", languages=[" + String.join(", ", languages) + "]" +
                ", certifications=[" + String.join(", ", certifications) + "]" +
                '}';
    }
}
