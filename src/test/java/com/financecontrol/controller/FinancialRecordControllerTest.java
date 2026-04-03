package com.financecontrol.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financecontrol.model.FinancialRecord;
import com.financecontrol.service.FinancialRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FinancialRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
class FinancialRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private FinancialRecordService recordService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.financecontrol.repository.UserRepository userRepository;

    @Test
    void createRecord_Returns200AndSavedRecord() throws Exception {
        FinancialRecord input = new FinancialRecord();
        input.setAmount(new BigDecimal("100.00"));
        input.setCategory("Food");
        input.setType(com.financecontrol.model.RecordType.EXPENSE);
        input.setDate(java.time.LocalDate.now());

        FinancialRecord saved = new FinancialRecord();
        saved.setId(1L);
        saved.setAmount(new BigDecimal("100.00"));
        saved.setCategory("Food");
        saved.setType(com.financecontrol.model.RecordType.EXPENSE);
        saved.setDate(java.time.LocalDate.now());

        when(recordService.createRecord(any(FinancialRecord.class))).thenReturn(saved);

        mockMvc.perform(post("/api/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void getAllRecords_Returns200AndPage() throws Exception {
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);
        record.setAmount(new BigDecimal("50.00"));
        Page<FinancialRecord> page = new PageImpl<>(List.of(record));

        when(recordService.getAllRecords(any(Pageable.class), isNull(), isNull(), isNull(), isNull())).thenReturn(page);

        mockMvc.perform(get("/api/records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getRecordById_Returns200AndRecord() throws Exception {
        FinancialRecord record = new FinancialRecord();
        record.setId(1L);

        when(recordService.getRecordById(1L)).thenReturn(record);

        mockMvc.perform(get("/api/records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateRecord_Returns200AndUpdatedRecord() throws Exception {
        FinancialRecord input = new FinancialRecord();
        input.setAmount(new BigDecimal("200.00"));
        input.setCategory("Gaming");
        input.setType(com.financecontrol.model.RecordType.EXPENSE);
        input.setDate(java.time.LocalDate.now());

        FinancialRecord updated = new FinancialRecord();
        updated.setId(1L);
        updated.setAmount(new BigDecimal("200.00"));
        updated.setCategory("Gaming");
        updated.setType(com.financecontrol.model.RecordType.EXPENSE);
        updated.setDate(java.time.LocalDate.now());

        when(recordService.updateRecord(eq(1L), any(FinancialRecord.class))).thenReturn(updated);

        mockMvc.perform(put("/api/records/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(200.00));
    }

    @Test
    void deleteRecord_Returns204() throws Exception {
        doNothing().when(recordService).deleteRecord(1L);

        mockMvc.perform(delete("/api/records/1"))
                .andExpect(status().isNoContent());
    }
}
