package com.example.MyProject1.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;
    @Test
    public void method1() {
        assertEquals(4, 2 + 2); // make sure the test logic is correct
        assertNotNull(userService.findByUserName("Bipin1"));
    }
}
