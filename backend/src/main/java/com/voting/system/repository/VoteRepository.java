package com.voting.system.repository;

import com.voting.system.entity.Poll;
import com.voting.system.entity.User;
import com.voting.system.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByPollAndUser(Poll poll, User user);
    List<Vote> findByPollIdAndUserId(Long pollId, Long userId);
    Long countByPollIdAndUserId(Long pollId, Long userId);
    boolean existsByPollIdAndUserId(Long pollId, Long userId);
    List<Vote> findByPollId(Long pollId);
    
    @Query("SELECT v FROM Vote v WHERE v.poll.id = :pollId AND v.user.id = :userId")
    List<Vote> findByPollIdAndUserIdFetchOption(@Param("pollId") Long pollId, @Param("userId") Long userId);
}
