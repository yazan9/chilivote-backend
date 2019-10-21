package chilivote.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import chilivote.Entities.Chilivote;
import chilivote.Repositories.ChilivoteRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/chilivotes")
public class ChilivoteController
{
    @Autowired //for repository auto-generation
    private ChilivoteRepository chilivoteRepository;

    @GetMapping(path="/all")
    public @ResponseBody Iterable<Chilivote> GetAllUsers()
    {
        return chilivoteRepository.findAll();
    }
}