package com.renatodz.desafio_votacao.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PautaDTO {
    @NotBlank(message = "O título da pauta não pode ser vazio")
    private String titulo;
    @NotBlank(message = "A descrição da pauta não pode ser vazia")
    private String descricao;
}

