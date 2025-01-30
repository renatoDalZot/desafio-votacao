package com.renatodz.desafio_votacao.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renatodz.desafio_votacao.domain.dto.PautaDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "pautas")
public class Pauta {
    @Id
    private String id;
    private String titulo;
    private String descricao;
    private Sessao sessao;
    private Boolean aprovada;
    private Long votosSim;
    private Long votosNao;
    private LocalDateTime dataApuracao;

    /**
     * Conforme <a href="https://github.com/dbserver/desafio-votacao#:~:text=Abrir%20uma%20sess%C3%A3o%20de%20vota%C3%A7%C3%A3o%20em%20uma%20pauta%20(a%20sess%C3%A3o%20de%20vota%C3%A7%C3%A3o%20deve%20ficar%20aberta%20por%20um%20tempo%20determinado%20na%20chamada%20de%20abertura%20ou%201%20minuto%20por%20default)">...</a>
     * Apenas uma sessão é concebida para cada pauta; por isso, a classe Sessao
     * é uma classe interna de Pauta, com valor default false para 'aberta'.
     */
    @Data
    @Document(collection = "sessoes")
    public static class Sessao {
        private LocalDateTime inicio;
        private LocalDateTime fim;
    }

    @JsonIgnore
    public boolean isSessaoFechada() {
        LocalDateTime agora = LocalDateTime.now();
        if (this.sessao.getInicio() == null || this.sessao.getFim() == null) {
            return true;
        }
        return !agora.isAfter(this.sessao.getInicio()) || !agora.isBefore(this.sessao.getFim());
    }

    @JsonIgnore
    public boolean isSessaoEncerrada() {
        LocalDateTime agora = LocalDateTime.now();
        if (this.sessao.getInicio() == null || this.sessao.getFim() == null) {
            return false;
        }
        return agora.isAfter(this.sessao.getFim());
    }

    @JsonIgnore
    public boolean isSessaoAlgumaVezAberta() {
        return this.sessao.getInicio() != null;
    }

    public void abrirSessao(LocalDateTime inicio, LocalDateTime fim) {
        this.sessao.setInicio(inicio);
        this.sessao.setFim(fim);
    }

    public void encerrarSessao () {
        this.sessao.setFim(LocalDateTime.now());
    }

    public static Pauta fromPautaDTO (PautaDTO pautaDTO) {
        var pauta = new Pauta();
        pauta.setTitulo(pautaDTO.getTitulo());
        pauta.setDescricao(pautaDTO.getDescricao());
        pauta.setAprovada(false);
        pauta.setVotosSim(0L);
        pauta.setVotosNao(0L);
        pauta.setDataApuracao(null);
        pauta.setSessao(new Sessao());
        return pauta;
    }
}

