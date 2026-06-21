package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "checkouts")
@EqualsAndHashCode(callSuper = false)
public class Checkout extends BaseEntity {

    @ManyToOne
    private Posto posto;

    @ManyToOne
    private Arquivo foto;

    @ManyToOne
    private Usuario usuario;

    @Column(name = "matutino_prevencoes")
    private int matutinoPrevencoes;

    @Column(name = "matutino_incidentes")
    private int matutinoIncidentes;

    @Column(name = "vespertino_prevencoes")
    private int vespertinoPrevencoes;

    @Column(name = "vespertino_incidentes")
    private int vespertinoIncidentes;

    @Column(name = "lesoes_agua_viva")
    private int lesoesAguaViva;

    @Column(name = "total_geral")
    private int totalGeral;
}
