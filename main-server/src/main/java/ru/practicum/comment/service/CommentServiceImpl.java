package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.comment.request.NewCommentRequest;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.exeption.ValidationException;
import ru.practicum.request.dto.ConfirmedRequests;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.event.model.State.PUBLISHED;
import static ru.practicum.request.model.RequestStatus.CONFIRMED;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentRequest newCommentRequest) {
        log.info("Создаем новый комментарий");

        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);

        if (event.getState() != PUBLISHED) {
            throw new ValidationException("Комментарий должен быть опубликован");
        }
        Comment comment = commentRepository.save(CommentMapper.mapToComment(newCommentRequest, author, event));
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        EventShortDto eventShort = EventMapper.mapToEventShortDto(event,
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));

        return CommentMapper.mapToCommentDto(comment, userShort, eventShort);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentRequest newCommentRequest) {
        log.info("Обновляем комментарий");

        Event event = checkAndGetEvent(eventId);


        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId).orElseThrow(() ->
                new NotFoundException("Комментарий с id " + commentId + " не найден или принадлежит не " + userId));

        if (comment.getEvent() != event) {
            throw new ValidationException("Комментарий не относится к событию");
        }

        comment.setText(newCommentRequest.getText());
        comment.setEdited(LocalDateTime.now());

        UserShortDto userShort = UserMapper.toUserShortDto(comment.getAuthor());
        EventShortDto eventShort = EventMapper.mapToEventShortDto(event,
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));

        return CommentMapper.mapToCommentDto(comment, userShort, eventShort);
    }

    @Override
    public List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size) {
        log.info("Возвращаем комментарий по id автора");
        User author = checkAndGetUser(userId);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, PageRequest.of(from / size, size));
        List<Long> eventIds = comments.stream().map(comment -> comment.getEvent().getId()).collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(eventIds, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));

        UserShortDto userShort = UserMapper.toUserShortDto(author);
        List<CommentDto> result = new ArrayList<>();

        for (Comment c : comments) {
            Long eventId  = c.getEvent().getId();
            EventShortDto eventShort = EventMapper.mapToEventShortDto(c.getEvent(), confirmedRequests.get(eventId));
            result.add(CommentMapper.mapToCommentDto(c, userShort, eventShort));
        }

        return result;
    }

    @Override
    public List<CommentDto> getComments(Long eventId, Integer from, Integer size) {
        log.info("Возвращаем комментарии по id события");
        Event event = checkAndGetEvent(eventId);
        EventShortDto eventShort = EventMapper.mapToEventShortDto(event,
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));

        return commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size))
                .stream()
                .map(c -> CommentMapper.mapToCommentDto(c, UserMapper.toUserShortDto(c.getAuthor()), eventShort))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        log.info("Возвращаем комментарий по id");
        Comment comment = checkAndGetComment(commentId);
        UserShortDto userShort = UserMapper.toUserShortDto(comment.getAuthor());

        EventShortDto eventShort = EventMapper.mapToEventShortDto(comment.getEvent(),
                requestRepository.countByEventIdAndStatus(comment.getEvent().getId(), CONFIRMED));

        return CommentMapper.mapToCommentDto(comment, userShort, eventShort);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("Удаляем свой комментарий");

        if (commentRepository.findByIdAndAuthorId(commentId, userId).isEmpty()) {
            throw  new NotFoundException("Комментарий с id " + commentId + " не найдено или его инициатор не " + userId);
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("Удаляем комментарий");
        checkAndGetComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private User checkAndGetUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private Event checkAndGetEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено"));
    }

    private Comment checkAndGetComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id " + commentId + " не найден"));
    }
}
