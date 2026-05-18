package com.dictionary.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {
    private Long id;
    private String englishWord;
    private String urduMeaning;
    private String englishMeaning;
    private String exampleSentence;
    private String partOfSpeech;
    private String pronunciation;
    private String synonyms;
    private String antonyms;
    private Long subjectId;
    private String subjectName;
    private LocalDateTime createdAt;
}