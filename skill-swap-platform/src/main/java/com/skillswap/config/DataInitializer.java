package com.skillswap.config;

import com.skillswap.entity.Skill;
import com.skillswap.entity.User;
import com.skillswap.repository.SkillRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j //-Automatically creates a logger object (log)
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            if (userRepository.count() > 0) {
                log.info("Database already seeded. Skipping...");
                return;
            }

            log.info("Seeding initial data...");

            // Create ADMIN user
            User admin = User.builder()
                    .username("admin")
                    .email("admin@skillswap.com")
                    .password(passwordEncoder.encode("admin123"))
                    .bio("Platform administrator")
                    .role(User.Role.ADMIN)
                    .build();
            userRepository.save(admin);

            // Create regular users
            User alice = User.builder()
                    .username("alice")
                    .email("alice@example.com")
                    .password(passwordEncoder.encode("alice123"))
                    .bio("Full-stack developer who loves teaching")
                    .role(User.Role.USER)
                    .build();
            userRepository.save(alice);

            User bob = User.builder()
                    .username("bob")
                    .email("bob@example.com")
                    .password(passwordEncoder.encode("bob123"))
                    .bio("Designer with UX expertise")
                    .role(User.Role.USER)
                    .build();
            userRepository.save(bob);

            // Create skills
            Skill javaSkill = Skill.builder()
                    .name("Java Programming")
                    .description("Core Java, Spring Boot, REST APIs")
                    .level(Skill.SkillLevel.EXPERT)
                    .owner(alice)
                    .build();
            skillRepository.save(javaSkill);

            Skill reactSkill = Skill.builder()
                    .name("React.js")
                    .description("Component-based UI development")
                    .level(Skill.SkillLevel.INTERMEDIATE)
                    .owner(alice)
                    .build();
            skillRepository.save(reactSkill);

            Skill uiDesign = Skill.builder()
                    .name("UI/UX Design")
                    .description("Figma, wireframing, prototyping")
                    .level(Skill.SkillLevel.EXPERT)
                    .owner(bob)
                    .build();
            skillRepository.save(uiDesign);

            Skill sqlSkill = Skill.builder()
                    .name("SQL & Database Design")
                    .description("PostgreSQL, MySQL, query optimization")
                    .level(Skill.SkillLevel.BEGINNER)
                    .owner(bob)
                    .build();
            skillRepository.save(sqlSkill);

            log.info("Seeding complete!");
            log.info("Test credentials:");
            log.info("  ADMIN  -> username: admin  | password: admin123");
            log.info("  USER   -> username: alice  | password: alice123");
            log.info("  USER   -> username: bob    | password: bob123");
        };
    }
}