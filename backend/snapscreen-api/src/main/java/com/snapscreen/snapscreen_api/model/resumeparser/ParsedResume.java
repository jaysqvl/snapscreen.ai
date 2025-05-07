package com.snapscreen.snapscreen_api.model.resumeparser;

import java.util.List;
import java.util.ArrayList;

public class ParsedResume {
    private List<ResumeSection> sections;

    // Default constructor
    public ParsedResume() {
        this.sections = new ArrayList<>();
    }

    public ParsedResume(List<ResumeSection> sections) {
        this.sections = new ArrayList<>();
    }

    public void addSection(ResumeSection section) {
    }

    public void setSections(List<ResumeSection> sections) {
        this.sections = sections;
    }

    public List<ResumeSection> getSections() {
        return sections;
    }
}
