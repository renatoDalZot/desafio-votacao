package com.renatodz.desafio_votacao.domain.dto;

import lombok.Data;

@Data
public class RespostaVotoDTO {
    private String idVoto;
    private String mensagem;

    public RespostaVotoDTO(String idVoto, String mensagem) {
        this.idVoto = idVoto;
        this.mensagem = mensagem;
    }
}