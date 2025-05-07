package com.snapscreen.snapscreen_api.model.resumeparser.attributes;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public class Skills implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<String> skills;
    
    // Default constructor
    public Skills() {
        this.skills = new ArrayList<>();
    }

    // Getters and Setters
    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public void addSkill(String skill) {
        skills.add(skill);
    }

    public void removeSkill(String skill) {
        skills.remove(skill);
    }

    public void clearSkills() {
        skills.clear();
    }

    @Override
    public String toString() {
        String skillsString = String.join(", ", skills);
        return "Skills{" +
                "skills=" + skillsString +
                '}';
    }
}
