package com.voting.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollResponse {
    private Long id;
    private String title;
    private String description;
    private String creatorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String voteType;
    private Boolean allowMultipleVotes;
    private Boolean isAnonymous;
    private Boolean showResultsRealtime;
    private String status;
    private Integer maxVotesPerUser;
    private Integer totalVotes;
    private Boolean hasVoted;
    private List<OptionResponse> options;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionResponse {
        private Long id;
        private String text;
        private Integer displayOrder;
        private Integer voteCount;
        private Double percentage;
    }
}
