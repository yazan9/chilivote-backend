package chilivote.services;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chilivote.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import chilivote.entities.AnswerEntity;
import chilivote.entities.ChilivoteEntity;
import chilivote.entities.FollowEntity;
import chilivote.entities.UserEntity;
import chilivote.jwt.JwtTokenUtil;
import chilivote.models.domain.AnswerVoteDTO;
import chilivote.models.domain.ChilivoteDTOBE;
import chilivote.models.domain.ChilivoteDTOUI;
import chilivote.models.domain.ChilivoteDTOUIUpdate;
import chilivote.models.domain.ChilivoteVotableDTO;
import chilivote.models.domain.MyChilivoteDTO;
import chilivote.Repositories.ChilivoteRepository;
import chilivote.Repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ChilivoteService
{
    @Autowired
    private RolesService roleLogicHandler;
    
    @Autowired
    private ChilivoteRepository chilivoteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public List<MyChilivoteDTO> GetMyChilivotes(String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        List<ChilivoteEntity> Entities = chilivoteRepository.findByUserId(id).orElseThrow(() -> new EntityNotFoundException(id, "User"));
        List<MyChilivoteDTO> Result = new ArrayList<MyChilivoteDTO>();
        for(ChilivoteEntity entity : Entities)
        {
            Result.add(this.ToMyChilivoteDTO(entity));
        }
        return Result;
    }

    public MyChilivoteDTO getMyChilivote(String token, int chilivoteId)
    {
        Integer userId = jwtTokenUtil.getIdFromToken(token);
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        ChilivoteEntity chilivoteEntity = chilivoteRepository.findById(chilivoteId).orElseThrow(() -> new EntityNotFoundException(chilivoteId, "Chilivote"));

        if(!userOwnsChilivote(userEntity, chilivoteEntity)){
            throw new ForbiddenException();
        }

        return ToMyChilivoteDTO(chilivoteEntity);
    }

    public List<ChilivoteVotableDTO> GetFeed(String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        List<FollowEntity> UserIFollow = user.getFollowing();
        List<ChilivoteVotableDTO> Result = new ArrayList<ChilivoteVotableDTO>();

        for(FollowEntity following: UserIFollow)
        {
            List<ChilivoteEntity> chilivotes = following.getTo().getChilivotes();
            for(ChilivoteEntity chilivote: chilivotes)
            {
                if(!chilivote.isPrivate() && !user.getPreferences().hide.contains(chilivote.getUser().getId()))
                    Result.add(this.ToChilivoteVotableDTO(chilivote, user));
            }
        }
        return Result;
    }

    public List<ChilivoteVotableDTO> GetRandomFeed(String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        
        UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        Long q = chilivoteRepository.count();
        int idx = (int)(Math.random()*q);
        Page<ChilivoteEntity> randomChilivotes = chilivoteRepository.findAll(PageRequest.of(idx, 4));
        List<ChilivoteEntity> chilivotes = randomChilivotes.getContent();

        //returning default results
        if(chilivotes.size() == 0){
            Iterable<ChilivoteEntity> iterables = chilivoteRepository.findAll();
            chilivotes = new ArrayList<ChilivoteEntity>();
            for(ChilivoteEntity c: iterables)
            {
                chilivotes.add(c);
            }
        }

        List<ChilivoteVotableDTO> FinalResult = new ArrayList<ChilivoteVotableDTO>();
        for(ChilivoteEntity pagedChilivote: chilivotes)
        {
            if(user.getId() != pagedChilivote.getUser().getId()
            && !user.getPreferences().hide.contains(pagedChilivote.getUser().getId())
            && !pagedChilivote.isPrivate())
                FinalResult.add(this.ToChilivoteVotableDTO(pagedChilivote, user));
        }
        return FinalResult;
    }

    public List<ChilivoteVotableDTO> GetFireChilivote(String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        
        UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        Long q = chilivoteRepository.count();
        int idx = (int)(Math.random()*q);
        Page<ChilivoteEntity> randomChilivotes = chilivoteRepository.findAll(PageRequest.of(idx, 4));
        List<ChilivoteEntity> chilivotes = randomChilivotes.getContent();

        //returning default results
        if(chilivotes.size() == 0){
            Iterable<ChilivoteEntity> iterables = chilivoteRepository.findAll();
            chilivotes = new ArrayList<ChilivoteEntity>();
            for(ChilivoteEntity c: iterables)
            {
                chilivotes.add(c);
            }
        }

        //get voted ids
        Set<Integer> VotedChilivoteIds = user.getVotes().stream().
        map(vote -> vote.getChilivote().getId()).collect(Collectors.toSet());

        //filter chilivotes by 2 conditions
        List<ChilivoteEntity> filteredChilivotes = chilivotes.stream().filter(chilivote ->
            chilivote.getUser().getId() != user.getId() &&
            !VotedChilivoteIds.contains(chilivote.getId())
        ).collect(Collectors.toList());

        List<ChilivoteVotableDTO> Result = new ArrayList<ChilivoteVotableDTO>();

        for(ChilivoteEntity chilivote: filteredChilivotes)
        {
            Result.add(this.ToChilivoteVotableDTO(chilivote, user));
        }
        return Result;
    }

    public List<ChilivoteVotableDTO> GetPrivateChilivotes(String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        List<ChilivoteEntity> chilivotes = chilivoteRepository.findByIsPrivate(true).orElse(new ArrayList<>());

        //get voted ids
        Set<Integer> VotedChilivoteIds = user.getVotes().stream().
                map(vote -> vote.getChilivote().getId()).collect(Collectors.toSet());

        List<ChilivoteEntity> filteredChilivotes = chilivotes.stream().filter(chilivote ->
                chilivote.getUser().getId() != user.getId() &&
                        !VotedChilivoteIds.contains(chilivote.getId()) &&
                        chilivote.getFollowers() != null &&
                        Stream.of(chilivote.getFollowers().split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList()).contains(user.getId())
        ).collect(Collectors.toList());

        //filter chilivotes by 2 conditions
        List<ChilivoteVotableDTO> Result = new ArrayList<ChilivoteVotableDTO>();

        for(ChilivoteEntity chilivote: filteredChilivotes)
        {
            if(!user.getPreferences().hide.contains(chilivote.getUser().getId()))
                Result.add(this.ToChilivoteVotableDTO(chilivote, user));
        }
        return Result;
    }

    public List<ChilivoteVotableDTO> GetTrendingFeed(String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        
        UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        Iterable<ChilivoteEntity> iterables = chilivoteRepository.findAll();
        List<ChilivoteEntity> chilivotes = new ArrayList<ChilivoteEntity>();
        for(ChilivoteEntity c: iterables)
        {
            chilivotes.add(c);
        }

        Collections.sort(chilivotes, compareByVotes);

        List<ChilivoteVotableDTO> FinalResult = new ArrayList<ChilivoteVotableDTO>();
        for(ChilivoteEntity chilivote: chilivotes)
        {
            if(user.getId() != chilivote.getUser().getId() && !chilivote.isPrivate() && !user.getPreferences().hide.contains(chilivote.getUser().getId()))
                FinalResult.add(this.ToChilivoteVotableDTO(chilivote, user));
        }
        return FinalResult;
    }

    public ChilivoteDTOBE CreateChilivote(ChilivoteDTOUI DTO, String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        ChilivoteEntity entity = ToChilivoteEntity(DTO, user);

        ChilivoteEntity SavedEntity = chilivoteRepository.save(entity);

        ChilivoteDTOBE result = ToChilivoteDTOBE(SavedEntity);

        result.role = this.roleLogicHandler.updateRole(user);

        return result;
    }

    public ChilivoteDTOBE UpdateChilivote(ChilivoteDTOUIUpdate DTO, String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        ChilivoteEntity entity = chilivoteRepository.findById(DTO.id)
        .orElseThrow(() -> new ChilivoteNotFoundException(DTO.id));

        if(!userOwnsChilivote(user, entity))
        throw new ForbiddenOperationException();

        List<AnswerEntity> answerEntities = entity.getAnswers();
        for(AnswerEntity answerEntity : answerEntities)
        {
            answerEntity.setUrl(DTO.answers.stream().filter( (a) -> a.id == answerEntity.getId()).findFirst().get().url);
        }

        ChilivoteEntity SavedEntity = chilivoteRepository.save(entity);

        return ToChilivoteDTOBE(SavedEntity);
    }

    public boolean DeleteChilivote(Integer ChilivoteId, String token)
    {
        Integer id = jwtTokenUtil.getIdFromToken(token);
        UserEntity user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

        ChilivoteEntity entity = chilivoteRepository.findById(ChilivoteId)
        .orElseThrow(() -> new ChilivoteNotFoundException(ChilivoteId));

        if(!userOwnsChilivote(user, entity))
        throw new ForbiddenOperationException();

        chilivoteRepository.deleteById(ChilivoteId);
        return true;
    }

    ///////////////////////// Protected ////////////////////////////////

    protected boolean userOwnsChilivote(UserEntity user, ChilivoteEntity chilivote)
    {
        return user.getChilivotes().contains(chilivote);
    }

    protected ChilivoteEntity ToChilivoteEntity(ChilivoteDTOUI DTO, UserEntity user)
    {
        ChilivoteEntity entity = new ChilivoteEntity();

        List<AnswerEntity> answerEntities = getAnswersSet(DTO);

        entity.setTitle(DTO.title);
        entity.setAnswers(answerEntities);
        entity.setUser(user);
        entity.setPrivate(DTO.isPrivate);
        entity.setFollowers(DTO.followers.stream().map(String::valueOf).collect(Collectors.joining(",")));

        for(AnswerEntity answerEntity : answerEntities)
        {
            answerEntity.setChilivote(entity);
            answerEntity.setUser(user);
        }

        return entity;
    }

    protected List<AnswerEntity> getAnswersSet(ChilivoteDTOUI DTO)
    {
        List<AnswerEntity> answerEntities = new ArrayList<>();
        answerEntities.add(new AnswerEntity("", DTO.answerLeft));
        answerEntities.add(new AnswerEntity("", DTO.answerRight));
        return answerEntities;
    }

    protected ChilivoteDTOBE ToChilivoteDTOBE(ChilivoteEntity entity)
    {
        ChilivoteDTOBE DTO = new ChilivoteDTOBE();
        List<AnswerEntity> answerEntities = new ArrayList<AnswerEntity>(entity.getAnswers());
        
        DTO.answerLeft = answerEntities.get(0).getUrl();
        DTO.answerRight = answerEntities.get(1).getUrl();
        DTO.title = entity.getTitle();
        DTO.created_at = entity.getCreatedAt();
        return DTO;
    }

    protected MyChilivoteDTO ToMyChilivoteDTO(ChilivoteEntity entity)
    {
        MyChilivoteDTO DTO = new MyChilivoteDTO();
        List<AnswerEntity> answerEntities = new ArrayList<AnswerEntity>(entity.getAnswers());
        
        DTO.answerLeft = new AnswerVoteDTO();
        DTO.answerLeft.url = answerEntities.get(0).getUrl();
        DTO.answerLeft.votes = answerEntities.get(0).getVotes().size();
        DTO.answerLeft.id = answerEntities.get(0).getId();

        DTO.answerRight = new AnswerVoteDTO();
        DTO.answerRight.url = answerEntities.get(1).getUrl();
        DTO.answerRight.votes = answerEntities.get(1).getVotes().size();
        DTO.answerRight.id = answerEntities.get(1).getId();

        DTO.title = entity.getTitle();
        DTO.created_at = entity.getCreatedAt();
        DTO.id = entity.getId();

        return DTO;
    }

    protected ChilivoteVotableDTO ToChilivoteVotableDTO(ChilivoteEntity entity, UserEntity user)
    {
        ChilivoteVotableDTO DTO = new ChilivoteVotableDTO();
        List<AnswerEntity> answerEntities = new ArrayList<AnswerEntity>(entity.getAnswers());

        AnswerEntity leftAnswer = answerEntities.get(0);
        AnswerEntity rightAnswer = answerEntities.get(1);

        DTO.answerLeft = new AnswerVoteDTO();
        DTO.answerLeft.url = leftAnswer.getUrl();
        DTO.answerLeft.votes = leftAnswer.getVotes().size();
        DTO.answerLeft.voted = !leftAnswer.getVotes().stream().filter((vote) ->
        vote.getUser().getId() == user.getId()).findFirst().isEmpty();
        DTO.answerLeft.id = leftAnswer.getId();

        DTO.answerRight = new AnswerVoteDTO();
        DTO.answerRight.url = rightAnswer.getUrl();
        DTO.answerRight.votes = rightAnswer.getVotes().size();
        DTO.answerRight.voted = !rightAnswer.getVotes().stream().filter((vote) ->
        vote.getUser().getId() == user.getId()).findFirst().isEmpty();
        DTO.answerRight.id = rightAnswer.getId();

        DTO.title = entity.getTitle();
        DTO.created_at = entity.getCreatedAt();
        DTO.id = entity.getId();
        DTO.username = entity.getUser().getUsername();
        DTO.userId = entity.getUser().getId();
        DTO.avatar = entity.getUser().getAvatar();

        DTO.isFollowing = !user.getFollowing().stream().filter(u -> 
        u.getTo().getId() == entity.getUser().getId())
        .findFirst().isEmpty();

        return DTO;
    }

    Comparator<ChilivoteEntity> compareByVotes = new Comparator<ChilivoteEntity>(){
        @Override
        public int compare(ChilivoteEntity o1, ChilivoteEntity o2) {
            return ((Integer)(o1.getVotes().size())).compareTo((Integer)(o2.getVotes().size()));
        }
    };
}

