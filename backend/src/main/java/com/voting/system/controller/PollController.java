package com.voting.system.controller;

import com.voting.system.dto.request.PollRequest;
import com.voting.system.dto.request.VoteRequest;
import com.voting.system.dto.response.ApiResponse;
import com.voting.system.dto.response.PollResponse;
import com.voting.system.dto.response.VoteResultResponse;
import com.voting.system.entity.Poll;
import com.voting.system.entity.User;
import com.voting.system.service.AuthService;
import com.voting.system.service.PollService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/polls")
@RequiredArgsConstructor
public class PollController {
    
    private final PollService pollService;
    private final AuthService authService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PollResponse>> createPoll(@Valid @RequestBody PollRequest request) {
        User user = authService.getCurrentUser();
        PollResponse response = pollService.createPoll(request, user);
        return ResponseEntity.ok(ApiResponse.success("投票项目创建成功", response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PollResponse>> updatePoll(
            @PathVariable Long id, 
            @Valid @RequestBody PollRequest request) {
        User user = authService.getCurrentUser();
        PollResponse response = pollService.updatePoll(id, request, user);
        return ResponseEntity.ok(ApiResponse.success("投票项目更新成功", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePoll(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        pollService.deletePoll(id, user);
        return ResponseEntity.ok(ApiResponse.success("投票项目删除成功", null));
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PollResponse>> activatePoll(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        PollResponse response = pollService.activatePoll(id, user);
        return ResponseEntity.ok(ApiResponse.success("投票项目已激活", response));
    }
    
    @PostMapping("/{id}/close")
    public ResponseEntity<ApiResponse<PollResponse>> closePoll(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        PollResponse response = pollService.closePoll(id, user);
        return ResponseEntity.ok(ApiResponse.success("投票项目已关闭", response));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<PollResponse>>> getActivePolls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        User user = null;
        try {
            user = authService.getCurrentUser();
        } catch (Exception ignored) {}
        
        Page<PollResponse> polls = pollService.getActivePolls(pageable, user);
        return ResponseEntity.ok(ApiResponse.success(polls));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<PollResponse>>> getPollsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        User user = null;
        try {
            user = authService.getCurrentUser();
        } catch (Exception ignored) {}
        
        Page<PollResponse> polls = pollService.getPollsByStatus(Poll.Status.valueOf(status), pageable, user);
        return ResponseEntity.ok(ApiResponse.success(polls));
    }
    
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PollResponse>>> getMyPolls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        User user = authService.getCurrentUser();
        Page<PollResponse> polls = pollService.getPollsByCreator(user, pageable);
        return ResponseEntity.ok(ApiResponse.success(polls));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PollResponse>> getPollById(@PathVariable Long id) {
        User user = null;
        try {
            user = authService.getCurrentUser();
        } catch (Exception ignored) {}
        
        PollResponse response = pollService.getPollById(id, user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/vote")
    public ResponseEntity<ApiResponse<VoteResultResponse>> castVote(
            @Valid @RequestBody VoteRequest request,
            HttpServletRequest httpRequest) {
        User user = authService.getCurrentUser();
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        VoteResultResponse response = pollService.castVote(request, user, ipAddress, userAgent);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{id}/results")
    public ResponseEntity<ApiResponse<PollResponse>> getPollResults(@PathVariable Long id) {
        User user = null;
        try {
            user = authService.getCurrentUser();
        } catch (Exception ignored) {}
        
        PollResponse response = pollService.getPollResults(id, user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
