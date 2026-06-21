package com.example.demo.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistroDTO {

    private Long id;
    private String tipo; // "checkin" ou "checkout"
    private String usuario;
    private String posto;
    private Long postoId;
    private String fotoUrl;
    private LocalDateTime timestamp;

    // Só preenchido no checkout
    private RelatorioDTO relatorio;
}
