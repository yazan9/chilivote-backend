package chilivote.Models.DTOs;

import java.time.LocalDateTime;

public class UserGenericDTO
{
    public Integer id;
    public String username;
    public String avatar;
    public LocalDateTime created_at;
    public boolean isFollowing;
}