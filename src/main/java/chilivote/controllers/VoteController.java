package chilivote.controllers;

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

import chilivote.services.VotesService;
import chilivote.models.domain.AnswerVotePairDTO;

import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/votes")
public class VoteController
{
    @Autowired
    private VotesService votesService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path="/vote/{id}")
    public @ResponseBody List<AnswerVotePairDTO> vote(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        return votesService.voteAndGetAnswers(id, token);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path="/unvote/{id}")
    public @ResponseBody void unvote(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        votesService.unvote(id, token);
    } 
}