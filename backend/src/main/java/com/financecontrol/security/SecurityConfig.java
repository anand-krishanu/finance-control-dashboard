package com.financecontrol.security;

import com.financecontrol.model.User;
import com.financecontrol.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Keeps bad guys out of our API lmao.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    /**
     * Tells Spring how to actually find our users in the database to log them in.
     *
     * @return a service that loads up the user details for spring security
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build();
        };
    }

    /**
     * Grabs the hashing tool to scramble passwords so we do not leak them.
     *
     * @return the bcrypt password encoder thing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This is customization of the defaul SecurityFilterChain that Spring Boot provides.
     * Sets up the actual rules for who can hit which web URLs.
     *
     * @param http the security web builder thing
     * @return the finalized security filter chain locking down the app
     * @throws Exception if something blows up while setting these rules
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Let anyone read the API docs
                .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll() // Allow registration conceptually
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/records/**").hasAnyRole("ANALYST", "ADMIN")
                .requestMatchers("/api/records/**").hasRole("ADMIN")
                .requestMatchers("/api/dashboard/**").hasAnyRole("VIEWER", "ANALYST", "ADMIN")
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(f -> f.disable())) // Required for H2 console
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
