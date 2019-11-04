package chilivote.LogicHandlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import chilivote.Entities.Answer;
import chilivote.Entities.Chilivote;
import chilivote.Entities.Follow;
import chilivote.Entities.User;
import chilivote.Entities.Vote;
import chilivote.Exceptions.ChilivoteNotFoundException;
import chilivote.Exceptions.ForbiddenOperationException;
import chilivote.Exceptions.UserNotFoundException;
import chilivote.JWT.JwtTokenUtil;
import chilivote.Models.DTOs.AnswerVoteDTO;
import chilivote.Models.DTOs.ChilivoteDTOBE;
import chilivote.Models.DTOs.ChilivoteDTOUI;
import chilivote.Models.DTOs.ChilivoteDTOUIUpdate;
import chilivote.Models.DTOs.ChilivoteVotableDTO;
import chilivote.Models.DTOs.MyChilivoteDTO;
import chilivote.Repositories.ChilivoteRepository;
import chilivote.Repositories.UserRepository;

public class ChilivoteLogicHandler
{
    private ChilivoteRepository chilivoteRepository;
    private UserRepository userRepository;
    private JwtTokenUtil jwtTokenUtil;

    public ChilivoteLogicHandler(ChilivoteRepository chilivoteRepository, 
    JwtTokenUtil jwtTokenUtil, UserRepository userRepository)
    {
        this.chilivoteRepository = chilivoteRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    public List<MyChilivoteDTO> GetMyChilivotes(String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        List<Chilivote> Entities = chilivoteRepository.findByUserId(id);
        
        List<MyChilivoteDTO> Result = new ArrayList<MyChilivoteDTO>();

        for(Chilivote entity : Entities)
        {
            Result.add(this.ToMyChilivoteDTO(entity));
        }

        return Result;
    }

    public List<ChilivoteVotableDTO> GetFeed(String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        
        User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        Set<Follow> UserIFollow = user.getFollowing();

        List<ChilivoteVotableDTO> Result = new ArrayList<ChilivoteVotableDTO>();

        for(Follow following: UserIFollow)
        {
            Set<Chilivote> chilivotes = following.getTo().getChilivotes();
            for(Chilivote chilivote: chilivotes)
            {
                Result.add(this.ToChilivoteVotableDTO(chilivote, user));
            }
        }
        return Result;
    }

    public ChilivoteDTOBE CreateChilivote(ChilivoteDTOUI DTO, String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        Chilivote entity = ToChilivoteEntity(DTO, user);

        Chilivote SavedEntity = chilivoteRepository.save(entity);

        return ToChilivoteDTOBE(SavedEntity);
    }

    public ChilivoteDTOBE UpdateChilivote(ChilivoteDTOUIUpdate DTO, String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        Chilivote entity = chilivoteRepository.findById(DTO.id)
        .orElseThrow(() -> new ChilivoteNotFoundException(DTO.id));

        if(!userOwnsChilivote(user, entity))
        throw new ForbiddenOperationException();

        Set<Answer> answers = entity.getAnswers();
        for(Answer answer : answers)
        {
            answer.setUrl(DTO.answers.stream().filter( (a) -> a.id == answer.getId()).findFirst().get().url);
        }

        Chilivote SavedEntity = chilivoteRepository.save(entity);

        return ToChilivoteDTOBE(SavedEntity);
    }

    public boolean DeleteChilivote(Integer ChilivoteId, String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        Chilivote entity = chilivoteRepository.findById(ChilivoteId)
        .orElseThrow(() -> new ChilivoteNotFoundException(ChilivoteId));

        if(!userOwnsChilivote(user, entity))
        throw new ForbiddenOperationException();

        chilivoteRepository.deleteById(ChilivoteId);
        return true;
    }

    ///////////////////////// Protected ////////////////////////////////

    protected boolean userOwnsChilivote(User user, Chilivote chilivote)
    {
        return user.getChilivotes().contains(chilivote);
    }

    protected Chilivote ToChilivoteEntity(ChilivoteDTOUI DTO, User user)
    {
        Chilivote entity = new Chilivote();

        Set<Answer> answers = getAnswersSet(DTO);

        entity.setTitle(DTO.title);
        entity.setAnswers(answers);    
        entity.setUser(user);

        for(Answer answer : answers)
        {
            answer.setChilivote(entity);
            answer.setUser(user);
        }

        return entity;
    }

    protected Set<Answer> getAnswersSet(ChilivoteDTOUI DTO)
    {
        Set<Answer> answers = new HashSet<Answer>();
        answers.add(new Answer("", DTO.answerLeft));
        answers.add(new Answer("", DTO.answerRight));
        return answers;
    }

    protected ChilivoteDTOBE ToChilivoteDTOBE(Chilivote entity)
    {
        ChilivoteDTOBE DTO = new ChilivoteDTOBE();
        List<Answer> answers = new ArrayList<Answer>(entity.getAnswers());
        
        DTO.answerLeft = answers.get(0).getUrl();
        DTO.answerRight = answers.get(1).getUrl();
        DTO.title = entity.getTitle();
        DTO.created_at = entity.getCreated_at();
        return DTO;
    }

    protected MyChilivoteDTO ToMyChilivoteDTO(Chilivote entity)
    {
        MyChilivoteDTO DTO = new MyChilivoteDTO();
        List<Answer> answers = new ArrayList<Answer>(entity.getAnswers());
        
        DTO.answerLeft = new AnswerVoteDTO();
        DTO.answerLeft.url = answers.get(0).getUrl();
        DTO.answerLeft.votes = answers.get(0).getVotes().size();
        DTO.answerLeft.id = answers.get(0).getId();

        DTO.answerRight = new AnswerVoteDTO();
        DTO.answerRight.url = answers.get(1).getUrl();
        DTO.answerRight.votes = answers.get(1).getVotes().size();
        DTO.answerRight.id = answers.get(1).getId();

        DTO.title = entity.getTitle();
        DTO.created_at = entity.getCreated_at();
        DTO.id = entity.getId();

        return DTO;
    }

    protected ChilivoteVotableDTO ToChilivoteVotableDTO(Chilivote entity, User user)
    {
        ChilivoteVotableDTO DTO = new ChilivoteVotableDTO();
        List<Answer> answers = new ArrayList<Answer>(entity.getAnswers());
        
        DTO.answerLeft = new AnswerVoteDTO();
        DTO.answerLeft.url = answers.get(0).getUrl();
        DTO.answerLeft.votes = answers.get(0).getVotes().size();
        DTO.answerLeft.voted = !answers.get(0).getVotes().stream().filter((vote) -> 
        vote.getUser().getId() == user.getId()).findFirst().isEmpty();
        DTO.answerLeft.id = answers.get(0).getId();

        DTO.answerRight = new AnswerVoteDTO();
        DTO.answerRight.url = answers.get(1).getUrl();
        DTO.answerRight.votes = answers.get(1).getVotes().size();
        DTO.answerRight.voted = !answers.get(1).getVotes().stream().filter((vote) -> 
        vote.getUser().getId() == user.getId()).findFirst().isEmpty();
        DTO.answerRight.id = answers.get(1).getId();

        DTO.title = entity.getTitle();
        DTO.created_at = entity.getCreated_at();
        DTO.id = entity.getId();
        DTO.username = entity.getUser().getUsername();

        return DTO;
    }
}