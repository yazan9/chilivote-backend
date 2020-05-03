package chilivote.Models.DTOs;

import java.time.LocalDateTime;

public class ProfileDTO {
    public Integer id;
    public String username;
    public String avatar;
    public LocalDateTime created_at;
    public String email;  
    public String role;
    public Integer following;
    public Integer followers;
    public Integer posts;
    public Integer votedOn;
    public Integer receivedVotesOn;
}