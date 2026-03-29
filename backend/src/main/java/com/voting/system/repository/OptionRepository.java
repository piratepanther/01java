package com.voting.system.repository;

import com.voting.system.entity.Option;
import com.voting.system.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByPollOrderByDisplayOrderAsc(Poll poll);
    List<Option> findByPollIdOrderByDisplayOrderAsc(Long pollId);
    
    @Modifying
    @Query("UPDATE Option o SET o.voteCount = o.voteCount + 1 WHERE o.id = :optionId")
    void incrementVoteCount(@Param("optionId") Long optionId);
    
    @Query("SELECT SUM(o.voteCount) FROM Option o WHERE o.poll.id = :pollId")
    Integer getTotalVotesByPollId(@Param("pollId") Long pollId);
    
    void deleteByPollId(Long pollId);
}
