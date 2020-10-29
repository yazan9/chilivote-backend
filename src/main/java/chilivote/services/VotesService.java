package chilivote.services;

import java.util.ArrayList;
import java.util.List;

import chilivote.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import chilivote.entities.AnswerEntity;
import chilivote.entities.ChilivoteEntity;
import chilivote.entities.UserEntity;
import chilivote.entities.VoteEntity;
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
    RolesService rolesService;

    public void vote(Integer answerId, String token)
    {
        UserEntity userEntity = userRepository.findById(jwtTokenUtil.getIdFromToken(token)).orElseThrow(() -> new UnauthorizedException());
        AnswerEntity answerEntity = answerRepository.findById(answerId).orElseThrow(() -> new EntityNotFoundException(answerId, "Answer"));
        doVote(userEntity, answerEntity);
    }

    private boolean hasVotedOnChilivote(Integer userId, Integer chilivoteId){
        return voteRepository.findByUserIdAndChilivoteId(userId, chilivoteId).orElse(null) != null;
    }

    private boolean hasVotedOnAnswer(Integer userId, Integer answerId){
        return voteRepository.findByUserIdAndAnswerId(userId, answerId).orElse(null) != null;
    }

    private ChilivoteEntity doVote(UserEntity userEntity, AnswerEntity answerEntity){
        ChilivoteEntity chilivoteEntity = answerEntity.getChilivote();

        if(this.hasVotedOnAnswer(userEntity.getId(), answerEntity.getId()))
            throw new DuplicateVoteException();

        if(this.hasVotedOnChilivote(userEntity.getId(), chilivoteEntity.getId()))
            this.doUnvote(userEntity, answerEntity);

        VoteEntity vote = new VoteEntity();
        vote.setAnswer(answerEntity);
        vote.setUser(userEntity);
        vote.setChilivote(chilivoteEntity);

        try{
            voteRepository.save(vote);
            this.rolesService.updateRole(userEntity);
            this.rolesService.updateRole(answerEntity.getUser());
        }
        catch(DataIntegrityViolationException e)
        {
            throw new DuplicateVoteException();
        }
        this.notificationsService.createNotification(chilivoteEntity);
        return chilivoteEntity;
    }

    public List<AnswerVotePairDTO> voteAndGetAnswers(Integer answerId, String token)
    {
        UserEntity userEntity = userRepository.findById(jwtTokenUtil.getIdFromToken(token)).orElseThrow(() -> new UnauthorizedException());
        AnswerEntity answerEntity = answerRepository.findById(answerId).orElseThrow(() -> new EntityNotFoundException(answerId, "Answer"));
        List<AnswerVotePairDTO> AnswerVotePairs = new ArrayList<AnswerVotePairDTO>();
        ChilivoteEntity chilivote = doVote(userEntity, answerEntity);
        List<AnswerEntity> answerEntities = chilivote.getAnswers();
        for(AnswerEntity answer : answerEntities){
            AnswerVotePairDTO dto = new AnswerVotePairDTO();
            dto.answerId = answer.getId();
            dto.votes = answer.getVotes().size();
            AnswerVotePairs.add(dto);
        }       
        
        return AnswerVotePairs;
    }

    public void unvote(Integer answerId, String token)
    {
        UserEntity user = userRepository.findById(jwtTokenUtil.getIdFromToken(token)).orElseThrow(() -> new UnauthorizedException());
        AnswerEntity answerEntity = answerRepository.findById(answerId).orElseThrow(() -> new EntityNotFoundException(answerId, "Answer"));
        doUnvote(user, answerEntity);
    }

    private void doUnvote(UserEntity userEntity, AnswerEntity answerEntity){
        UserEntity targetUser = answerRepository.findById(answerEntity.getId()).orElseThrow(() -> new EntityNotFoundException(answerEntity.getId(), "Answer")).getUser();
        VoteEntity voteToDelete = voteRepository.findByUserIdAndChilivoteId(userEntity.getId(), answerEntity.getChilivote().getId()).orElse(null);

        if(voteToDelete == null)
            return;

        voteRepository.deleteById(voteToDelete.getId());

        this.rolesService.updateRole(userEntity);
        this.rolesService.updateRole(targetUser);
    }
}