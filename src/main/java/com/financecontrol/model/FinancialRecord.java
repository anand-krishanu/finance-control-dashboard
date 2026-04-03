package com.financecontrol.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single money move in the database so we know where the cash went.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "financial_records", indexes = {
    @Index(name = "idx_record_user_id", columnList = "user_id"),
    @Index(name = "idx_record_date", columnList = "date"),
    @Index(name = "idx_record_type", columnList = "type")
})
public class FinancialRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be strictly positive")
    @Column(nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Record type must be fully specified")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType type;

    @NotBlank(message = "Needs a category so we can group it later")
    @Column(nullable = false)
    private String category;

    @NotNull(message = "Transaction date is required")
    @Column(nullable = false)
    private LocalDate date;

    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
