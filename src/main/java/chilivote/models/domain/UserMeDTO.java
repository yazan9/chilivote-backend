package chilivote.models.domain;

import java.time.LocalDateTime;

public class UserMeDTO
{
    public Integer id;
    public String username;
    public String avatar;
    public LocalDateTime created_at;
    public String email;  
    public String role;
}