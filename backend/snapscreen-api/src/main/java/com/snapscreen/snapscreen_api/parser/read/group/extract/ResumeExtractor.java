package com.snapscreen.snapscreen_api.parser.read.group.extract;

import com.snapscreen.snapscreen_api.model.resumeparser.*;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Profile;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Education;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Experience;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Skills;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Main orchestrator for the resume extraction process.
 * Coordinates the extraction of different resume components from sections.
 */
@Component
public class ResumeExtractor {
    private final ProfileExtractor profileExtractor;
    private final EducationExtractor educationExtractor;
    private final ExperienceExtractor experienceExtractor;
    private final SkillsExtractor skillsExtractor;

    public ResumeExtractor(
        ProfileExtractor profileExtractor,
        EducationExtractor educationExtractor,
        ExperienceExtractor experienceExtractor,
        SkillsExtractor skillsExtractor
    ) {
        this.profileExtractor = profileExtractor;
        this.educationExtractor = educationExtractor;
        this.experienceExtractor = experienceExtractor;
        this.skillsExtractor = skillsExtractor;
    }

    /**
     * Extract a complete resume from the grouped sections
     * @param sections The grouped sections from the resume
     * @return A complete ParsedResume object with all extracted information
     */
    public ParsedResume extractResumeFromSections(Map<String, ResumeSection> sections) {
        // Extract profile information
        Profile profile = profileExtractor.extractProfile(sections);

        // Extract education history
        List<Education> educations = educationExtractor.extractEducation(sections);

        // Extract work experience
        List<Experience> experiences = experienceExtractor.extractExperience(sections);

        // Extract skills
        Skills skills = skillsExtractor.extractSkills(sections);

        // Create and return the complete ParsedResume
        ParsedResume parsedResume = new ParsedResume();
        if (profile != null) {
            parsedResume.setName(profile.getName());
            parsedResume.setEmail(profile.getEmail());
            parsedResume.setPhone(profile.getPhone());
            parsedResume.setLocation(profile.getLocation());
            parsedResume.setWebsite(profile.getWebsite());
            parsedResume.setSummary(profile.getSummary());
            if (profile.getLinks() != null) {
                parsedResume.getSkills().getCertifications().addAll(profile.getLinks()); // Optionally store links
            }
        }
        parsedResume.setEducations(educations);
        parsedResume.setExperiences(experiences);
        parsedResume.setSkills(skills);
        // Optionally: set rawText, projects, certifications, etc. if available
        return parsedResume;
    }
}
