package com.snapscreen.snapscreen_api.model.resumeparser.attributes;

import java.io.Serializable;

public class Education implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String school;
    private String degree;
    private String fieldOfStudy;

    // Default constructor
    public Education() {
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "Education{" +
                "school='" + school + '\'' +
                ", degree='" + degree + '\'' +
                ", fieldOfStudy='" + fieldOfStudy + '\'' +
                '}';
    }
}
