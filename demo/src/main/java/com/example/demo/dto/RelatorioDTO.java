package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioDTO {

    private TurnoDTO matutino;
    private TurnoDTO vespertino;
    private int lesoesAguaViva;
    private int totalGeral;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TurnoDTO {
        private int prevencoes;
        private int incidentes;
        private int total;
    }
}
