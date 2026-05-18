//package com.dictionary.service;
//
//import com.dictionary.dto.WordDTO;
//import com.dictionary.dto.WordRequest;
//import com.dictionary.model.Subject;
//import com.dictionary.model.Word;
//import com.dictionary.repository.WordRepository;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class WordService {
//
//    @Autowired
//    private WordRepository wordRepository;
//
//    @Autowired
//    private SubjectService subjectService;
//
//    public Page<WordDTO> getWordsBySubject(Long subjectId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("englishWord").ascending());
//        return wordRepository.findBySubjectId(subjectId, pageable).map(this::toDTO);
//    }
//
//    public Page<WordDTO> globalSearch(String keyword, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("englishWord").ascending());
//        return wordRepository.globalSearch(keyword, pageable).map(this::toDTO);
//    }
//
//    public Page<WordDTO> searchBySubject(Long subjectId, String keyword, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("englishWord").ascending());
//        return wordRepository.searchBySubject(subjectId, keyword, pageable).map(this::toDTO);
//    }
//
//    public WordDTO createWord(WordRequest request) {
//        Subject subject = subjectService.findById(request.getSubjectId());
//        Word word = Word.builder()
//                .englishWord(request.getEnglishWord())
//                .urduMeaning(request.getUrduMeaning())
//                .englishMeaning(request.getEnglishMeaning())
//                .exampleSentence(request.getExampleSentence())
//                .partOfSpeech(request.getPartOfSpeech())
//                .pronunciation(request.getPronunciation())
//                .synonyms(request.getSynonyms())
//                .antonyms(request.getAntonyms())
//                .subject(subject)
//                .build();
//        return toDTO(wordRepository.save(word));
//    }
//
//    public WordDTO updateWord(Long id, WordRequest request) {
//        Word word = wordRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Word not found: " + id));
//        Subject subject = subjectService.findById(request.getSubjectId());
//        word.setEnglishWord(request.getEnglishWord());
//        word.setUrduMeaning(request.getUrduMeaning());
//        word.setEnglishMeaning(request.getEnglishMeaning());
//        word.setExampleSentence(request.getExampleSentence());
//        word.setPartOfSpeech(request.getPartOfSpeech());
//        word.setPronunciation(request.getPronunciation());
//        word.setSynonyms(request.getSynonyms());
//        word.setAntonyms(request.getAntonyms());
//        word.setSubject(subject);
//        return toDTO(wordRepository.save(word));
//    }
//
//    public void deleteWord(Long id) {
//        wordRepository.deleteById(id);
//    }
//
//    public WordDTO getWordById(Long id) {
//        return toDTO(wordRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Word not found: " + id)));
//    }
//
//    public long getTotalWordCount() {
//        return wordRepository.countAllWords();
//    }
//
//    // Export all words to Excel
//    public byte[] exportToExcel() throws IOException {
//        List<Word> words = wordRepository.findAllForExport();
//        return generateExcel(words);
//    }
//
//    // Export words by subject to Excel
//    public byte[] exportBySubjectToExcel(Long subjectId) throws IOException {
//        List<Word> words = wordRepository.findBySubjectIdOrderByEnglishWord(subjectId);
//        return generateExcel(words);
//    }
//
//    // Download Excel template
//    public byte[] downloadTemplate() throws IOException {
//        try (XSSFWorkbook workbook = new XSSFWorkbook();
//             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            Sheet sheet = workbook.createSheet("Words Template");
//            String[] headers = {"English Word", "Urdu Meaning", "English Meaning",
//                    "Example Sentence", "Part of Speech", "Pronunciation", "Synonyms", "Antonyms"};
//            Row headerRow = sheet.createRow(0);
//            CellStyle headerStyle = workbook.createCellStyle();
//            Font font = workbook.createFont();
//            font.setBold(true);
//            headerStyle.setFont(font);
//            for (int i = 0; i < headers.length; i++) {
//                Cell cell = headerRow.createCell(i);
//                cell.setCellValue(headers[i]);
//                cell.setCellStyle(headerStyle);
//                sheet.autoSizeColumn(i);
//            }
//            workbook.write(out);
//            return out.toByteArray();
//        }
//    }
//
//    private byte[] generateExcel(List<Word> words) throws IOException {
//        try (XSSFWorkbook workbook = new XSSFWorkbook();
//             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            Sheet sheet = workbook.createSheet("Dictionary Words");
//            String[] headers = {"English Word", "Urdu Meaning", "English Meaning",
//                    "Example Sentence", "Part of Speech", "Pronunciation", "Synonyms", "Antonyms", "Subject"};
//            Row headerRow = sheet.createRow(0);
//            CellStyle headerStyle = workbook.createCellStyle();
//            Font font = workbook.createFont();
//            font.setBold(true);
//            headerStyle.setFont(font);
//            for (int i = 0; i < headers.length; i++) {
//                Cell cell = headerRow.createCell(i);
//                cell.setCellValue(headers[i]);
//                cell.setCellStyle(headerStyle);
//            }
//            int rowNum = 1;
//            for (Word word : words) {
//                Row row = sheet.createRow(rowNum++);
//                row.createCell(0).setCellValue(word.getEnglishWord());
//                row.createCell(1).setCellValue(word.getUrduMeaning() != null ? word.getUrduMeaning() : "");
//                row.createCell(2).setCellValue(word.getEnglishMeaning() != null ? word.getEnglishMeaning() : "");
//                row.createCell(3).setCellValue(word.getExampleSentence() != null ? word.getExampleSentence() : "");
//                row.createCell(4).setCellValue(word.getPartOfSpeech() != null ? word.getPartOfSpeech() : "");
//                row.createCell(5).setCellValue(word.getPronunciation() != null ? word.getPronunciation() : "");
//                row.createCell(6).setCellValue(word.getSynonyms() != null ? word.getSynonyms() : "");
//                row.createCell(7).setCellValue(word.getAntonyms() != null ? word.getAntonyms() : "");
//                row.createCell(8).setCellValue(word.getSubject() != null ? word.getSubject().getName() : "");
//            }
//            for (int i = 0; i < headers.length; i++) {
//                sheet.autoSizeColumn(i);
//            }
//            workbook.write(out);
//            return out.toByteArray();
//        }
//    }
//
//    public WordDTO toDTO(Word word) {
//        return WordDTO.builder()
//                .id(word.getId())
//                .englishWord(word.getEnglishWord())
//                .urduMeaning(word.getUrduMeaning())
//                .englishMeaning(word.getEnglishMeaning())
//                .exampleSentence(word.getExampleSentence())
//                .partOfSpeech(word.getPartOfSpeech())
//                .pronunciation(word.getPronunciation())
//                .synonyms(word.getSynonyms())
//                .antonyms(word.getAntonyms())
//                .subjectId(word.getSubject() != null ? word.getSubject().getId() : null)
//                .subjectName(word.getSubject() != null ? word.getSubject().getName() : null)
//                .createdAt(word.getCreatedAt())
//                .build();
//    }
//}

package com.dictionary.service;

import com.dictionary.dto.WordDTO;
import com.dictionary.dto.WordRequest;
import com.dictionary.model.Subject;
import com.dictionary.model.Word;
import com.dictionary.repository.WordRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WordService {

    @Autowired private WordRepository wordRepository;
    @Autowired private SubjectService subjectService;

    // ── All words paginated (for admin "All Subjects") ──────────────────────
    public Page<WordDTO> getAllWords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("subject.name").ascending().and(Sort.by("englishWord").ascending()));
        return wordRepository.findAll(pageable).map(this::toDTO);
    }

    // ── Words by subject ─────────────────────────────────────────────────────
    public Page<WordDTO> getWordsBySubject(Long subjectId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("englishWord").ascending());
        return wordRepository.findBySubjectId(subjectId, pageable).map(this::toDTO);
    }

    // ── Global search ────────────────────────────────────────────────────────
    public Page<WordDTO> globalSearch(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("englishWord").ascending());
        return wordRepository.globalSearch(keyword, pageable).map(this::toDTO);
    }

    // ── Subject-wise search ──────────────────────────────────────────────────
    public Page<WordDTO> searchBySubject(Long subjectId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("englishWord").ascending());
        return wordRepository.searchBySubject(subjectId, keyword, pageable).map(this::toDTO);
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────
    public WordDTO createWord(WordRequest req) {
        Subject subject = subjectService.findById(req.getSubjectId());
        Word word = Word.builder()
                .englishWord(req.getEnglishWord())
                .urduMeaning(req.getUrduMeaning())
                .englishMeaning(req.getEnglishMeaning())
                .exampleSentence(req.getExampleSentence())
                .partOfSpeech(req.getPartOfSpeech())
                .pronunciation(req.getPronunciation())
                .synonyms(req.getSynonyms())
                .antonyms(req.getAntonyms())
                .subject(subject)
                .build();
        return toDTO(wordRepository.save(word));
    }

    public WordDTO updateWord(Long id, WordRequest req) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Word not found: " + id));
        Subject subject = subjectService.findById(req.getSubjectId());
        word.setEnglishWord(req.getEnglishWord());
        word.setUrduMeaning(req.getUrduMeaning());
        word.setEnglishMeaning(req.getEnglishMeaning());
        word.setExampleSentence(req.getExampleSentence());
        word.setPartOfSpeech(req.getPartOfSpeech());
        word.setPronunciation(req.getPronunciation());
        word.setSynonyms(req.getSynonyms());
        word.setAntonyms(req.getAntonyms());
        word.setSubject(subject);
        return toDTO(wordRepository.save(word));
    }

    public void deleteWord(Long id) {
        wordRepository.deleteById(id);
    }

    public WordDTO getWordById(Long id) {
        return toDTO(wordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Word not found: " + id)));
    }

    public long getTotalWordCount() {
        return wordRepository.countAllWords();
    }

    // ── Export ────────────────────────────────────────────────────────────────
    public byte[] exportToExcel() throws IOException {
        return generateExcel(wordRepository.findAllForExport());
    }

    public byte[] exportBySubjectToExcel(Long subjectId) throws IOException {
        return generateExcel(wordRepository.findBySubjectIdOrderByEnglishWord(subjectId));
    }

    public byte[] downloadTemplate() throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Words Template");
            String[] headers = {
                    "English Word *", "Urdu Meaning", "English Meaning",
                    "Example Sentence", "Part of Speech", "Pronunciation",
                    "Synonyms", "Antonyms"
            };
            Row headerRow = sheet.createRow(0);
            CellStyle style = wb.createCellStyle();
            Font font = wb.createFont(); font.setBold(true); style.setFont(font);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(style);
                sheet.setColumnWidth(i, 5000);
            }
            // Add one sample row
            Row sample = sheet.createRow(1);
            sample.createCell(0).setCellValue("Photosynthesis");
            sample.createCell(1).setCellValue("ضیائی تالیف");
            sample.createCell(2).setCellValue("Process by which plants make food using sunlight");
            sample.createCell(3).setCellValue("Photosynthesis occurs in chloroplasts.");
            sample.createCell(4).setCellValue("Noun");
            sample.createCell(5).setCellValue("/ˌfəʊtəʊˈsɪnθɪsɪs/");
            sample.createCell(6).setCellValue("");
            sample.createCell(7).setCellValue("");
            wb.write(out);
            return out.toByteArray();
        }
    }

    private byte[] generateExcel(List<Word> words) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Dictionary Words");
            String[] headers = {
                    "English Word", "Urdu Meaning", "English Meaning",
                    "Example Sentence", "Part of Speech", "Pronunciation",
                    "Synonyms", "Antonyms", "Subject"
            };
            Row headerRow = sheet.createRow(0);
            CellStyle style = wb.createCellStyle();
            Font font = wb.createFont(); font.setBold(true); style.setFont(font);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(style);
            }
            int rowNum = 1;
            for (Word w : words) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(safe(w.getEnglishWord()));
                row.createCell(1).setCellValue(safe(w.getUrduMeaning()));
                row.createCell(2).setCellValue(safe(w.getEnglishMeaning()));
                row.createCell(3).setCellValue(safe(w.getExampleSentence()));
                row.createCell(4).setCellValue(safe(w.getPartOfSpeech()));
                row.createCell(5).setCellValue(safe(w.getPronunciation()));
                row.createCell(6).setCellValue(safe(w.getSynonyms()));
                row.createCell(7).setCellValue(safe(w.getAntonyms()));
                row.createCell(8).setCellValue(w.getSubject() != null ? w.getSubject().getName() : "");
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
            return out.toByteArray();
        }
    }

    private String safe(String s) { return s != null ? s : ""; }

    public WordDTO toDTO(Word w) {
        return WordDTO.builder()
                .id(w.getId())
                .englishWord(w.getEnglishWord())
                .urduMeaning(w.getUrduMeaning())
                .englishMeaning(w.getEnglishMeaning())
                .exampleSentence(w.getExampleSentence())
                .partOfSpeech(w.getPartOfSpeech())
                .pronunciation(w.getPronunciation())
                .synonyms(w.getSynonyms())
                .antonyms(w.getAntonyms())
                .subjectId(w.getSubject() != null ? w.getSubject().getId() : null)
                .subjectName(w.getSubject() != null ? w.getSubject().getName() : null)
                .createdAt(w.getCreatedAt())
                .build();
    }
}