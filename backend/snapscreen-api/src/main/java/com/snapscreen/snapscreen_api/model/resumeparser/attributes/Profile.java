package com.snapscreen.snapscreen_api.model.resumeparser.attributes;

import java.io.Serializable;

/**
 * Represents the personal/profile information section of a parsed resume.
 * This contains basic identification and contact details of the candidate.
 */
public class Profile implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String email;
    private String phone;
    private String location;
    private String website;
    private String linkedInUrl;
    private String summary;
    
    // Default constructor
    public Profile() {
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
    
    public String getLinkedInUrl() {
        return linkedInUrl;
    }
    
    public void setLinkedInUrl(String linkedInUrl) {
        this.linkedInUrl = linkedInUrl;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", website='" + website + '\'' +
                ", linkedInUrl='" + linkedInUrl + '\'' +
                ", summary='" + (summary != null ? summary.substring(0, Math.min(summary.length(), 50)) + "..." : null) + '\'' +
                '}';
    }
} 