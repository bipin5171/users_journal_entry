package com.example.MyProject1.controller;

import com.example.MyProject1.entity.User;
import com.example.MyProject1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

     @Autowired
     private UserService userService;
    @GetMapping("/all-users")
    public List<User> getAllUsers() {

        return userService.getAllUser();
    }

    @PostMapping("/create-admin")
    public ResponseEntity<User> createAdmin(@RequestBody User user) {
        userService.saveAdmin(user);
        return ResponseEntity.ok(user);
    }


}
