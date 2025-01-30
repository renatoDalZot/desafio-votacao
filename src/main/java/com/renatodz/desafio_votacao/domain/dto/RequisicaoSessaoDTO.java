package com.renatodz.desafio_votacao.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequisicaoSessaoDTO {
    @NotBlank
    private String idPauta;
    private Long duracao = 1L;
    private String unidade = "MINUTES";

}
