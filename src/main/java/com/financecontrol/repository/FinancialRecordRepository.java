package com.financecontrol.repository;

import com.financecontrol.model.FinancialRecord;
import com.financecontrol.model.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Talks to the database to do all the heavy lifting for money records.
 */
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    /**
     * Grabs a paginated list of records specifically for the logged-in user.
     */
    Page<FinancialRecord> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT f FROM FinancialRecord f WHERE f.user.id = :userId " +
           "AND (:startDate IS NULL OR f.date >= :startDate) " +
           "AND (:endDate IS NULL OR f.date <= :endDate) " +
           "AND (:category IS NULL OR f.category = :category) " +
           "AND (:type IS NULL OR f.type = :type)")
    Page<FinancialRecord> findFilteredRecords(@Param("userId") Long userId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              @Param("category") String category,
                                              @Param("type") RecordType type,
                                              Pageable pageable);

    @Query("SELECT f.date AS date, f.type AS type, SUM(f.amount) AS totalAmount " +
           "FROM FinancialRecord f " +
           "WHERE f.user.id = :userId AND f.date >= :startDate " +
           "GROUP BY f.date, f.type " +
           "ORDER BY f.date ASC")
    List<com.financecontrol.dto.TrendData> getTrendData(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    /**
     * Does the math to add up all the cash we made.
     *
     * @return the total money coming in
     */
    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.type = 'INCOME' AND f.user.id = :userId")
    BigDecimal getTotalIncome(@Param("userId") Long userId);

    /**
     * Does the math to add up all the cash we blew.
     *
     * @return the total money going out
     */
    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.type = 'EXPENSE' AND f.user.id = :userId")
    BigDecimal getTotalExpense(@Param("userId") Long userId);
}
