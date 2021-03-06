package chilivote.models.domain;

import java.time.LocalDateTime;

public class ChilivoteRandomDTO
{
    public Integer id;
    public String title;
    public AnswerVoteDTO answerLeft;
    public AnswerVoteDTO answerRight;
    public LocalDateTime created_at;
    public String username;
    public Integer userId;
    public boolean isFollowing;
}