package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Checkout;

@Repository
public interface CheckoutRepository extends BaseRepository<Checkout, Long> {

    @Query("""
            SELECT c FROM Checkout c
            WHERE c.usuario.email = :email
            AND c.ativo = TRUE
            ORDER BY c.createdAt DESC
    """)
    List<Checkout> findByUsuarioEmail(String email);

    @Query("""
            SELECT c FROM Checkout c
            WHERE c.ativo = TRUE
            ORDER BY c.createdAt DESC
    """)
    List<Checkout> findAllOrderByCreatedAtDesc();

    @Query("""
            SELECT c FROM Checkout c
            WHERE c.ativo = TRUE
            AND c.posto.id = :postoId
            ORDER BY c.createdAt DESC
    """)
    List<Checkout> findByPostoId(Long postoId);

    @Query("""
            SELECT c FROM Checkout c
            WHERE c.usuario.email = :email
            AND c.ativo = TRUE
            AND c.createdAt >= :inicio
            AND c.createdAt < :fim
            ORDER BY c.createdAt DESC
    """)
    List<Checkout> findByUsuarioEmailAndCreatedAtBetween(String email, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            SELECT c FROM Checkout c
            WHERE c.ativo = TRUE
            AND c.createdAt >= :inicio
            AND c.createdAt < :fim
            ORDER BY c.createdAt DESC
    """)
    List<Checkout> findByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            SELECT c FROM Checkout c
            WHERE c.ativo = TRUE
            AND c.posto.id = :postoId
            AND c.createdAt >= :inicio
            AND c.createdAt < :fim
            ORDER BY c.createdAt DESC
    """)
    List<Checkout> findByPostoIdAndCreatedAtBetween(Long postoId, LocalDateTime inicio, LocalDateTime fim);
}
