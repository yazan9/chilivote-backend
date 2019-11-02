package chilivote.LogicHandlers;

import org.springframework.dao.DataIntegrityViolationException;

import chilivote.Entities.Answer;
import chilivote.Entities.Chilivote;
import chilivote.Entities.User;
import chilivote.Entities.Vote;
import chilivote.Exceptions.AnswerNotFoundException;
import chilivote.Exceptions.DuplicateVoteException;
import chilivote.Exceptions.UserNotFoundException;
import chilivote.JWT.JwtTokenUtil;
import chilivote.Repositories.AnswerRepository;
import chilivote.Repositories.ChilivoteRepository;
import chilivote.Repositories.UserRepository;
import chilivote.Repositories.VoteRepository;

public class VoteLogicHandler
{
    private UserRepository userRepository;
    private JwtTokenUtil jwtTokenUtil;
    private AnswerRepository answerRepository;

    public VoteLogicHandler(
        JwtTokenUtil jwtTokenUtil, UserRepository userRepository, AnswerRepository answerRepository)
    {
        this.answerRepository = answerRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    public String vote(Integer answerId, String token)
    {
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

        answer.getVotes().add(vote);

        try{
            answerRepository.save(answer);
        }
        catch(DataIntegrityViolationException e)
        {
            throw new DuplicateVoteException();
        }
    
        return "ok";
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
    
        return "ok";
    }
}