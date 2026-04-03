package com.financecontrol.controller;

import com.financecontrol.dto.DashboardSummary;
import com.financecontrol.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.financecontrol.repository.UserRepository userRepository;

    @Test
    void getSummary_Returns200AndJson() throws Exception {
        DashboardSummary mockSummary = new DashboardSummary(
                new BigDecimal("5000.00"),
                new BigDecimal("2000.00"),
                new BigDecimal("3000.00")
        );
        when(dashboardService.getSummary()).thenReturn(mockSummary);

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").value(5000.00))
                .andExpect(jsonPath("$.totalExpense").value(2000.00))
                .andExpect(jsonPath("$.netBalance").value(3000.00));
    }
}
