package com.voting.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResultResponse {
    private Long pollId;
    private String pollTitle;
    private Integer totalVotes;
    private LocalDateTime votedAt;
    private String message;
}
