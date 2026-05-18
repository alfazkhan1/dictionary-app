package com.dictionary.repository;

import com.dictionary.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByName(String name);

    @Query("SELECT s FROM Subject s LEFT JOIN FETCH s.words ORDER BY s.name")
    List<Subject> findAllWithWordCount();

    @Query("SELECT s.name FROM Subject s")
    List<String> findAllNames();
}