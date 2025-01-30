package com.renatodz.desafio_votacao.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "votos")
public class Voto {
    @Id
    private String id;
    private String pautaId;
    private String associadoCPF;
    private LocalDateTime dataHora;
    private String opcao;

    public Voto(String pautaId, String associadoCPF, LocalDateTime dataHora, String opcao) {
        this.pautaId = pautaId;
        this.associadoCPF = associadoCPF;
        this.dataHora = dataHora;
        this.opcao = opcao;
    }
}
