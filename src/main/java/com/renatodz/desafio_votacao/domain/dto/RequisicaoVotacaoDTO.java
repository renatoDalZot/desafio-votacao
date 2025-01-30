package com.renatodz.desafio_votacao.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequisicaoVotacaoDTO {

    @NotBlank
    private String idPauta;
    @NotBlank
    private String cpfAssociado;
    @NotBlank
    private String opcao;
}
