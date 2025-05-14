package com.snapscreen.snapscreen_api.parser.read.group.extract.scoring;

import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Handles bullet points in text items and groups them into bullet point lists.
 */
@Component
public class BulletPointHandler {
    private static final Pattern BULLET_POINT_PATTERN = Pattern.compile("^[•●○◆◇■□▪▫]");
    private static final Pattern NUMBERED_PATTERN = Pattern.compile("^\\d+[.)]");

    /**
     * Check if a text item is a bullet point
     */
    public boolean isBulletPoint(TextItem item) {
        String text = item.getText().trim();
        return BULLET_POINT_PATTERN.matcher(text).find() || 
               NUMBERED_PATTERN.matcher(text).find();
    }

    /**
     * Extract the content of a bullet point (remove the bullet point marker)
     */
    public String extractBulletPointContent(TextItem item) {
        String text = item.getText().trim();
        // Remove bullet point marker
        text = BULLET_POINT_PATTERN.matcher(text).replaceFirst("").trim();
        // Remove numbered list marker
        text = NUMBERED_PATTERN.matcher(text).replaceFirst("").trim();
        return text;
    }

    /**
     * Group text items into bullet point lists
     */
    public List<List<String>> groupBulletPoints(List<TextItem> textItems) {
        List<List<String>> bulletPointGroups = new ArrayList<>();
        List<String> currentGroup = new ArrayList<>();
        boolean inBulletPointGroup = false;

        for (TextItem item : textItems) {
            if (isBulletPoint(item)) {
                if (!inBulletPointGroup) {
                    inBulletPointGroup = true;
                    currentGroup = new ArrayList<>();
                    bulletPointGroups.add(currentGroup);
                }
                currentGroup.add(extractBulletPointContent(item));
            } else {
                inBulletPointGroup = false;
            }
        }

        return bulletPointGroups;
    }
} 