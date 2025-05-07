package com.snapscreen.snapscreen_api.model.resumeparser.attributes;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

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
    private String summary;
    
    private List<String> links;

    // Default constructor
    public Profile() {
        this.links = new ArrayList<>();
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
    
    public List<String> getLinks() {
        return links;
    }
    
    public void setLinks(List<String> links) {
        this.links = links;
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
                ", links=" + String.join(", ", links) +
                ", summary='" + (summary != null ? summary.substring(0, Math.min(summary.length(), 50)) + "..." : null) + '\'' +
                '}';
    }
} 