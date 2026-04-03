package com.financecontrol.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * A DTO holding the final math for the dashboard.
 *
 * @param totalIncome all the cash coming in
 * @param totalExpense all the cash going out
 * @param netBalance what is left at the end of the day
 * @param trends historical data points grouped by date and type
 */
public record DashboardSummary(
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal netBalance,
    List<TrendData> trends
) {}
