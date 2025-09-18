package com.example.MyProject1.controller;

import com.example.MyProject1.entity.JournalEntry;
import com.example.MyProject1.entity.User;
import com.example.MyProject1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private UserService userService;
    // POST: localhost:8080/journal
    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@RequestBody User user) {

        try {
            userService.saveUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

}
