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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
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
    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        String username = principal.getName(); // 👈 comes from the authenticated user

        User user = userService.findByUserName(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<JournalEntry> all = user.getJournalentry();

        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




    @PostMapping("/entries")
    public ResponseEntity<?> createEntry(Principal principal,
                                         @RequestBody JournalEntry myEntry) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        String username = principal.getName(); // comes from Spring Security auth

        try {
            JournalEntry saved = journalEntryService.saveEntry(myEntry, username);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId().toString())
                    .toUri();

            return ResponseEntity.created(location).body(saved);

        } catch (IllegalArgumentException e) {
            logger.warn("User not found while creating entry: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            logger.error("createEntry failed for user {}: ", username, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{myId}")
    public ResponseEntity<JournalEntry> updateById(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry) {
        // Get currently logged-in username from Basic Auth
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        // Fetch the user from DB
        User user = userService.findByUserName(currentUserName);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Find the journal entry
        JournalEntry old = journalEntryService.findById(myId).orElse(null);
        if (old != null) {
            // Check if the entry belongs to the authenticated user
            boolean belongsToUser = user.getJournalentry().stream().anyMatch(entry -> entry.getId().equals(myId));
            if (!belongsToUser) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // Update fields if provided
            old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty() ? newEntry.getTitle() : old.getTitle());
            old.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : old.getContent());

            journalEntryService.saveEntry(old, currentUserName);
            return new ResponseEntity<>(old, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET: localhost:8080/journal/123
    @GetMapping("/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId) {
        // Get currently logged-in username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        // Fetch user
        User user = userService.findByUserName(currentUserName);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Find the journal entry
        Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);

        // Check ownership
        if (journalEntry.isPresent() && user.getJournalentry().stream()
                .anyMatch(entry -> entry.getId().equals(myId))) {
            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    // DELETE: localhost:8080/journal/{myId}
    @DeleteMapping("/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId) {
        // Get currently logged-in username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        // Fetch user
        User user = userService.findByUserName(currentUserName);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Check ownership and remove reference
        boolean removed = user.getJournalentry().removeIf(entry -> entry.getId() != null && entry.getId().equals(myId));

        if (!removed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // entry not owned by user
        }

        // Persist updated user
        userService.saveUser(user);

        // Delete the JournalEntry document
        journalEntryService.DeleteById(myId);

        return ResponseEntity.noContent().build();
    }


}
