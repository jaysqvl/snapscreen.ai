package com.snapscreen.snapscreen_api.parser.read.group;

import com.snapscreen.snapscreen_api.model.resumeparser.ResumeLine;
import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Groups TextItems into lines based on their vertical position.
 * This is equivalent to step 2 of the open-resume solution.
 */
@Component
public class LineGrouper {

    /**
     * Groups TextItems into ResumeLine objects
     * @param textItems List of TextItem objects extracted from the PDF
     * @return List of ResumeLine objects
     */
    public List<ResumeLine> groupIntoLines(List<TextItem> textItems) {
        List<ResumeLine> lines = new ArrayList<>();
        List<TextItem> currentLine = new ArrayList<>();
        
        // Make sure textItems are sorted properly (top to bottom, left to right)
        textItems.sort((a, b) -> {
            if (Math.abs(a.getY() - b.getY()) < 2.0f) {
                return Float.compare(a.getX1(), b.getX1());
            }
            return Float.compare(b.getY(), a.getY());  // Reverse because PDF coordinates start from bottom
        });
        
        for (TextItem item : textItems) {
            if (item.isNewLine() && !currentLine.isEmpty()) {
                // Create a new line and add it to lines
                ResumeLine line = createResumeLine(currentLine);
                lines.add(line);
                currentLine = new ArrayList<>();
            }
            
            currentLine.add(item);
        }
        
        // Don't forget the last line
        if (!currentLine.isEmpty()) {
            ResumeLine line = createResumeLine(currentLine);
            lines.add(line);
        }
        
        return lines;
    }
    
    private ResumeLine createResumeLine(List<TextItem> lineItems) {
        // Sort the items by X position to ensure correct order
        lineItems.sort((a, b) -> Float.compare(a.getX1(), b.getX1()));
        
        // Determine line properties
        float y = lineItems.get(0).getY();
        String text = lineItems.stream()
            .map(TextItem::getText)
            .reduce("", (a, b) -> a + (a.isEmpty() || b.isEmpty() ? "" : " ") + b);
        
        ResumeLine line = new ResumeLine();
        line.setY(y);
        line.setText(text);
        line.setTextItems(new ArrayList<>(lineItems));
        
        return line;
    }
}
