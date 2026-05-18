package com.dictionary.service;

import com.dictionary.dto.SubjectDTO;
import com.dictionary.dto.SubjectRequest;
import com.dictionary.model.Subject;
import com.dictionary.repository.SubjectRepository;
import com.dictionary.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private WordRepository wordRepository;

    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SubjectDTO getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));
        return toDTO(subject);
    }

    public SubjectDTO createSubject(SubjectRequest request) {
        Subject subject = Subject.builder()
                .name(request.getName())
                .description(request.getDescription())
                .icon(request.getIcon())
                .color(request.getColor())
                .build();
        return toDTO(subjectRepository.save(subject));
    }

    public SubjectDTO updateSubject(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));
        subject.setName(request.getName());
        subject.setDescription(request.getDescription());
        subject.setIcon(request.getIcon());
        subject.setColor(request.getColor());
        return toDTO(subjectRepository.save(subject));
    }

    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Subject not found: " + id);
        }
        subjectRepository.deleteById(id);
    }

    public SubjectDTO toDTO(Subject subject) {
        long wordCount = wordRepository.countBySubjectId(subject.getId());
        return SubjectDTO.builder()
                .id(subject.getId())
                .name(subject.getName())
                .description(subject.getDescription())
                .icon(subject.getIcon())
                .color(subject.getColor())
                .wordCount(wordCount)
                .createdAt(subject.getCreatedAt())
                .build();
    }

    public Subject findById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));
    }
}