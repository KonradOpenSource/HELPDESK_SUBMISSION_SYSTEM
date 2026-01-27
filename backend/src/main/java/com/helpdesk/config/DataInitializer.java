package com.helpdesk.config;

import com.helpdesk.entity.Category;
import com.helpdesk.entity.User;
import com.helpdesk.repository.CategoryRepository;
import com.helpdesk.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Initializes default data in the database on application startup.
 * Creates default users (admin, agent, user) and categories if they don't exist.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, 
                          CategoryRepository categoryRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("🚀 Starting data initialization...");
        
        initializeUsers();
        initializeCategories();
        
        logger.info("✅ Data initialization completed successfully!");
    }

    private void initializeUsers() {
        logger.info("Initializing default users...");
        
        // Create admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@helpdesk.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            logger.info("✅ Created default ADMIN user: admin / admin123");
        } else {
            logger.info("ℹ️  Admin user already exists");
        }

        // Create agent user
        if (!userRepository.existsByUsername("agent")) {
            User agent = new User();
            agent.setUsername("agent");
            agent.setEmail("agent@helpdesk.com");
            agent.setPassword(passwordEncoder.encode("agent123"));
            agent.setFirstName("Support");
            agent.setLastName("Agent");
            agent.setRole(User.Role.AGENT);
            userRepository.save(agent);
            logger.info("✅ Created default AGENT user: agent / agent123");
        } else {
            logger.info("ℹ️  Agent user already exists");
        }

        // Create regular user
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@helpdesk.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setFirstName("Regular");
            user.setLastName("User");
            user.setRole(User.Role.USER);
            userRepository.save(user);
            logger.info("✅ Created default USER: user / user123");
        } else {
            logger.info("ℹ️  Regular user already exists");
        }
    }

    private void initializeCategories() {
        logger.info("Initializing default categories...");
        
        createCategoryIfNotExists("Technical Support", "Technical issues and software problems");
        createCategoryIfNotExists("Account Issues", "Login, password, and account related problems");
        createCategoryIfNotExists("Billing", "Payment and subscription issues");
        createCategoryIfNotExists("General Inquiry", "General questions and information requests");
        
        logger.info("✅ Categories initialized");
    }

    private void createCategoryIfNotExists(String name, String description) {
        if (!categoryRepository.existsByName(name)) {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            categoryRepository.save(category);
            logger.info("✅ Created category: {}", name);
        } else {
            logger.debug("ℹ️  Category already exists: {}", name);
        }
    }
}
