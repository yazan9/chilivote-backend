package chilivote.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import chilivote.Entities.User;
import chilivote.JWT.JwtResponse;
import chilivote.JWT.JwtTokenUtil;
import chilivote.LogicHandlers.UserLogicHandler;
import chilivote.LogicHandlers.VoteLogicHandler;
import chilivote.Models.DTOs.FollowerDTO;
import chilivote.Models.DTOs.FollowingDTO;
import chilivote.Models.DTOs.UserGenericDTO;
import chilivote.Models.Requests.FBTokenRequest;
import chilivote.Repositories.AnswerRepository;
import chilivote.Repositories.FollowRepository;
import chilivote.Repositories.UserRepository;
import chilivote.Repositories.VoteRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/votes")
public class VoteController
{
    @Autowired //for repository auto-generation
    private UserRepository userRepository;
    @Autowired //for repository auto-generation
    private AnswerRepository answerRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping(path="/vote/{id}")
    public @ResponseBody String vote(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        return new VoteLogicHandler(jwtTokenUtil, userRepository, answerRepository).vote(id, token);
    } 

    @PostMapping(path="/unvote/{id}")
    public @ResponseBody String unvote(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        return new VoteLogicHandler(jwtTokenUtil, userRepository, answerRepository).unvote(id, token);
    } 
}