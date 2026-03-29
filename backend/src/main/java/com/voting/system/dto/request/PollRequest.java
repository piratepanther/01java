package com.voting.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PollRequest {
    @NotBlank(message = "投票标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    @Size(max = 2000, message = "描述长度不能超过2000个字符")
    private String description;
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    @NotEmpty(message = "选项不能为空")
    @Size(min = 2, max = 20, message = "选项数量必须在2-20个之间")
    private List<String> options;
    
    private String voteType = "SINGLE";
    
    private Boolean allowMultipleVotes = false;
    
    private Boolean isAnonymous = false;
    
    private Boolean showResultsRealtime = true;
    
    private Integer maxVotesPerUser = 1;
}
