package com.example.MyProject1.controller;

import com.example.MyProject1.entity.JournalEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HealthCheck {

    @GetMapping("/health-check")
    public String healthcheck(){
        return "Ok";
    }

}
