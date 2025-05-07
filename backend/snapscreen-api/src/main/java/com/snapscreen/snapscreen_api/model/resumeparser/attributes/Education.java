package com.snapscreen.snapscreen_api.model.resumeparser.attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an education entry in a resume.
 * Contains details about a school, degree, dates, etc.
 */
public class Education implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String school;
    private String degree;
    private String fieldOfStudy;
    private String startDate;
    private String endDate;
    private String gpa;
    private String location;
    private List<String> activities;
    private List<String> descriptions;
    
    // Default constructor
    public Education() {
        this.activities = new ArrayList<>();
        this.descriptions = new ArrayList<>();
    }
    
    // Getters and setters
    public String getSchool() {
        return school;
    }
    
    public void setSchool(String school) {
        this.school = school;
    }
    
    public String getDegree() {
        return degree;
    }
    
    public void setDegree(String degree) {
        this.degree = degree;
    }
    
    public String getFieldOfStudy() {
        return fieldOfStudy;
    }
    
    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    public String getGpa() {
        return gpa;
    }
    
    public void setGpa(String gpa) {
        this.gpa = gpa;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public List<String> getActivities() {
        return activities;
    }
    
    public void setActivities(List<String> activities) {
        this.activities = activities;
    }
    
    public void addActivity(String activity) {
        this.activities.add(activity);
    }
    
    public List<String> getDescriptions() {
        return descriptions;
    }
    
    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }
    
    public void addDescription(String description) {
        this.descriptions.add(description);
    }
    
    @Override
    public String toString() {
        return "Education{" +
                "school='" + school + '\'' +
                ", degree='" + degree + '\'' +
                ", fieldOfStudy='" + fieldOfStudy + '\'' +
                ", period='" + startDate + " - " + endDate + '\'' +
                ", gpa='" + gpa + '\'' +
                '}';
    }
}
