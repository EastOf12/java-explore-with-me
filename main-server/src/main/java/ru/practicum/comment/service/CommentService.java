package ru.practicum.comment.service;


import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.request.NewCommentRequest;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long eventId, NewCommentRequest newCommentRequest);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentRequest newCommentRequest);

    List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size);

    List<CommentDto> getComments(Long eventId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);

    void deleteComment(Long userId, Long commentId);

    void deleteComment(Long commentId);
}
