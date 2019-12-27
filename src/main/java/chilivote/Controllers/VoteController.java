package chilivote.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import chilivote.JWT.JwtTokenUtil;
import chilivote.LogicHandlers.VoteLogicHandler;
import chilivote.Models.DTOs.AnswerVotePairDTO;
import chilivote.Repositories.AnswerRepository;
import chilivote.Repositories.UserRepository;
import chilivote.Repositories.VoteRepository;

import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/votes")
public class VoteController
{
    @Autowired //for repository auto-generation
    private UserRepository userRepository;
    @Autowired //for repository auto-generation
    private AnswerRepository answerRepository;
    @Autowired //for repository auto-generation
    private VoteRepository voteRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path="/vote/{id}")
    public @ResponseBody void vote(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        new VoteLogicHandler(jwtTokenUtil, userRepository, answerRepository, voteRepository).vote(id, token);
    } 

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path="/vote_return/{id}")
    public @ResponseBody List<AnswerVotePairDTO> voteAndGetAnswers(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        return new VoteLogicHandler(jwtTokenUtil, userRepository, answerRepository, voteRepository).voteAndGetAnswers(id, token);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path="/unvote/{id}")
    public @ResponseBody void unvote(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        new VoteLogicHandler(jwtTokenUtil, userRepository, answerRepository, voteRepository).unvote(id, token);
    } 
}