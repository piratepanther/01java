package com.voting.system.controller;

import com.voting.system.dto.request.PollRequest;
import com.voting.system.dto.response.ApiResponse;
import com.voting.system.dto.response.PollResponse;
import com.voting.system.entity.Poll;
import com.voting.system.entity.User;
import com.voting.system.service.AuthService;
import com.voting.system.service.PollService;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final PollService pollService;
    private final AuthService authService;
    
    @PostMapping("/polls")
    public ResponseEntity<ApiResponse<PollResponse>> createPoll(@Valid @RequestBody PollRequest request) {
        User user = authService.getCurrentUser();
        PollResponse response = pollService.createPoll(request, user);
        return ResponseEntity.ok(ApiResponse.success("投票项目创建成功", response));
    }
    
    @GetMapping("/polls")
    public ResponseEntity<ApiResponse<Page<PollResponse>>> getAllPolls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        User user = authService.getCurrentUser();
        
        Page<PollResponse> polls;
        if (status != null && !status.isEmpty()) {
            polls = pollService.getPollsByStatus(Poll.Status.valueOf(status), pageable, user);
        } else {
            polls = pollService.getPollsByStatus(Poll.Status.ACTIVE, pageable, user);
        }
        
        return ResponseEntity.ok(ApiResponse.success(polls));
    }
    
    @PutMapping("/polls/{id}")
    public ResponseEntity<ApiResponse<PollResponse>> updatePoll(
            @PathVariable Long id, 
            @Valid @RequestBody PollRequest request) {
        User user = authService.getCurrentUser();
        PollResponse response = pollService.updatePoll(id, request, user);
        return ResponseEntity.ok(ApiResponse.success("投票项目更新成功", response));
    }
    
    @DeleteMapping("/polls/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePoll(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        pollService.deletePoll(id, user);
        return ResponseEntity.ok(ApiResponse.success("投票项目删除成功", null));
    }
    
    @PostMapping("/polls/{id}/activate")
    public ResponseEntity<ApiResponse<PollResponse>> activatePoll(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        PollResponse response = pollService.activatePoll(id, user);
        return ResponseEntity.ok(ApiResponse.success("投票项目已激活", response));
    }
    
    @PostMapping("/polls/{id}/close")
    public ResponseEntity<ApiResponse<PollResponse>> closePoll(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        PollResponse response = pollService.closePoll(id, user);
        return ResponseEntity.ok(ApiResponse.success("投票项目已关闭", response));
    }
}
