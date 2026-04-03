package com.financecontrol.config;

import com.financecontrol.model.FinancialRecord;
import com.financecontrol.model.RecordType;
import com.financecontrol.model.Role;
import com.financecontrol.model.User;
import com.financecontrol.repository.FinancialRecordRepository;
import com.financecontrol.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FinancialRecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();

            User analyst = User.builder()
                    .username("analyst")
                    .password(passwordEncoder.encode("analyst123"))
                    .role(Role.ANALYST)
                    .active(true)
                    .build();

            User viewer = User.builder()
                    .username("viewer")
                    .password(passwordEncoder.encode("viewer123"))
                    .role(Role.VIEWER)
                    .active(true)
                    .build();

            userRepository.saveAll(List.of(admin, analyst, viewer));

            FinancialRecord rec1 = FinancialRecord.builder()
                    .user(admin)
                    .amount(new BigDecimal("5000.00"))
                    .type(RecordType.INCOME)
                    .category("Salary")
                    .date(LocalDate.now().minusDays(5))
                    .notes("Monthly paycheck")
                    .build();

            FinancialRecord rec2 = FinancialRecord.builder()
                    .user(admin)
                    .amount(new BigDecimal("1200.00"))
                    .type(RecordType.EXPENSE)
                    .category("Rent")
                    .date(LocalDate.now().minusDays(2))
                    .notes("Apartment rent")
                    .build();

            FinancialRecord rec3 = FinancialRecord.builder()
                    .user(analyst)
                    .amount(new BigDecimal("200.00"))
                    .type(RecordType.EXPENSE)
                    .category("Groceries")
                    .date(LocalDate.now().minusDays(1))
                    .notes("Weekly food")
                    .build();

            recordRepository.saveAll(List.of(rec1, rec2, rec3));

            System.out.println("Demo data seeded beautifully. Users created: admin, analyst, viewer (Password: <username>123)");
        }
    }
}
