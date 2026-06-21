package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Checkin;

@Repository
public interface CheckinRepository extends BaseRepository<Checkin, Long> {

    @Query("""
            SELECT c FROM Checkin c
            WHERE c.usuario.email = :email
            AND c.ativo = TRUE
            ORDER BY c.createdAt DESC
    """)
    List<Checkin> findByUsuarioEmail(String email);

    @Query("""
            SELECT c FROM Checkin c
            WHERE c.ativo = TRUE
            ORDER BY c.createdAt DESC
    """)
    List<Checkin> findAllOrderByCreatedAtDesc();

    @Query("""
            SELECT c FROM Checkin c
            WHERE c.usuario.email = :email
            AND c.ativo = TRUE
            AND c.createdAt >= :inicio
            AND c.createdAt < :fim
            ORDER BY c.createdAt DESC
    """)
    List<Checkin> findByUsuarioEmailAndCreatedAtBetween(String email, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            SELECT c FROM Checkin c
            WHERE c.ativo = TRUE
            AND c.createdAt >= :inicio
            AND c.createdAt < :fim
            ORDER BY c.createdAt DESC
    """)
    List<Checkin> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            SELECT c FROM Checkin c
            WHERE c.ativo = TRUE
            AND c.posto.id = :postoId
            ORDER BY c.createdAt DESC
    """)
    List<Checkin> findByPostoId(Long postoId);

    @Query("""
            SELECT c FROM Checkin c
            WHERE c.ativo = TRUE
            AND c.posto.id = :postoId
            AND c.createdAt >= :inicio
            AND c.createdAt < :fim
            ORDER BY c.createdAt DESC
    """)
    List<Checkin> findByPostoIdAndCreatedAtBetween(Long postoId, LocalDateTime inicio, LocalDateTime fim);
}

