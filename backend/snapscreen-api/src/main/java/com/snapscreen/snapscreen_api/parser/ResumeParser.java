package com.snapscreen.snapscreen_api.parser;

import com.snapscreen.snapscreen_api.model.resumeparser.ParsedResume;
import com.snapscreen.snapscreen_api.model.resumeparser.ResumeLine;
import com.snapscreen.snapscreen_api.model.resumeparser.ResumeSection;
import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Education;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Experience;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Profile;
import com.snapscreen.snapscreen_api.model.resumeparser.attributes.Skills;
import com.snapscreen.snapscreen_api.parser.extraction.EducationExtractor;
import com.snapscreen.snapscreen_api.parser.extraction.ExperienceExtractor;
import com.snapscreen.snapscreen_api.parser.extraction.LineGrouper;
import com.snapscreen.snapscreen_api.parser.extraction.PdfExtractor;
import com.snapscreen.snapscreen_api.parser.extraction.ProfileExtractor;
import com.snapscreen.snapscreen_api.parser.extraction.SectionGrouper;
import com.snapscreen.snapscreen_api.parser.extraction.SkillsExtractor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main resume parser service that orchestrates the parsing process.
 * This combines all the extraction steps from the open-resume solution
 * to parse a resume PDF and extract structured information.
 */
@Service
public class ResumeParser {

    private final PdfExtractor pdfExtractor;
    private final LineGrouper lineGrouper;
    private final SectionGrouper sectionGrouper;
    private final ProfileExtractor profileExtractor;
    private final ExperienceExtractor experienceExtractor;
    private final EducationExtractor educationExtractor;
    private final SkillsExtractor skillsExtractor;
    
    public ResumeParser(
            PdfExtractor pdfExtractor,
            LineGrouper lineGrouper,
            SectionGrouper sectionGrouper,
            ProfileExtractor profileExtractor,
            ExperienceExtractor experienceExtractor,
            EducationExtractor educationExtractor,
            SkillsExtractor skillsExtractor) {
        this.pdfExtractor = pdfExtractor;
        this.lineGrouper = lineGrouper;
        this.sectionGrouper = sectionGrouper;
        this.profileExtractor = profileExtractor;
        this.experienceExtractor = experienceExtractor;
        this.educationExtractor = educationExtractor;
        this.skillsExtractor = skillsExtractor;
    }
    
    /**
     * Parse a resume from a PDF file
     * @param pdfFile the PDF file to parse
     * @return the parsed resume with structured data
     */
    public ParsedResume parseResume(File pdfFile) throws IOException {
        // Step 1: Extract text items from PDF
        List<TextItem> textItems = pdfExtractor.extractTextFromPdf(pdfFile);
        
        // Continue with common parsing logic
        return parseResumeFromTextItems(textItems);
    }
    
    /**
     * Parse a resume from a PDF input stream
     * @param inputStream the PDF input stream to parse
     * @return the parsed resume with structured data
     */
    public ParsedResume parseResume(InputStream inputStream) throws IOException {
        // Step 1: Extract text items from PDF
        List<TextItem> textItems = pdfExtractor.extractTextFromPdf(inputStream);
        
        // Continue with common parsing logic
        return parseResumeFromTextItems(textItems);
    }
    
    /**
     * Common parsing logic starting from text items
     */
    private ParsedResume parseResumeFromTextItems(List<TextItem> textItems) {
        // Step 2: Group text items into lines
        List<ResumeLine> lines = lineGrouper.groupIntoLines(textItems);
        
        // Step 3: Group lines into sections
        Map<String, ResumeSection> sections = sectionGrouper.groupIntoSections(lines);
        
        // Step 4: Extract structured information from sections
        
        // Create parsed resume object
        ParsedResume parsedResume = new ParsedResume();
        
        // Extract profile data
        Profile profile = profileExtractor.extractProfile(sections);
        parsedResume.setName(profile.getName());
        parsedResume.setEmail(profile.getEmail());
        parsedResume.setPhone(profile.getPhone());
        parsedResume.setLocation(profile.getLocation());
        parsedResume.setWebsite(profile.getWebsite());
        parsedResume.setSummary(profile.getSummary());
        
        // Extract work experience
        List<Experience> experiences = experienceExtractor.extractExperience(sections);
        parsedResume.setExperiences(experiences);
        
        // Extract education
        List<Education> educations = educationExtractor.extractEducation(sections);
        parsedResume.setEducations(educations);
        
        // Extract skills
        Skills skills = skillsExtractor.extractSkills(sections);
        parsedResume.setSkills(skills);
        
        // Extract raw text for reference
        String rawText = lines.stream()
            .map(ResumeLine::getText)
            .collect(Collectors.joining("\n"));
        parsedResume.setRawText(rawText);
        
        return parsedResume;
    }
} 