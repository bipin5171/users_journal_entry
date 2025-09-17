package com.example.MyProject1.controller;

import com.example.MyProject1.entity.JournalEntry;
import com.example.MyProject1.entity.User;
import com.example.MyProject1.service.JournalEntryService;
import com.example.MyProject1.service.UserService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(JournalEntryController.class);

    // GET: localhost:8080/journal
    @GetMapping("/{userName}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String userName) {

        User user = userService.findByUserName(userName);

        List<JournalEntry> all = user.getJournalentry();

        if(all != null && !all.isEmpty()){
            return  new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




    @PostMapping("/{userName}")
    public ResponseEntity<?> createEntry(@PathVariable String userName,
                                         @RequestBody JournalEntry myEntry) {
        try {
            JournalEntry saved = journalEntryService.saveEntry(myEntry, userName);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // User not found or invalid input
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            // Any other errors
            logger.error("createEntry failed for user {}: ", userName, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }



    // GET: localhost:8080/journal/id/123
    @GetMapping("/id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);

        if (journalEntry.isPresent()) {
            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{userName}/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable String userName,
                                                    @PathVariable ObjectId myId) {

        User user = userService.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // 1. Remove the JournalEntry reference from User
        boolean removed = user.getJournalentry()
                .removeIf(entry -> entry.getId() != null && entry.getId().equals(myId));

        if (removed) {
            userService.saveEntry(user); // persist updated user
        }

        // 2. Delete the JournalEntry document
        journalEntryService.DeleteById(myId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userName}/{myId}")
    public ResponseEntity<JournalEntry> updateById(@PathVariable String userName, @PathVariable ObjectId myId, @RequestBody JournalEntry newEntry) {
        JournalEntry old = journalEntryService.findById(myId).orElse(null);

        if (old != null) {
            old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("") ? newEntry.getTitle() : old.getTitle());
            old.setContent(newEntry.getContent() != null && !newEntry.getContent().equals("") ? newEntry.getContent() : old.getContent());
            journalEntryService.saveEntry(old, userName);
            return new ResponseEntity<>(old, HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

}
