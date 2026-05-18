//package com.dictionary.controller;
//
//import com.dictionary.dto.MessageResponse;
//import com.dictionary.dto.WordDTO;
//import com.dictionary.dto.WordRequest;
//import com.dictionary.service.BulkUploadService;
//import com.dictionary.service.WordService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.http.*;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
//@RequestMapping("/api/words")
//public class WordController {
//
//    @Autowired
//    private WordService wordService;
//
//    @Autowired
//    private BulkUploadService bulkUploadService;
//
//    // GET words by subject (paginated)
//    @GetMapping("/subject/{subjectId}")
//    public ResponseEntity<Map<String, Object>> getWordsBySubject(
//            @PathVariable Long subjectId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        Page<WordDTO> pageData = wordService.getWordsBySubject(subjectId, page, size);
//        return ResponseEntity.ok(buildPageResponse(pageData));
//    }
//
//    // GET global search
//    @GetMapping("/global-search")
//    public ResponseEntity<Map<String, Object>> globalSearch(
//            @RequestParam String keyword,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        Page<WordDTO> pageData = wordService.globalSearch(keyword, page, size);
//        return ResponseEntity.ok(buildPageResponse(pageData));
//    }
//
//    // GET subject-wise search
//    @GetMapping("/subject/{subjectId}/search")
//    public ResponseEntity<Map<String, Object>> searchBySubject(
//            @PathVariable Long subjectId,
//            @RequestParam String keyword,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        Page<WordDTO> pageData = wordService.searchBySubject(subjectId, keyword, page, size);
//        return ResponseEntity.ok(buildPageResponse(pageData));
//    }
//
//    // GET single word
//    @GetMapping("/{id}")
//    public ResponseEntity<WordDTO> getWord(@PathVariable Long id) {
//        return ResponseEntity.ok(wordService.getWordById(id));
//    }
//
//    // POST create word
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<WordDTO> createWord(@RequestBody WordRequest request) {
//        return ResponseEntity.ok(wordService.createWord(request));
//    }
//
//    // PUT update word
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<WordDTO> updateWord(@PathVariable Long id,
//                                              @RequestBody WordRequest request) {
//        return ResponseEntity.ok(wordService.updateWord(id, request));
//    }
//
//    // DELETE word
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<MessageResponse> deleteWord(@PathVariable Long id) {
//        wordService.deleteWord(id);
//        return ResponseEntity.ok(new MessageResponse("Word deleted successfully!", true));
//    }
//
//    // POST bulk upload
//    @PostMapping("/bulk-upload/{subjectId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<MessageResponse> bulkUpload(@PathVariable Long subjectId,
//                                                      @RequestParam("file") MultipartFile file) {
//        try {
//            int count = bulkUploadService.bulkUpload(subjectId, file);
//            return ResponseEntity.ok(new MessageResponse(count + " words uploaded successfully!", true));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new MessageResponse("Upload failed: " + e.getMessage(), false));
//        }
//    }
//
//    // GET download template
//    @GetMapping("/template")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<byte[]> downloadTemplate() {
//        try {
//            byte[] data = wordService.downloadTemplate();
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=words_template.xlsx")
//                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                    .body(data);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    // GET export all words
//    @GetMapping("/export")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<byte[]> exportAll() {
//        try {
//            byte[] data = wordService.exportToExcel();
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_words.xlsx")
//                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                    .body(data);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    // GET export by subject
//    @GetMapping("/export/{subjectId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<byte[]> exportBySubject(@PathVariable Long subjectId) {
//        try {
//            byte[] data = wordService.exportBySubjectToExcel(subjectId);
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subject_words.xlsx")
//                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                    .body(data);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    // GET stats
//    @GetMapping("/stats")
//    public ResponseEntity<Map<String, Object>> getStats() {
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("totalWords", wordService.getTotalWordCount());
//        return ResponseEntity.ok(stats);
//    }
//
//    private Map<String, Object> buildPageResponse(Page<WordDTO> page) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", page.getContent());
//        response.put("currentPage", page.getNumber());
//        response.put("totalItems", page.getTotalElements());
//        response.put("totalPages", page.getTotalPages());
//        response.put("pageSize", page.getSize());
//        return response;
//    }
//}

package com.dictionary.controller;

import com.dictionary.dto.MessageResponse;
import com.dictionary.dto.WordDTO;
import com.dictionary.dto.WordRequest;
import com.dictionary.service.BulkUploadService;
import com.dictionary.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/words")
public class WordController {

    @Autowired private WordService wordService;
    @Autowired private BulkUploadService bulkUploadService;

    // ── GET ALL words (paginated) — for admin "All Subjects" view ──────────
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllWords(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "25") int size) {
        Page<WordDTO> pageData = wordService.getAllWords(page, size);
        return ResponseEntity.ok(buildPageResponse(pageData));
    }

    // ── GET words by subject (paginated) ────────────────────────────────────
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<Map<String, Object>> getWordsBySubject(
            @PathVariable Long subjectId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "25") int size) {
        Page<WordDTO> pageData = wordService.getWordsBySubject(subjectId, page, size);
        return ResponseEntity.ok(buildPageResponse(pageData));
    }

    // ── GET global search (all subjects) ────────────────────────────────────
    @GetMapping("/global-search")
    public ResponseEntity<Map<String, Object>> globalSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "25") int size) {
        Page<WordDTO> pageData = wordService.globalSearch(keyword, page, size);
        return ResponseEntity.ok(buildPageResponse(pageData));
    }

    // ── GET subject-wise search ──────────────────────────────────────────────
    @GetMapping("/subject/{subjectId}/search")
    public ResponseEntity<Map<String, Object>> searchBySubject(
            @PathVariable Long subjectId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "25") int size) {
        Page<WordDTO> pageData = wordService.searchBySubject(subjectId, keyword, page, size);
        return ResponseEntity.ok(buildPageResponse(pageData));
    }

    // ── GET single word ──────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<WordDTO> getWord(@PathVariable Long id) {
        return ResponseEntity.ok(wordService.getWordById(id));
    }

    // ── POST create word ─────────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WordDTO> createWord(@RequestBody WordRequest request) {
        return ResponseEntity.ok(wordService.createWord(request));
    }

    // ── PUT update word ──────────────────────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WordDTO> updateWord(@PathVariable Long id,
                                              @RequestBody WordRequest request) {
        return ResponseEntity.ok(wordService.updateWord(id, request));
    }

    // ── DELETE word ──────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ResponseEntity.ok(new MessageResponse("Word deleted successfully!", true));
    }

    // ── POST bulk upload ─────────────────────────────────────────────────────
    @PostMapping("/bulk-upload/{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> bulkUpload(
            @PathVariable Long subjectId,
            @RequestParam("file") MultipartFile file) {
        try {
            int count = bulkUploadService.bulkUpload(subjectId, file);
            return ResponseEntity.ok(new MessageResponse(count + " words uploaded successfully!", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Upload failed: " + e.getMessage(), false));
        }
    }

    // ── GET download Excel template ──────────────────────────────────────────
    @GetMapping("/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            byte[] data = wordService.downloadTemplate();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=words_template.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── GET export all words ─────────────────────────────────────────────────
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportAll() {
        try {
            byte[] data = wordService.exportToExcel();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=all_words.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── GET export by subject ────────────────────────────────────────────────
    @GetMapping("/export/{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportBySubject(@PathVariable Long subjectId) {
        try {
            byte[] data = wordService.exportBySubjectToExcel(subjectId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=subject_words.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ── GET stats ────────────────────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalWords", wordService.getTotalWordCount());
        return ResponseEntity.ok(stats);
    }

    // ── helper ───────────────────────────────────────────────────────────────
    private Map<String, Object> buildPageResponse(Page<WordDTO> page) {
        Map<String, Object> r = new HashMap<>();
        r.put("content",     page.getContent());
        r.put("currentPage", page.getNumber());
        r.put("totalItems",  page.getTotalElements());
        r.put("totalPages",  page.getTotalPages());
        r.put("pageSize",    page.getSize());
        return r;
    }
}