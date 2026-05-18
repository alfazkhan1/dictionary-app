//package com.dictionary.repository;
//
//import com.dictionary.model.Word;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface WordRepository extends JpaRepository<Word, Long> {
//
//    // Find by subject (paginated)
//    Page<Word> findBySubjectId(Long subjectId, Pageable pageable);
//
//    // Count by subject
//    long countBySubjectId(Long subjectId);
//
//    // Global search - English or Urdu
//    @Query("SELECT w FROM Word w WHERE " +
//            "LOWER(w.englishWord) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "w.urduMeaning LIKE CONCAT('%', :keyword, '%') OR " +
//            "LOWER(w.englishMeaning) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    Page<Word> globalSearch(@Param("keyword") String keyword, Pageable pageable);
//
//    // Subject-wise search
//    @Query("SELECT w FROM Word w WHERE w.subject.id = :subjectId AND (" +
//            "LOWER(w.englishWord) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "w.urduMeaning LIKE CONCAT('%', :keyword, '%') OR " +
//            "LOWER(w.englishMeaning) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//    Page<Word> searchBySubject(@Param("subjectId") Long subjectId,
//                               @Param("keyword") String keyword,
//                               Pageable pageable);
//
//    // All words for export
//    @Query("SELECT w FROM Word w JOIN FETCH w.subject ORDER BY w.subject.name, w.englishWord")
//    List<Word> findAllForExport();
//
//    // Words by subject for export
//    List<Word> findBySubjectIdOrderByEnglishWord(Long subjectId);
//
//    // Total word count
//    @Query("SELECT COUNT(w) FROM Word w")
//    long countAllWords();
//}


package com.dictionary.repository;

import com.dictionary.model.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    // Find by subject (paginated)
    Page<Word> findBySubjectId(Long subjectId, Pageable pageable);

    // Count by subject
    long countBySubjectId(Long subjectId);

    // Global search - English or Urdu
    @Query("SELECT w FROM Word w WHERE " +
            "LOWER(w.englishWord) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "w.urduMeaning LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(w.englishMeaning) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Word> globalSearch(@Param("keyword") String keyword, Pageable pageable);

    // Subject-wise search
    @Query("SELECT w FROM Word w WHERE w.subject.id = :subjectId AND (" +
            "LOWER(w.englishWord) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "w.urduMeaning LIKE CONCAT('%', :keyword, '%') OR " +
            "LOWER(w.englishMeaning) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Word> searchBySubject(@Param("subjectId") Long subjectId,
                               @Param("keyword") String keyword,
                               Pageable pageable);

    // All words for export
    @Query("SELECT w FROM Word w JOIN FETCH w.subject ORDER BY w.subject.name, w.englishWord")
    List<Word> findAllForExport();

    // Words by subject for export
    List<Word> findBySubjectIdOrderByEnglishWord(Long subjectId);

    // Total word count
    @Query("SELECT COUNT(w) FROM Word w")
    long countAllWords();
}