package com.dictionary.init;

import com.dictionary.model.Role;
import com.dictionary.model.Subject;
import com.dictionary.model.User;
import com.dictionary.repository.SubjectRepository;
import com.dictionary.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdmin();
        initDefaultSubjects();
    }

    private void initAdmin() {
        if (!userRepository.existsByUsername("Kareemmusa")) {
            User admin = User.builder()
                    .username("Kareemmusa")
                    .email("kareemk128@gmail.com")
                    .password(passwordEncoder.encode("Kareemmusa1999"))
                    .fullName("Kareem")
                    .role(Role.ROLE_ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            logger.info("Admin user created: admin / admin123");
        }
        if (!userRepository.existsByUsername("Faiz")) {
            User member = User.builder()
                    .username("Faiz")
                    .email("Faiz@dictionary.com")
                    .password(passwordEncoder.encode("Faizahmed123"))
                    .fullName("Faiz Ahmed")
                    .role(Role.ROLE_MEMBER)
                    .active(true)
                    .build();
            userRepository.save(member);
            logger.info("Member user created: member / member123");
        }
    }

    private void initDefaultSubjects() {
        if (subjectRepository.count() > 0) return;

        List<Subject> subjects = Arrays.asList(
                Subject.builder().name("Biology").description("Study of living organisms and life processes")
                        .icon("🧬").color("#10b981").build(),
                Subject.builder().name("Chemistry").description("Study of matter, its properties and reactions")
                        .icon("⚗️").color("#f59e0b").build(),
                Subject.builder().name("Physics").description("Study of matter, energy, and the interactions between them")
                        .icon("⚡").color("#3b82f6").build(),
                Subject.builder().name("Mathematics").description("Science of numbers, quantities, and shapes")
                        .icon("📐").color("#8b5cf6").build(),
                Subject.builder().name("Earth Science").description("Study of the Earth and its structure")
                        .icon("🌍").color("#06b6d4").build(),
                Subject.builder().name("Environmental Science").description("Study of the environment and ecological systems")
                        .icon("🌿").color("#22c55e").build(),
                Subject.builder().name("Indian History").description("Historical events and civilization of India")
                        .icon("🏛️").color("#f97316").build(),
                Subject.builder().name("Current Affairs").description("Recent and ongoing events of significance")
                        .icon("📰").color("#ef4444").build(),
                Subject.builder().name("Algebra").description("Branch of mathematics dealing with symbols and rules")
                        .icon("🔢").color("#a855f7").build(),
                Subject.builder().name("Verbal Non-Verbal").description("Verbal and non-verbal reasoning skills")
                        .icon("💭").color("#14b8a6").build(),
                Subject.builder().name("Probability").description("Study of likelihood of events occurring")
                        .icon("🎲").color("#f43f5e").build(),
                Subject.builder().name("Everyday Science").description("Scientific concepts in daily life")
                        .icon("🔬").color("#84cc16").build(),
                Subject.builder().name("Frequency Discount").description("Frequency and discount related terminology")
                        .icon("📊").color("#0ea5e9").build()
        );

        subjectRepository.saveAll(subjects);
        logger.info("Default subjects initialized: {} subjects created", subjects.size());
    }
}