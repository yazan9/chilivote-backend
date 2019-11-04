package chilivote.Models.DTOs;

import java.time.LocalDateTime;

public class MyChilivoteDTO
{
    public Integer id;
    public String title;
    public AnswerVoteDTO answerLeft;
    public AnswerVoteDTO answerRight;
    public LocalDateTime created_at;
    public boolean voted_on;
}