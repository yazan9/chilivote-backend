package chilivote.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import chilivote.entities.AnswerEntity;
import chilivote.entities.ChilivoteEntity;
import chilivote.entities.UserEntity;
import chilivote.entities.VoteEntity;
import chilivote.exceptions.AnswerNotFoundException;
import chilivote.exceptions.DuplicateVoteException;
import chilivote.exceptions.UserNotFoundException;
import chilivote.jwt.JwtTokenUtil;
import chilivote.models.domain.AnswerVotePairDTO;
import chilivote.Repositories.AnswerRepository;
import chilivote.Repositories.UserRepository;
import chilivote.Repositories.VoteRepository;

@Component
public class VotesService
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
    private NotificationsService notificationsService;

    @Autowired
    RolesService roleLogicHandler;

    public String vote(Integer answerId, String token)
    {
        doVote(answerId, token);  
        return "ok";
    }

    public List<AnswerVotePairDTO> voteAndGetAnswers(Integer answerId, String token)
    {
        List<AnswerVotePairDTO> AnswerVotePairs = new ArrayList<AnswerVotePairDTO>();
        ChilivoteEntity chilivote = doVote(answerId, token);
        List<AnswerEntity> answerEntities = chilivote.getAnswers();
        for(AnswerEntity answerEntity : answerEntities){
            AnswerVotePairDTO dto = new AnswerVotePairDTO();
            dto.answerId = answerEntity.getId();
            dto.votes = answerEntity.getVotes().size();
            AnswerVotePairs.add(dto);
        }       
        
        return AnswerVotePairs;
    }

    private ChilivoteEntity doVote(Integer answerId, String token){
        Integer userId = jwtTokenUtil.getIdFromToken(token);
        
        UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

        AnswerEntity answerEntity = answerRepository.findById(answerId)
        .orElseThrow(() -> new AnswerNotFoundException(answerId));

        ChilivoteEntity chilivote = answerEntity.getChilivote();

        VoteEntity vote = new VoteEntity();
        vote.setAnswer(answerEntity);
        vote.setUser(user);
        vote.setChilivote(chilivote);

        //Check if the user has already voted
        Optional<VoteEntity> optional = chilivote.getVotes().stream().filter(v -> v.getUser().getId() == user.getId()).findFirst();
        if(!optional.isEmpty())
        {
            VoteEntity UserPrevVote = optional.get();
            answerEntity.getVotes().remove(UserPrevVote);
            UserPrevVote.setAnswer(null);
            UserPrevVote.setUser(null);
            UserPrevVote.setChilivote(null);
            voteRepository.deleteById(UserPrevVote.getId());
        }

        answerEntity.getVotes().add(vote);

        try{
            answerRepository.save(answerEntity);
            this.roleLogicHandler.updateRole(user);
            this.roleLogicHandler.updateRole(answerEntity.getUser());
        }
        catch(DataIntegrityViolationException e)
        {
            throw new DuplicateVoteException();
        }
        this.notificationsService.createNotification(chilivote);
        return chilivote;
    }

    public String unvote(Integer answerId, String token)
    {
        Integer userId = jwtTokenUtil.getIdFromToken(token);
        
        UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

        AnswerEntity answerEntity = answerRepository.findById(answerId)
        .orElseThrow(() -> new AnswerNotFoundException(answerId));

        VoteEntity vote = answerEntity.getVotes().stream().filter((v) -> v.getUser().equals(user)).findFirst().get();
        answerEntity.getVotes().remove(vote);

        answerRepository.save(answerEntity);
        this.roleLogicHandler.updateRole(user);
        this.roleLogicHandler.updateRole(answerEntity.getUser());
    
        return "ok";
    }
}