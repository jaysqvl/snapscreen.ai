package com.snapscreen.snapscreen_api.parser.read.group.extract.scoring;

import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Common feature functions used across different extractors.
 * These functions help identify specific characteristics in text items.
 */
@Component
public class CommonFeatures {
    private static final List<String> MONTHS = Arrays.asList(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    );
    
    private static final List<String> SEASONS = Arrays.asList(
        "Summer", "Fall", "Spring", "Winter"
    );

    /**
     * Check if text item is bold based on font name
     */
    public static Function<TextItem, Boolean> isBold() {
        return TextItem::isBold;
    }

    /**
     * Check if text item contains letters
     */
    public static Function<TextItem, Boolean> hasLetter() {
        return item -> Pattern.compile("[a-zA-Z]").matcher(item.getText()).find();
    }

    /**
     * Check if text item contains numbers
     */
    public static Function<TextItem, Boolean> hasNumber() {
        return item -> Pattern.compile("[0-9]").matcher(item.getText()).find();
    }

    /**
     * Check if text item contains commas
     */
    public static Function<TextItem, Boolean> hasComma() {
        return item -> item.getText().contains(",");
    }

    /**
     * Check if text item contains specific text
     */
    public static Function<TextItem, Boolean> containsText(String text) {
        return item -> item.getText().contains(text);
    }

    /**
     * Check if text item contains only letters, spaces, and ampersands
     */
    public static Function<TextItem, Boolean> hasOnlyLettersSpacesAmpersands() {
        return item -> Pattern.compile("^[A-Za-z\\s&]+$").matcher(item.getText()).matches();
    }

    /**
     * Check if text item has letters and is all uppercase
     */
    public static Function<TextItem, Boolean> hasLetterAndIsAllUpperCase() {
        return item -> {
            String text = item.getText();
            return hasLetter().apply(item) && text.equals(text.toUpperCase());
        };
    }

    /**
     * Check if text item contains a year (19xx or 20xx)
     */
    public static Function<TextItem, Boolean> hasYear() {
        return item -> Pattern.compile("(?:19|20)\\d{2}").matcher(item.getText()).find();
    }

    /**
     * Check if text item contains a month name
     */
    public static Function<TextItem, Boolean> hasMonth() {
        return item -> MONTHS.stream().anyMatch(month -> 
            item.getText().contains(month) || 
            item.getText().contains(month.substring(0, 4))
        );
    }

    /**
     * Check if text item contains a season name
     */
    public static Function<TextItem, Boolean> hasSeason() {
        return item -> SEASONS.stream().anyMatch(season -> 
            item.getText().contains(season)
        );
    }

    /**
     * Check if text item contains "Present"
     */
    public static Function<TextItem, Boolean> hasPresent() {
        return item -> item.getText().contains("Present");
    }
} 