package com.financecontrol.dto;

import java.math.BigDecimal;

/**
 * A DTO holding the final math for the dashboard.
 *
 * @param totalIncome all the cash coming in
 * @param totalExpense all the cash going out
 * @param netBalance what is left at the end of the day
 */
public record DashboardSummary(
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal netBalance
) {}
