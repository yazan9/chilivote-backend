package chilivote.LogicHandlers;

import java.util.Optional;

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
import chilivote.Repositories.UserRepository;
import chilivote.Repositories.VoteRepository;

public class VoteLogicHandler
{
    private UserRepository userRepository;
    private JwtTokenUtil jwtTokenUtil;
    private AnswerRepository answerRepository;
    private VoteRepository voteRepository;

    public VoteLogicHandler(
        JwtTokenUtil jwtTokenUtil, UserRepository userRepository, AnswerRepository answerRepository, VoteRepository voteRepository)
    {
        this.answerRepository = answerRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
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