package com.example.demo.controller;

import com.example.demo.dto.UserRechargeDTO;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/recharge")
    public ResponseEntity<?> recharge(@RequestBody UserRechargeDTO dto) {
        userService.rechargeUser(dto);
        return ResponseEntity.ok().build();
    }
}

