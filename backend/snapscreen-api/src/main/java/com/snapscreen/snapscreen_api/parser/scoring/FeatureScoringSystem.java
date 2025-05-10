package com.snapscreen.snapscreen_api.parser.scoring;

import com.snapscreen.snapscreen_api.model.resumeparser.TextItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Core utility for the feature scoring system.
 * This implements the feature scoring approach from the open-resume solution to 
 * extract resume information based on feature matching.
 */
@Component
public class FeatureScoringSystem {

    /**
     * Represents a feature set used for scoring
     */
    public static class FeatureSet {
        private final Function<TextItem, Boolean> featureFunction;
        private final int score;
        private final boolean isExactMatch;
        
        public FeatureSet(Function<TextItem, Boolean> featureFunction, int score) {
            this(featureFunction, score, false);
        }
        
        public FeatureSet(Function<TextItem, Boolean> featureFunction, int score, boolean isExactMatch) {
            this.featureFunction = featureFunction;
            this.score = score;
            this.isExactMatch = isExactMatch;
        }
        
        public Function<TextItem, Boolean> getFeatureFunction() {
            return featureFunction;
        }
        
        public int getScore() {
            return score;
        }
        
        public boolean isExactMatch() {
            return isExactMatch;
        }
    }
    
    /**
     * Represents a scored text item
     */
    public static class TextScore {
        private final String text;
        private final int score;
        private final boolean isMatch;
        
        public TextScore(String text, int score, boolean isMatch) {
            this.text = text;
            this.score = score;
            this.isMatch = isMatch;
        }
        
        public String getText() {
            return text;
        }
        
        public int getScore() {
            return score;
        }
        
        public boolean isMatch() {
            return isMatch;
        }
    }
    
    /**
     * Compute feature scores for a list of text items
     */
    private List<TextScore> computeFeatureScores(List<TextItem> textItems, List<FeatureSet> featureSets) {
        List<TextScore> textScores = textItems.stream()
            .map(item -> new TextScore(item.getText(), 0, false))
            .collect(Collectors.toList());
        
        for (int i = 0; i < textItems.size(); i++) {
            TextItem textItem = textItems.get(i);
            
            for (FeatureSet featureSet : featureSets) {
                boolean hasFeature = featureSet.getFeatureFunction().apply(textItem);
                
                if (hasFeature) {
                    TextScore textScore = textScores.get(i);
                    textScores.set(i, new TextScore(
                        textScore.getText(),
                        textScore.getScore() + featureSet.getScore(),
                        featureSet.isExactMatch() || textScore.isMatch()
                    ));
                }
            }
        }
        
        return textScores;
    }
    
    /**
     * Get the text item with the highest feature score
     */
    public String getTextWithHighestFeatureScore(List<TextItem> textItems, List<FeatureSet> featureSets) {
        return getTextWithHighestFeatureScore(textItems, featureSets, true);
    }
    
    /**
     * Get the text item with the highest feature score
     * @param textItems The list of text items to score
     * @param featureSets The feature sets to use for scoring
     * @param returnEmptyIfHighestScoreNotPositive Whether to return empty string if highest score is not positive
     * @return The text item with the highest score
     */
    public String getTextWithHighestFeatureScore(
            List<TextItem> textItems, 
            List<FeatureSet> featureSets,
            boolean returnEmptyIfHighestScoreNotPositive) {
        
        if (textItems.isEmpty()) {
            return "";
        }
        
        List<TextScore> textScores = computeFeatureScores(textItems, featureSets);
        
        TextScore highestScore = textScores.stream()
            .max(Comparator.comparing(TextScore::getScore))
            .orElse(new TextScore("", 0, false));
        
        if (returnEmptyIfHighestScoreNotPositive && highestScore.getScore() <= 0) {
            return "";
        }
        
        return highestScore.getText();
    }
    
    /**
     * Common feature function: checks if text item contains a regex pattern
     */
    public static Function<TextItem, Boolean> matchesPattern(Pattern pattern) {
        return textItem -> {
            Matcher matcher = pattern.matcher(textItem.getText());
            return matcher.find();
        };
    }
    
    /**
     * Common feature function: checks if text item contains exact text
     */
    public static Function<TextItem, Boolean> containsText(String text) {
        return textItem -> textItem.getText().contains(text);
    }
    
    /**
     * Common feature function: checks if text item is bold
     */
    public static Function<TextItem, Boolean> isBold() {
        return TextItem::isBold;
    }
    
    /**
     * Common feature function: checks if text item has letter characters
     */
    public static Function<TextItem, Boolean> hasLetter() {
        return textItem -> textItem.getText().matches(".*[a-zA-Z].*");
    }
    
    /**
     * Common feature function: checks if text item has numbers
     */
    public static Function<TextItem, Boolean> hasNumber() {
        return textItem -> textItem.getText().matches(".*[0-9].*");
    }
    
    /**
     * Common feature function: checks if text item has commas
     */
    public static Function<TextItem, Boolean> hasComma() {
        return textItem -> textItem.getText().contains(",");
    }
    
    /**
     * Common feature function: checks if text is all uppercase
     */
    public static Function<TextItem, Boolean> isAllUppercase() {
        return textItem -> {
            String text = textItem.getText();
            return !text.equals(text.toLowerCase()) && text.equals(text.toUpperCase());
        };
    }
} 