package com.financecontrol.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configure JPA Auditing separately from the main application class.
 * This fixes "JPA metamodel must not be empty" when running @WebMvcTest testing.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
