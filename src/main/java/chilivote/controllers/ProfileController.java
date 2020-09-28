package chilivote.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import chilivote.services.ProfileService;
import chilivote.models.domain.ProfileDTO;


@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileLogicHandler;

    @GetMapping(path="/")
    public @ResponseBody ProfileDTO getProfile(@RequestHeader("Authorization") String token)
    {
        return profileLogicHandler.getProfile(token);
    }

    @PostMapping(path = "/")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token, @RequestBody ProfileDTO profile) {
        return profileLogicHandler.updateProfile(token, profile);
    }
}