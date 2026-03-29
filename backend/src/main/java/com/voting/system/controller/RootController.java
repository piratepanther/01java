package com.voting.system.controller;

import com.voting.system.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {
    
    @GetMapping
    public ResponseEntity<ApiResponse<String>> getRoot() {
        return ResponseEntity.ok(ApiResponse.success("Voting System API is running"));
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> getHealth() {
        return ResponseEntity.ok(ApiResponse.success("OK"));
    }
}
