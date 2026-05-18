package com.dictionary.dto;
import lombok.Data;
@Data
public class WordRequest {
    private String englishWord;
    private String urduMeaning;
    private String englishMeaning;
    private String exampleSentence;
    private String partOfSpeech;
    private String pronunciation;
    private String synonyms;
    private String antonyms;
    private Long subjectId;
}