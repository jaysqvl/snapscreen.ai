package com.snapscreen.snapscreen_api.model.resumeparser.attributes;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Experience implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String company;
    private String title;
    private String location;
    private String startDate;
    private String endDate;
    private String description;
    
    private List<String> responsibilities;

    // Default constructor
    public Experience() {
        this.responsibilities = new ArrayList<>();
    }

    // Getters and Setters
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getResponsibilities() {
        return responsibilities;
    }
    
    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities;
    }
    
    public void addResponsibility(String responsibility) {
        this.responsibilities.add(responsibility);
    }
    
    @Override
    public String toString() {
        return "Experience{" +
                "company='" + company + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", period='" + startDate + " - " + endDate + '\'' +
                ", responsibilities=" + String.join(", ", responsibilities) +
                '}';
    }
}
