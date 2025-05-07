package com.snapscreen.snapscreen_api.model.resumeparser;

import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Education;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Experience;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a fully parsed resume with structured data.
 * This is the final output of the resume parsing process.
 */
public class ParsedResume implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Personal information
    private String name;
    private String email;
    private String phone;
    private String location;
    private String website;
    private String summary;
    
    // Resume sections
    private List<Experience> experiences = new ArrayList<>();
    private List<Education> educations = new ArrayList<>();
    private List<String> skills = new ArrayList<>();
    private List<Map<String, String>> projects = new ArrayList<>();
    private List<Map<String, String>> certifications = new ArrayList<>();
    
    // Additional parsed sections (for flexibility)
    private Map<String, Object> additionalSections = new HashMap<>();
    
    // Raw text for debugging/reference
    private String rawText;
    
    // Default constructor
    public ParsedResume() {
    }
    
    // Add an experience entry
    public void addExperience(Experience experience) {
        this.experiences.add(experience);
    }
    
    // Add an education entry
    public void addEducation(Education education) {
        this.educations.add(education);
    }
    
    // Add a skill
    public void addSkill(String skill) {
        this.skills.add(skill);
    }
    
    // Add a project
    public void addProject(Map<String, String> project) {
        this.projects.add(project);
    }
    
    // Add a certification
    public void addCertification(Map<String, String> certification) {
        this.certifications.add(certification);
    }
    
    // Add a custom section
    public void addAdditionalSection(String sectionName, Object sectionData) {
        this.additionalSections.put(sectionName, sectionData);
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public List<Experience> getExperiences() {
        return experiences;
    }
    
    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }
    
    public List<Education> getEducations() {
        return educations;
    }
    
    public void setEducations(List<Education> educations) {
        this.educations = educations;
    }
    
    public List<String> getSkills() {
        return skills;
    }
    
    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
    
    public List<Map<String, String>> getProjects() {
        return projects;
    }
    
    public void setProjects(List<Map<String, String>> projects) {
        this.projects = projects;
    }
    
    public List<Map<String, String>> getCertifications() {
        return certifications;
    }
    
    public void setCertifications(List<Map<String, String>> certifications) {
        this.certifications = certifications;
    }
    
    public Map<String, Object> getAdditionalSections() {
        return additionalSections;
    }
    
    public void setAdditionalSections(Map<String, Object> additionalSections) {
        this.additionalSections = additionalSections;
    }
    
    public String getRawText() {
        return rawText;
    }
    
    public void setRawText(String rawText) {
        this.rawText = rawText;
    }
}
