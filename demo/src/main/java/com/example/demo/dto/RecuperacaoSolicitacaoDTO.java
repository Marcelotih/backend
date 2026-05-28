package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecuperacaoSolicitacaoDTO {

    @NotBlank(message = "O campo email é obrigatório!")
    @Email(message = "o email tem q ser valido!")
    private String email;
}
