package com.voting.system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ip_vote_records", indexes = {
    @Index(name = "idx_ip_poll_time", columnList = "ip_address, poll_id, voted_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IpVoteRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    @ToString.Exclude
    private Poll poll;
    
    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;
    
    @PrePersist
    protected void onCreate() {
        votedAt = LocalDateTime.now();
    }
}
