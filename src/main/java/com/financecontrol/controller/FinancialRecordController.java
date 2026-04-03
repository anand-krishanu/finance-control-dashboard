package com.financecontrol.controller;

import com.financecontrol.model.FinancialRecord;
import com.financecontrol.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import com.financecontrol.model.RecordType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller to handle all the money tracking stuff.
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {
    private final FinancialRecordService recordService;

    /**
     * Saves a brand new money record.
     *
     * @param record the incoming data to save
     * @return the freshly saved record
     */
    @PostMapping
    public ResponseEntity<FinancialRecord> createRecord(@Valid @RequestBody FinancialRecord record) {
        return ResponseEntity.ok(recordService.createRecord(record));
    }

    /**
     * Grabs the records but cleanly packed into pages so it does not crash.
     * Keeps sorting functionality and filters them.
     *
     * @param pageable pagination settings to keep it smooth
     * @param startDate optional start date for filter
     * @param endDate optional end date for filter
     * @param category optional category
     * @param type optional transaction type (INCOME/EXPENSE)
     * @return a single page of money records
     */
    @GetMapping
    public ResponseEntity<Page<FinancialRecord>> getAllRecords(
            @PageableDefault(size = 20, sort = "date") Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) RecordType type) {
        return ResponseEntity.ok(recordService.getAllRecords(pageable, startDate, endDate, category, type));        
    }

    /**
     * Grabs a specific record by its ID.
     *
     * @param id the ID of the record to fetch
     * @return the matching record if it exists
     */
    @GetMapping("/{id}")
    public ResponseEntity<FinancialRecord> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(recordService.getRecordById(id));
    }

    /**
     * Changes an existing record to match the new details you pass in.
     *
     * @param id the ID of the record you want to change
     * @param recordDetails the updated data to save over it
     * @return the fully updated record
     */
    @PutMapping("/{id}")
    public ResponseEntity<FinancialRecord> updateRecord(@PathVariable Long id, @Valid @RequestBody FinancialRecord recordDetails) {
        return ResponseEntity.ok(recordService.updateRecord(id, recordDetails));
    }

    /**
     * Tosses a record right into the trash.
     *
     * @param id the ID of the record to delete
     * @return an empty response letting you know it is gone
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
