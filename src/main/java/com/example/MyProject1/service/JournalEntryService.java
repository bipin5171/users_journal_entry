package com.example.MyProject1.service;

import com.example.MyProject1.Repository.JournalEntryRepository;
import com.example.MyProject1.entity.JournalEntry;
import com.example.MyProject1.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;

    @Autowired
    public JournalEntryService(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    @Autowired
    private UserService userService;

    @Transactional
    public JournalEntry saveEntry(JournalEntry journalEntry, String userName) {
        // 1. Find the user
        User user = userService.findByUserName(userName);
        if (user == null) {
            throw new IllegalArgumentException("User '" + userName + "' not found");

        }

        // 3. Save the journal entry
        JournalEntry saved = journalEntryRepository.save(journalEntry);

        // 4. Link this entry to the user
        user.getJournalentry().add(saved);

        // 5. Save the user back with updated journal entries
        userService.saveEntry(user);

        return saved;
    }

    public List<JournalEntry> getAll(){

        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId myId){

        return journalEntryRepository.findById(myId);
    }

    public void DeleteById(ObjectId myId){
        journalEntryRepository.deleteById(myId);
    }
}
