package com.voting.system.service;

import com.voting.system.dto.request.PollRequest;
import com.voting.system.dto.request.VoteRequest;
import com.voting.system.dto.response.PollResponse;
import com.voting.system.dto.response.VoteResultResponse;
import com.voting.system.entity.*;
import com.voting.system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollService {
    
    private final PollRepository pollRepository;
    private final OptionRepository optionRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final IpVoteRecordRepository ipVoteRecordRepository;
    
    @Transactional
    public PollResponse createPoll(PollRequest request, User creator) {
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new RuntimeException("结束时间不能早于开始时间");
        }
        
        Poll poll = Poll.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .creator(creator)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .voteType(Poll.VoteType.valueOf(request.getVoteType()))
                .allowMultipleVotes(request.getAllowMultipleVotes())
                .isAnonymous(request.getIsAnonymous())
                .showResultsRealtime(request.getShowResultsRealtime())
                .maxVotesPerUser(request.getMaxVotesPerUser())
                .status(Poll.Status.DRAFT)
                .build();
        
        poll = pollRepository.save(poll);
        
        for (int i = 0; i < request.getOptions().size(); i++) {
            Option option = Option.builder()
                    .poll(poll)
                    .text(request.getOptions().get(i))
                    .displayOrder(i)
                    .build();
            optionRepository.save(option);
        }
        
        return convertToResponse(poll, null);
    }
    
    @Transactional
    public PollResponse updatePoll(Long id, PollRequest request, User user) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("投票项目不存在"));
        
        if (!poll.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("无权修改此投票项目");
        }
        
        if (poll.getStatus() == Poll.Status.ACTIVE) {
            throw new RuntimeException("活跃状态的投票项目不能修改");
        }
        
        poll.setTitle(request.getTitle());
        poll.setDescription(request.getDescription());
        poll.setStartTime(request.getStartTime());
        poll.setEndTime(request.getEndTime());
        poll.setVoteType(Poll.VoteType.valueOf(request.getVoteType()));
        poll.setAllowMultipleVotes(request.getAllowMultipleVotes());
        poll.setIsAnonymous(request.getIsAnonymous());
        poll.setShowResultsRealtime(request.getShowResultsRealtime());
        poll.setMaxVotesPerUser(request.getMaxVotesPerUser());
        
        poll.getOptions().clear();
        optionRepository.deleteByPollId(poll.getId());
        
        for (int i = 0; i < request.getOptions().size(); i++) {
            Option option = Option.builder()
                    .poll(poll)
                    .text(request.getOptions().get(i))
                    .displayOrder(i)
                    .build();
            optionRepository.save(option);
        }
        
        poll = pollRepository.save(poll);
        return convertToResponse(poll, user);
    }
    
    @Transactional
    public void deletePoll(Long id, User user) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("投票项目不存在"));
        
        if (!poll.getCreator().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("无权删除此投票项目");
        }
        
        pollRepository.delete(poll);
    }
    
    @Transactional
    public PollResponse activatePoll(Long id, User user) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("投票项目不存在"));
        
        if (!poll.getCreator().getId().equals(user.getId())) {
            throw new RuntimeException("无权激活此投票项目");
        }
        
        poll.setStatus(Poll.Status.ACTIVE);
        poll = pollRepository.save(poll);
        
        return convertToResponse(poll, user);
    }
    
    @Transactional
    public PollResponse closePoll(Long id, User user) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("投票项目不存在"));
        
        if (!poll.getCreator().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("无权关闭此投票项目");
        }
        
        poll.setStatus(Poll.Status.CLOSED);
        poll = pollRepository.save(poll);
        
        return convertToResponse(poll, user);
    }
    
    public Page<PollResponse> getActivePolls(Pageable pageable, User user) {
        LocalDateTime now = LocalDateTime.now();
        Page<Poll> polls = pollRepository.findActivePolls(now, pageable);
        return polls.map(poll -> convertToResponse(poll, user));
    }
    
    public Page<PollResponse> getPollsByStatus(Poll.Status status, Pageable pageable, User user) {
        Page<Poll> polls = pollRepository.findByStatus(status, pageable);
        return polls.map(poll -> convertToResponse(poll, user));
    }
    
    public Page<PollResponse> getPollsByCreator(User creator, Pageable pageable) {
        Page<Poll> polls = pollRepository.findByCreator(creator, pageable);
        return polls.map(poll -> convertToResponse(poll, creator));
    }
    
    public PollResponse getPollById(Long id, User user) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("投票项目不存在"));
        return convertToResponse(poll, user);
    }
    
    @Transactional
    public VoteResultResponse castVote(VoteRequest request, User user, String ipAddress, String userAgent) {
        Poll poll = pollRepository.findById(request.getPollId())
                .orElseThrow(() -> new RuntimeException("投票项目不存在"));
        
        if (poll.getStatus() != Poll.Status.ACTIVE) {
            throw new RuntimeException("投票项目未激活或已结束");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(poll.getStartTime()) || now.isAfter(poll.getEndTime())) {
            throw new RuntimeException("不在投票时间范围内");
        }
        
        if (!poll.getAllowMultipleVotes()) {
            boolean hasVoted = voteRepository.existsByPollIdAndUserId(poll.getId(), user.getId());
            if (hasVoted) {
                throw new RuntimeException("您已经投过票了");
            }
        } else {
            Long voteCount = voteRepository.countByPollIdAndUserId(poll.getId(), user.getId());
            if (voteCount >= poll.getMaxVotesPerUser()) {
                throw new RuntimeException("您已达到最大投票次数");
            }
        }
        
        long ipVoteCount = ipVoteRecordRepository.countByIpAddressAndPollId(ipAddress, poll.getId());
        if (ipVoteCount >= 10) {
            throw new RuntimeException("该IP投票次数已达上限");
        }
        
        List<Option> options = optionRepository.findAllById(request.getOptionIds());
        if (options.size() != request.getOptionIds().size()) {
            throw new RuntimeException("部分选项不存在");
        }
        
        for (Option option : options) {
            if (!option.getPoll().getId().equals(poll.getId())) {
                throw new RuntimeException("选项不属于该投票项目");
            }
        }
        
        if (poll.getVoteType() == Poll.VoteType.SINGLE && request.getOptionIds().size() > 1) {
            throw new RuntimeException("该投票项目只能选择一个选项");
        }
        
        for (Option option : options) {
            Vote vote = Vote.builder()
                    .poll(poll)
                    .user(user)
                    .option(option)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();
            voteRepository.save(vote);
            
            optionRepository.incrementVoteCount(option.getId());
        }
        
        IpVoteRecord ipRecord = IpVoteRecord.builder()
                .ipAddress(ipAddress)
                .poll(poll)
                .build();
        ipVoteRecordRepository.save(ipRecord);
        
        Integer totalVotes = optionRepository.getTotalVotesByPollId(poll.getId());
        
        return VoteResultResponse.builder()
                .pollId(poll.getId())
                .pollTitle(poll.getTitle())
                .totalVotes(totalVotes)
                .votedAt(LocalDateTime.now())
                .message("投票成功")
                .build();
    }
    
    public PollResponse getPollResults(Long pollId, User user) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("投票项目不存在"));
        
        if (!poll.getShowResultsRealtime() && poll.getStatus() != Poll.Status.CLOSED) {
            if (user == null || !poll.getCreator().getId().equals(user.getId())) {
                throw new RuntimeException("投票尚未结束，无法查看结果");
            }
        }
        
        return convertToResponse(poll, user);
    }
    
    private PollResponse convertToResponse(Poll poll, User currentUser) {
        List<Option> options = optionRepository.findByPollIdOrderByDisplayOrderAsc(poll.getId());
        Integer totalVotes = optionRepository.getTotalVotesByPollId(poll.getId());
        
        boolean hasVoted = false;
        if (currentUser != null) {
            hasVoted = voteRepository.existsByPollIdAndUserId(poll.getId(), currentUser.getId());
        }
        
        List<PollResponse.OptionResponse> optionResponses = options.stream()
                .map(option -> {
                    double percentage = 0.0;
                    if (totalVotes != null && totalVotes > 0) {
                        percentage = (option.getVoteCount() * 100.0) / totalVotes;
                    }
                    return PollResponse.OptionResponse.builder()
                            .id(option.getId())
                            .text(option.getText())
                            .displayOrder(option.getDisplayOrder())
                            .voteCount(option.getVoteCount())
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
        
        return PollResponse.builder()
                .id(poll.getId())
                .title(poll.getTitle())
                .description(poll.getDescription())
                .creatorName(poll.getCreator().getUsername())
                .startTime(poll.getStartTime())
                .endTime(poll.getEndTime())
                .voteType(poll.getVoteType().name())
                .allowMultipleVotes(poll.getAllowMultipleVotes())
                .isAnonymous(poll.getIsAnonymous())
                .showResultsRealtime(poll.getShowResultsRealtime())
                .status(poll.getStatus().name())
                .maxVotesPerUser(poll.getMaxVotesPerUser())
                .totalVotes(totalVotes != null ? totalVotes : 0)
                .hasVoted(hasVoted)
                .options(optionResponses)
                .build();
    }
}
