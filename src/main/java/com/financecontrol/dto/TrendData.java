package com.financecontrol.dto;

import com.financecontrol.model.RecordType;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface TrendData {
    LocalDate getDate();
    RecordType getType();
    BigDecimal getTotalAmount();
}