package com.voting.system.repository;

import com.voting.system.entity.IpVoteRecord;
import com.voting.system.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IpVoteRecordRepository extends JpaRepository<IpVoteRecord, Long> {
    Long countByIpAddressAndPollId(String ipAddress, Long pollId);
    
    @Query("SELECT COUNT(r) FROM IpVoteRecord r WHERE r.ipAddress = :ipAddress AND r.poll.id = :pollId AND r.votedAt >= :since")
    Long countRecentVotesByIpAndPoll(@Param("ipAddress") String ipAddress, @Param("pollId") Long pollId, @Param("since") LocalDateTime since);
    
    List<IpVoteRecord> findByIpAddressAndPollId(String ipAddress, Long pollId);
}
