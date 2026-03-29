package com.voting.system.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class VoteRequest {
    @NotNull(message = "投票项目ID不能为空")
    private Long pollId;
    
    @NotEmpty(message = "选项不能为空")
    private List<Long> optionIds;
}
