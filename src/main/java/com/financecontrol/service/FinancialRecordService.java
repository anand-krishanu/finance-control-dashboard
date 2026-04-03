package com.financecontrol.service;

import com.financecontrol.model.FinancialRecord;
import com.financecontrol.model.RecordType;
import com.financecontrol.model.User;
import com.financecontrol.repository.FinancialRecordRepository;
import com.financecontrol.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles the actual business logic for saving and finding money records.
 */
@Service
@RequiredArgsConstructor
public class FinancialRecordService {
    private final FinancialRecordRepository repository;
    private final UserRepository userRepository;

    /**
     * Grabs the user ID of whoever is currently hitting the API.
     */
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    /**
     * Saves a fresh money record into the database for the logged-in user.
     *
     * @param record the raw data to save
     * @return the freshly saved record
     */
    public FinancialRecord createRecord(FinancialRecord record) {
        record.setUser(getCurrentUser());
        return repository.save(record);
    }

    /**
     * Grabs a specific chunk of records for the active user so we do not blow up the server memory.
     * Includes filtering capabilities.
     *
     * @param pageable how many to grab and how to sort them
     * @param startDate filter explicitly from this date 
     * @param endDate filter explicitly to this date
     * @param category filter by a specific category
     * @param type filter if it's an INCOME or EXPENSE
     * @return a single page of money moves
     */
    public Page<FinancialRecord> getAllRecords(Pageable pageable, LocalDate startDate, LocalDate endDate, String category, RecordType type) {
        return repository.findFilteredRecords(getCurrentUser().getId(), startDate, endDate, category, type, pageable);
    }     
    /**
     * Hunts down a single record using its database ID. Checks if the user actually owns it!
     *
     * @param id the unique number of the record
     * @return the record if we find it
     */
    public FinancialRecord getRecordById(Long id) {
        FinancialRecord record = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Record not found"));
        if (!record.getUser().getId().equals(getCurrentUser().getId())) {
             throw new SecurityException("Nah ah, that's not your record mate.");
        }
        return record;
    }

    /**
     * Swaps out the old details of a record with whatever new stuff you pass in.
     *
     * @param id the ID of the record to change
     * @param recordDetails the new data to apply
     * @return the fully updated record
     */
    public FinancialRecord updateRecord(Long id, FinancialRecord recordDetails) {
        FinancialRecord record = getRecordById(id);
        record.setAmount(recordDetails.getAmount());
        record.setType(recordDetails.getType());
        record.setCategory(recordDetails.getCategory());
        record.setDate(recordDetails.getDate());
        record.setNotes(recordDetails.getNotes());
        return repository.save(record);
    }

    /**
     * Nukes a record right out of the database.
     *
     * @param id the ID of the record to delete
     */
    public void deleteRecord(Long id) {
        repository.deleteById(id);
    }
}
