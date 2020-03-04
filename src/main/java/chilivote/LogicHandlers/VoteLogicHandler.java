package chilivote.LogicHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import chilivote.Entities.Answer;
import chilivote.Entities.Chilivote;
import chilivote.Entities.User;
import chilivote.Entities.Vote;
import chilivote.Exceptions.AnswerNotFoundException;
import chilivote.Exceptions.DuplicateVoteException;
import chilivote.Exceptions.UserNotFoundException;
import chilivote.JWT.JwtTokenUtil;
import chilivote.Models.DTOs.AnswerVotePairDTO;
import chilivote.Repositories.AnswerRepository;
import chilivote.Repositories.UserRepository;
import chilivote.Repositories.VoteRepository;

@Component
public class VoteLogicHandler
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    RoleLogicHandler roleLogicHandler;

    public String vote(Integer answerId, String token)
    {
        doVote(answerId, token);  
        return "ok";
    }

    public List<AnswerVotePairDTO> voteAndGetAnswers(Integer answerId, String token)
    {
        List<AnswerVotePairDTO> AnswerVotePairs = new ArrayList<AnswerVotePairDTO>();
        Chilivote chilivote = doVote(answerId, token);
        Set<Answer> answers = chilivote.getAnswers();
        for(Answer answer : answers){
            AnswerVotePairDTO dto = new AnswerVotePairDTO();
            dto.answerId = answer.getId();
            dto.votes = answer.getVotes().size();
            AnswerVotePairs.add(dto);
        }       
        
        return AnswerVotePairs;
    }

    private Chilivote doVote(Integer answerId, String token){
        Integer userId = jwtTokenUtil.getIdFromToken(token);
        
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

        Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new AnswerNotFoundException(answerId));

        Chilivote chilivote = answer.getChilivote();

        Vote vote = new Vote();
        vote.setAnswer(answer);
        vote.setUser(user);
        vote.setChilivote(chilivote);

        //Check if the user has already voted
        Optional<Vote> optional = chilivote.getVotes().stream().filter(v -> v.getUser().getId() == user.getId()).findFirst();
        if(!optional.isEmpty())
        {
            Vote UserPrevVote = optional.get();
            answer.getVotes().remove(UserPrevVote);
            UserPrevVote.setAnswer(null);
            UserPrevVote.setUser(null);
            UserPrevVote.setChilivote(null);
            voteRepository.deleteById(UserPrevVote.getId());
        }

        answer.getVotes().add(vote);

        try{
            answerRepository.save(answer);
            this.roleLogicHandler.updateRole(user);
            this.roleLogicHandler.updateRole(answer.getUser());
        }
        catch(DataIntegrityViolationException e)
        {
            throw new DuplicateVoteException();
        }
        return chilivote;
    }

    public String unvote(Integer answerId, String token)
    {
        Integer userId = jwtTokenUtil.getIdFromToken(token);
        
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

        Answer answer = answerRepository.findById(answerId)
        .orElseThrow(() -> new AnswerNotFoundException(answerId));

        Vote vote = answer.getVotes().stream().filter((v) -> v.getUser().equals(user)).findFirst().get();
        answer.getVotes().remove(vote);

        answerRepository.save(answer);
        this.roleLogicHandler.updateRole(user);
        this.roleLogicHandler.updateRole(answer.getUser());
    
        return "ok";
    }
}