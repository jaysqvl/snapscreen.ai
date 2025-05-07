package com.snapscreen.snapscreen_api.model.resumeparser.attributes;

import java.io.Serializable;

public class Projects implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    
    // Default constructor
    public Projects() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Projects{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
