package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ViewStats;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface StaticRepository extends JpaRepository<Event, Long> {

    @Query("SELECT new ru.practicum.ViewStats(e.app, e.uri, COUNT(e)) " +
            "FROM Event e " +
            "WHERE e.createTime BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri")
    List<ViewStats> findAppUriCountByCreateTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStats(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM Event e " +
            "WHERE e.createTime BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri")
    List<ViewStats> findUniqueAppUriCountByCreateTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStats(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM Event e " +
            "WHERE e.createTime BETWEEN :start AND :end " +
            "AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri")
    List<ViewStats> findUniqueAppUriCountByCreateTimeBetweenAndUris(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ViewStats(e.app, e.uri, COUNT(e.ip)) " +
            "FROM Event e " +
            "WHERE e.createTime BETWEEN :start AND :end " +
            "AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStats> findAppUriCountByCreateTimeBetweenAndUris(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);
}
