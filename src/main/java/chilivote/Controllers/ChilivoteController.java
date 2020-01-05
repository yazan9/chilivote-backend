package chilivote.Controllers;

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

import chilivote.Entities.Chilivote;
import chilivote.JWT.JwtTokenUtil;
import chilivote.LogicHandlers.ChilivoteLogicHandler;
import chilivote.Models.DTOs.ChilivoteDTOBE;
import chilivote.Models.DTOs.ChilivoteDTOUI;
import chilivote.Models.DTOs.ChilivoteDTOUIUpdate;
import chilivote.Models.DTOs.ChilivoteVotableDTO;
import chilivote.Models.DTOs.MyChilivoteDTO;
import chilivote.Repositories.ChilivoteRepository;
import chilivote.Repositories.UserRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/chilivotes")
public class ChilivoteController
{
    @Autowired //for repository auto-generation
    private ChilivoteRepository chilivoteRepository;

    @Autowired //for repository auto-generation
    private UserRepository userRepository;
    @Autowired //for repository auto-generation
    
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Chilivote> GetAllUsers()
    {
        return chilivoteRepository.findAll();
    }

    @GetMapping(path="/mychilivotes")
    public @ResponseBody Iterable<MyChilivoteDTO> GetMyChilivotes(@RequestHeader("Authorization") String token)
    {
        return new ChilivoteLogicHandler(chilivoteRepository, jwtTokenUtil, userRepository).
        GetMyChilivotes(token);
    }

    @GetMapping(path="/feed")
    public @ResponseBody Iterable<ChilivoteVotableDTO> Feed(@RequestHeader("Authorization") String token)
    {
        return new ChilivoteLogicHandler(chilivoteRepository, jwtTokenUtil, userRepository).
        GetFeed(token);
    }

    @GetMapping(path="/random_feed")
    public @ResponseBody Iterable<ChilivoteVotableDTO> RandomFeed(@RequestHeader("Authorization") String token)
    {
        return new ChilivoteLogicHandler(chilivoteRepository, jwtTokenUtil, userRepository).
        GetRandomFeed(token);
    }

    @GetMapping(path="/trending_feed")
    public @ResponseBody Iterable<ChilivoteVotableDTO> TrendingFeed(@RequestHeader("Authorization") String token)
    {
        return new ChilivoteLogicHandler(chilivoteRepository, jwtTokenUtil, userRepository).
        GetTrendingFeed(token);
    }

    @GetMapping(path="/fire_chilivote")
    public @ResponseBody List<ChilivoteVotableDTO> FireChilivote(@RequestHeader("Authorization") String token)
    {
        return new ChilivoteLogicHandler(chilivoteRepository, jwtTokenUtil, userRepository).
        GetFireChilivote(token);
    }

    @PostMapping(path="/add")
    public @ResponseBody ChilivoteDTOBE add(@RequestHeader("Authorization") String token, @RequestBody ChilivoteDTOUI DTO)
    {
        return new ChilivoteLogicHandler(chilivoteRepository, jwtTokenUtil, userRepository).CreateChilivote(DTO, token);
    } 

    @PutMapping(path="/update")
    public @ResponseBody ChilivoteDTOBE update(@RequestHeader("Authorization") String token, @RequestBody ChilivoteDTOUIUpdate DTO)
    {
        return new ChilivoteLogicHandler(chilivoteRepository, jwtTokenUtil, userRepository).UpdateChilivote(DTO, token);
    }

    @DeleteMapping(path="/delete/{id}")
    public @ResponseBody boolean delete(@RequestHeader("Authorization") String token, @PathVariable Integer id)
    {
        return new ChilivoteLogicHandler(chilivoteRepository, jwtTokenUtil, userRepository).DeleteChilivote(id,  token);
    }
}