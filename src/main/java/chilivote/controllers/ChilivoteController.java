package chilivote.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import chilivote.entities.ChilivoteEntity;
import chilivote.services.ChilivoteService;
import chilivote.models.domain.ChilivoteDTOBE;
import chilivote.models.domain.ChilivoteDTOUI;
import chilivote.models.domain.ChilivoteDTOUIUpdate;
import chilivote.models.domain.ChilivoteVotableDTO;
import chilivote.models.domain.MyChilivoteDTO;
import chilivote.Repositories.ChilivoteRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/chilivotes")
public class ChilivoteController
{
    @Autowired
    private ChilivoteService chilivoteService;

    @Autowired //for repository auto-generation
    private ChilivoteRepository chilivoteRepository;

    @GetMapping(path="/all")
    public @ResponseBody Iterable<ChilivoteEntity> GetAllUsers()
    {
        return chilivoteRepository.findAll();
    }

    @GetMapping(path="/mychilivotes")
    public @ResponseBody Iterable<MyChilivoteDTO> GetMyChilivotes(@RequestHeader("Authorization") String token)
    {
        return chilivoteService.GetMyChilivotes(token);
    }

    @GetMapping(path="/{id}")
    public @ResponseBody MyChilivoteDTO GetMyChilivote(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        return chilivoteService.getMyChilivote(token, id);
    }

    @GetMapping(path="/feed")
    public @ResponseBody Iterable<ChilivoteVotableDTO> Feed(@RequestHeader("Authorization") String token)
    {
        return chilivoteService.GetFeed(token);
    }

    @GetMapping(path="/random_feed")
    public @ResponseBody Iterable<ChilivoteVotableDTO> RandomFeed(@RequestHeader("Authorization") String token)
    {
        return chilivoteService.GetRandomFeed(token);
    }

    @GetMapping(path="/trending_feed")
    public @ResponseBody Iterable<ChilivoteVotableDTO> TrendingFeed(@RequestHeader("Authorization") String token)
    {
        return chilivoteService.GetTrendingFeed(token);
    }

    @GetMapping(path="/fire_chilivote")
    public @ResponseBody List<ChilivoteVotableDTO> FireChilivote(@RequestHeader("Authorization") String token)
    {
        return chilivoteService.GetFireChilivote(token);
    }

    @GetMapping(path="/private_chilivote")
    public @ResponseBody List<ChilivoteVotableDTO> PrivateChilivote(@RequestHeader("Authorization") String token)
    {
        return chilivoteService.GetPrivateChilivotes(token);
    }

    @PostMapping(path="/add")
    public @ResponseBody ChilivoteDTOBE add(@RequestHeader("Authorization") String token, @RequestBody ChilivoteDTOUI DTO)
    {
        return chilivoteService.CreateChilivote(DTO, token);
    } 

    @PutMapping(path="/update")
    public @ResponseBody ChilivoteDTOBE update(@RequestHeader("Authorization") String token, @RequestBody ChilivoteDTOUIUpdate DTO)
    {
        return chilivoteService.UpdateChilivote(DTO, token);
    }

    @DeleteMapping(path="/delete/{id}")
    public @ResponseBody boolean delete(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        return chilivoteService.DeleteChilivote(id,  token);
    }
}