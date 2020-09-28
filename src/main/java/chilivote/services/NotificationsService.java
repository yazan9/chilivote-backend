package chilivote.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import chilivote.entities.ChilivoteEntity;
import chilivote.entities.NotificationEntity;
import chilivote.entities.UserEntity;
import chilivote.exceptions.UserNotFoundException;
import chilivote.jwt.JwtTokenUtil;
import chilivote.models.domain.NotificationDTO;
import chilivote.Repositories.NotificationRepository;
import chilivote.Repositories.UserRepository;
import javassist.NotFoundException;

@Service
public class NotificationsService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public List<NotificationDTO> getNotifications(String token) {
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        UserEntity owner = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException(user_id));

        return toNotificationDTO(owner.getNotifications());
    }

    public ResponseEntity<?> readNotification(String token, Integer notificationId) throws Exception{
        Integer user_id = jwtTokenUtil.getIdFromToken(token);
        UserEntity owner = userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException(user_id));
        NotificationEntity readNotification = owner.getNotifications().stream().filter(n -> n.getId() == notificationId).findFirst().orElse(null);
        if(readNotification == null)
            throw new NotFoundException("Notification not found");

        notificationRepository.delete(readNotification);

        return ResponseEntity.ok().build();
    }

    protected List<NotificationDTO> toNotificationDTO(List<NotificationEntity> notifications){
        List<NotificationDTO> result = new ArrayList<NotificationDTO>();
        List<NotificationEntity> notificationsList = new ArrayList<NotificationEntity>(notifications);
        
        Map<ChilivoteEntity, List<NotificationEntity>> groupedNotifications =
        notificationsList.stream().collect(Collectors.groupingBy(n -> n.getChilivote()));

        for(ChilivoteEntity chilivote: groupedNotifications.keySet()){
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.chilivoteId = chilivote.getId();
            notificationDTO.chilivoteTitle = chilivote.getTitle();
            notificationDTO.count = groupedNotifications.get(chilivote).size();
            result.add(notificationDTO);
        }

        return result;
    }
}