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
public class SubjectDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String color;
    private long wordCount;
    private LocalDateTime createdAt;
}