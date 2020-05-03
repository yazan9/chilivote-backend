package chilivote.LogicHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import chilivote.Entities.Chilivote;
import chilivote.Entities.Notification;
import chilivote.Entities.User;
import chilivote.Exceptions.UserNotFoundException;
import chilivote.JWT.JwtTokenUtil;
import chilivote.Models.DTOs.NotificationDTO;
import chilivote.Repositories.NotificationRepository;
import chilivote.Repositories.UserRepository;
import javassist.NotFoundException;

@Service
public class NotificationLogicHandler {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public List<NotificationDTO> getNotifications(String token) {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        User owner = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException(user_id));

        return toNotificationDTO(owner.getNotifications());
    }

    public ResponseEntity<?> readNotification(String token, Integer notificationId) throws Exception{
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        User owner = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException(user_id));
        Notification readNotification = owner.getNotifications().stream().filter(n -> n.getId() == notificationId).findFirst().orElse(null);
        if(readNotification == null)
            throw new NotFoundException("Notification not found");

        notificationRepository.delete(readNotification);

        return ResponseEntity.ok().build();
    }

    protected List<NotificationDTO> toNotificationDTO(Set<Notification> notifications){
        List<NotificationDTO> result = new ArrayList<NotificationDTO>();
        List<Notification> notificationsList = new ArrayList<Notification>(notifications);
        
        Map<Chilivote, List<Notification>> groupedNotifications =
        notificationsList.stream().collect(Collectors.groupingBy(n -> n.getChilivote()));

        for(Chilivote chilivote: groupedNotifications.keySet()){
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.chilivoteId = chilivote.getId();
            notificationDTO.chilivoteTitle = chilivote.getTitle();
            notificationDTO.count = groupedNotifications.get(chilivote).size();
            result.add(notificationDTO);
        }

        return result;
    }
}