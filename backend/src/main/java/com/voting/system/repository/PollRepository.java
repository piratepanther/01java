package com.voting.system.repository;

import com.voting.system.entity.Poll;
import com.voting.system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    Page<Poll> findByStatus(Poll.Status status, Pageable pageable);
    Page<Poll> findByCreator(User creator, Pageable pageable);
    
    @Query("SELECT p FROM Poll p WHERE p.status = :status AND p.startTime <= :now AND p.endTime >= :now")
    List<Poll> findActivePolls(@Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM Poll p WHERE p.status = 'ACTIVE' AND p.startTime <= :now AND p.endTime >= :now")
    Page<Poll> findActivePolls(@Param("now") LocalDateTime now, Pageable pageable);
    
    @Query("SELECT p FROM Poll p WHERE p.endTime < :now AND p.status = 'ACTIVE'")
    List<Poll> findExpiredPolls(@Param("now") LocalDateTime now);
}
