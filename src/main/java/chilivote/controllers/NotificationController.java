package chilivote.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import chilivote.services.NotificationsService;
import chilivote.models.domain.NotificationDTO;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@RequestMapping(path="/notifications")
public class NotificationController {

    @Autowired
    private NotificationsService notificationLogicHandler;

    @GetMapping(path="/")
    public @ResponseBody List<NotificationDTO> notifications(@RequestHeader("Authorization") String token)
    {
        List<NotificationDTO> result = notificationLogicHandler.getNotifications(token);
        return result;
    }

    @PostMapping(path="/delete/{id}")
    public ResponseEntity<?> readNotification(@RequestHeader("Authorization") String token, @PathVariable Integer notificationId) throws Exception
    {
        return notificationLogicHandler.readNotification(token, notificationId);
    }
}