package com.example.MyProject1.controller;

import com.example.MyProject1.entity.JournalEntry;
import com.example.MyProject1.entity.User;
import com.example.MyProject1.service.JournalEntryService;
import com.example.MyProject1.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    // GET: localhost:8080/journal
    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUser();
    }

    @GetMapping("/{userName}")
    public ResponseEntity<User> getUserByUserName(@PathVariable String userName) {
        User responseUser = userService.findByUserName(userName);

        if (responseUser != null) {
            return new ResponseEntity<>(responseUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("/{userName}")
    public ResponseEntity<User> deleteUserByUserName(@PathVariable String userName) {
        try{
            userService.deleteByUserName(userName);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping("/{userName}")
    public ResponseEntity<?> updateUser(@PathVariable String userName, @RequestBody User user) {
        User userInDb = userService.findByUserName(userName);

        if (userInDb != null) {
            userInDb.setUserName(user.getUserName());
            userInDb.setPassword(user.getPassword());
            userService.saveUser(userInDb);
            return new ResponseEntity<>(userInDb, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
