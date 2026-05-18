//package com.dictionary.service;
//
//import com.dictionary.model.Subject;
//import com.dictionary.model.Word;
//import com.dictionary.repository.WordRepository;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class BulkUploadService {
//
//    @Autowired
//    private WordRepository wordRepository;
//
//    @Autowired
//    private SubjectService subjectService;
//
//    public int bulkUpload(Long subjectId, MultipartFile file) throws Exception {
//        Subject subject = subjectService.findById(subjectId);
//        String filename = file.getOriginalFilename();
//        List<Word> words;
//
//        if (filename != null && (filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
//            words = parseExcel(file, subject);
//        } else if (filename != null && filename.endsWith(".csv")) {
//            words = parseCsv(file, subject);
//        } else {
//            throw new RuntimeException("Unsupported file format. Please use .xlsx, .xls, or .csv");
//        }
//
//        wordRepository.saveAll(words);
//        return words.size();
//    }
//
//    private List<Word> parseExcel(MultipartFile file, Subject subject) throws Exception {
//        List<Word> words = new ArrayList<>();
//        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
//            Sheet sheet = workbook.getSheetAt(0);
//            boolean firstRow = true;
//            for (Row row : sheet) {
//                if (firstRow) { firstRow = false; continue; } // skip header
//                if (row == null || isRowEmpty(row)) continue;
//
//                Word word = Word.builder()
//                        .englishWord(getCellValue(row, 0))
//                        .urduMeaning(getCellValue(row, 1))
//                        .englishMeaning(getCellValue(row, 2))
//                        .exampleSentence(getCellValue(row, 3))
//                        .partOfSpeech(getCellValue(row, 4))
//                        .pronunciation(getCellValue(row, 5))
//                        .synonyms(getCellValue(row, 6))
//                        .antonyms(getCellValue(row, 7))
//                        .subject(subject)
//                        .build();
//
//                if (word.getEnglishWord() != null && !word.getEnglishWord().isEmpty()) {
//                    words.add(word);
//                }
//            }
//        }
//        return words;
//    }
//
//    private List<Word> parseCsv(MultipartFile file, Subject subject) throws Exception {
//        List<Word> words = new ArrayList<>();
//        try (BufferedReader reader = new BufferedReader(
//                new InputStreamReader(file.getInputStream(), "UTF-8"))) {
//            String line;
//            boolean firstLine = true;
//            while ((line = reader.readLine()) != null) {
//                if (firstLine) { firstLine = false; continue; }
//                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//                if (cols.length < 1 || cols[0].trim().isEmpty()) continue;
//
//                Word word = Word.builder()
//                        .englishWord(cleanCsvField(cols, 0))
//                        .urduMeaning(cleanCsvField(cols, 1))
//                        .englishMeaning(cleanCsvField(cols, 2))
//                        .exampleSentence(cleanCsvField(cols, 3))
//                        .partOfSpeech(cleanCsvField(cols, 4))
//                        .pronunciation(cleanCsvField(cols, 5))
//                        .synonyms(cleanCsvField(cols, 6))
//                        .antonyms(cleanCsvField(cols, 7))
//                        .subject(subject)
//                        .build();
//                words.add(word);
//            }
//        }
//        return words;
//    }
//
//    private String getCellValue(Row row, int cellIndex) {
//        Cell cell = row.getCell(cellIndex);
//        if (cell == null) return "";
//        return switch (cell.getCellType()) {
//            case STRING -> cell.getStringCellValue().trim();
//            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
//            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
//            default -> "";
//        };
//    }
//
//    private boolean isRowEmpty(Row row) {
//        for (Cell cell : row) {
//            if (cell.getCellType() != CellType.BLANK) return false;
//        }
//        return true;
//    }
//
//    private String cleanCsvField(String[] cols, int idx) {
//        if (idx >= cols.length) return "";
//        return cols[idx].trim().replaceAll("^\"|\"$", "");
//    }
//}

package com.dictionary.service;

import com.dictionary.model.Subject;
import com.dictionary.model.Word;
import com.dictionary.repository.WordRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BulkUploadService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private SubjectService subjectService;

    // ─────────────────────────────────────────────────────────────────────────
    // Urdu Unicode block:  0600–06FF  (Arabic/Urdu script)
    // Also captures common punctuation used in Urdu: ،  ؟  ۔
    // ─────────────────────────────────────────────────────────────────────────
    private static final Pattern URDU_BLOCK =
            Pattern.compile("[\\u0600-\\u06FF\\u0750-\\u077F\\uFB50-\\uFDFF\\uFE70-\\uFEFF]+");

    /**
     * Given a raw cell value that may look like:
     *   "Roasting روسٹنگ"   →  english="Roasting",  urdu="روسٹنگ"
     *   "Moist نم"           →  english="Moist",     urdu="نم"
     *   "Calcination تکلیس"  →  english="Calcination", urdu="تکلیس"
     *   "Pure water"         →  english="Pure water",  urdu=""   (no Urdu present)
     *   "روغنی بافتیں"        →  english="",           urdu="روغنی بافتیں"
     *
     * Strategy:
     *  1. Find all Urdu (Arabic-block) characters → that is the urduPart.
     *  2. Whatever remains (stripped) → englishPart.
     *  3. If Column B already has a value, that value wins for urduMeaning
     *     (i.e. we don't overwrite explicitly provided urdu column data).
     */
    private String[] splitEnglishUrdu(String raw) {
        if (raw == null || raw.isBlank()) return new String[]{"", ""};

        StringBuilder urduBuilder   = new StringBuilder();
        StringBuilder englishBuilder = new StringBuilder();

        // Walk character by character; group contiguous Urdu chars
        for (char c : raw.toCharArray()) {
            if (isUrduChar(c)) {
                urduBuilder.append(c);
            } else {
                englishBuilder.append(c);
            }
        }

        String english = englishBuilder.toString().trim()
                // remove leftover stray punctuation at ends
                .replaceAll("[\\s\\-–—]+$", "")
                .replaceAll("^[\\s\\-–—]+", "")
                .trim();

        String urdu = urduBuilder.toString().trim();

        return new String[]{english, urdu};
    }

    /** Returns true for characters in Arabic / Urdu Unicode blocks */
    private boolean isUrduChar(char c) {
        return (c >= 0x0600 && c <= 0x06FF)   // Arabic block (core Urdu letters)
                || (c >= 0x0750 && c <= 0x077F)   // Arabic Supplement
                || (c >= 0xFB50 && c <= 0xFDFF)   // Arabic Presentation Forms-A
                || (c >= 0xFE70 && c <= 0xFEFF)   // Arabic Presentation Forms-B
                || c == 0x200C || c == 0x200D      // Zero-width non-joiner / joiner
                || c == 0x060C || c == 0x061B      // Arabic comma / semicolon
                || c == 0x061F || c == 0x06D4;     // Arabic ? / full stop
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLIC ENTRY POINT
    // ─────────────────────────────────────────────────────────────────────────
    public int bulkUpload(Long subjectId, MultipartFile file) throws Exception {
        Subject subject = subjectService.findById(subjectId);
        String filename = file.getOriginalFilename();
        List<Word> words;

        if (filename != null && (filename.endsWith(".xlsx") || filename.endsWith(".xls"))) {
            words = parseExcel(file, subject);
        } else if (filename != null && filename.endsWith(".csv")) {
            words = parseCsv(file, subject);
        } else {
            throw new RuntimeException(
                    "Unsupported file format. Please upload .xlsx, .xls, or .csv");
        }

        wordRepository.saveAll(words);
        return words.size();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EXCEL PARSER
    // ─────────────────────────────────────────────────────────────────────────
    private List<Word> parseExcel(MultipartFile file, Subject subject) throws Exception {
        List<Word> words = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;

            for (Row row : sheet) {
                if (firstRow) { firstRow = false; continue; }   // skip header row
                if (row == null || isRowEmpty(row)) continue;

                String colA = getCellValue(row, 0);   // may be "English روسٹنگ" or just English
                String colB = getCellValue(row, 1);   // explicit urdu column (may be empty)

                // ── Split colA into english + urdu parts ──
                String[] parts    = splitEnglishUrdu(colA);
                String englishWord = parts[0];
                String urduFromA   = parts[1];

                // Column B wins if it has real Urdu content, otherwise use what we extracted
                String urduMeaning = (colB != null && !colB.isBlank()) ? colB.trim() : urduFromA;

                if (englishWord.isEmpty() && urduMeaning.isEmpty()) continue;

                // If column A was pure Urdu with no English (edge case), keep original as english_word
                if (englishWord.isEmpty() && !colA.isBlank()) {
                    englishWord = colA.trim();
                    urduMeaning = colB != null ? colB.trim() : "";
                }

                Word word = Word.builder()
                        .englishWord(englishWord)
                        .urduMeaning(urduMeaning)
                        .englishMeaning(getCellValue(row, 2))
                        .exampleSentence(getCellValue(row, 3))
                        .partOfSpeech(getCellValue(row, 4))
                        .pronunciation(getCellValue(row, 5))
                        .synonyms(getCellValue(row, 6))
                        .antonyms(getCellValue(row, 7))
                        .subject(subject)
                        .build();

                words.add(word);
            }
        }
        return words;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CSV PARSER
    // ─────────────────────────────────────────────────────────────────────────
    private List<Word> parseCsv(MultipartFile file, Subject subject) throws Exception {
        List<Word> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), "UTF-8"))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }

                // RFC-4180 aware split (handles quoted commas)
                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (cols.length < 1) continue;

                String colA = cleanCsvField(cols, 0);
                String colB = cleanCsvField(cols, 1);

                String[] parts     = splitEnglishUrdu(colA);
                String englishWord = parts[0];
                String urduFromA   = parts[1];

                String urduMeaning = (!colB.isBlank()) ? colB : urduFromA;

                if (englishWord.isEmpty() && !colA.isBlank()) {
                    englishWord = colA;
                    urduMeaning = colB;
                }
                if (englishWord.isBlank()) continue;

                Word word = Word.builder()
                        .englishWord(englishWord)
                        .urduMeaning(urduMeaning)
                        .englishMeaning(cleanCsvField(cols, 2))
                        .exampleSentence(cleanCsvField(cols, 3))
                        .partOfSpeech(cleanCsvField(cols, 4))
                        .pronunciation(cleanCsvField(cols, 5))
                        .synonyms(cleanCsvField(cols, 6))
                        .antonyms(cleanCsvField(cols, 7))
                        .subject(subject)
                        .build();

                words.add(word);
            }
        }
        return words;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> "";
        };
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }

    private String cleanCsvField(String[] cols, int idx) {
        if (idx >= cols.length) return "";
        return cols[idx].trim().replaceAll("^\"|\"$", "");
    }
}
