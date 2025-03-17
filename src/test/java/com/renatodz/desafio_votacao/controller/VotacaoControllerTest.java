package com.renatodz.desafio_votacao.controller;

import com.renatodz.desafio_votacao.domain.dto.RequisicaoSessaoDTO;
import com.renatodz.desafio_votacao.domain.dto.RequisicaoVotacaoDTO;
import com.renatodz.desafio_votacao.domain.dto.RespostaVotoDTO;
import com.renatodz.desafio_votacao.domain.model.Pauta;
import com.renatodz.desafio_votacao.domain.model.Voto;
import com.renatodz.desafio_votacao.service.VotacaoService;
import net.bytebuddy.asm.Advice;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class VotacaoControllerTest {

    @Mock
    private VotacaoService votacaoService;

    @InjectMocks
    private VotacaoController votacaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void abrirSessao_createsSessionSuccessfully() {
        RequisicaoSessaoDTO requisicao = new RequisicaoSessaoDTO();
        requisicao.setIdPauta("1");
        requisicao.setDuracao(1L);
        requisicao.setUnidade("HOURS");
        Pauta pauta = new Pauta();
        when(votacaoService.abrirSessao("1", 1, ChronoUnit.HOURS)).thenReturn(pauta);

        ResponseEntity<Pauta> response = votacaoController.abrirSessao(requisicao);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pauta, response.getBody());
    }

    @Test
    void reabrirSessao_reopensSessionSuccessfully() {
        RequisicaoSessaoDTO requisicao = new RequisicaoSessaoDTO();
        requisicao.setIdPauta("1");
        requisicao.setDuracao(1L);
        requisicao.setUnidade("HOURS");
        Pauta pauta = new Pauta();
        when(votacaoService.reabrirSessao("1", 1, ChronoUnit.HOURS)).thenReturn(pauta);

        ResponseEntity<Pauta> response = votacaoController.reabrirSessao(requisicao);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pauta, response.getBody());
    }

    @Test
    void encerrarSessao_closesSessionSuccessfully() {
        String id = "1";
        doNothing().when(votacaoService).encerrarSessao(id);

        ResponseEntity<String> response = votacaoController.encerrarSessao(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sessão encerrada com sucesso.", response.getBody());
    }

    @Test
    void votar_registersVoteSuccessfully() {
        RequisicaoVotacaoDTO requisicao = new RequisicaoVotacaoDTO();
        requisicao.setIdPauta("1");
        requisicao.setCpfAssociado("12345678901");
        requisicao.setOpcao("SIM");
        RespostaVotoDTO respostaEsperada = new RespostaVotoDTO("1", "Voto registrado com sucesso");
        when(votacaoService.votar(requisicao.getIdPauta(), requisicao.getCpfAssociado(), requisicao.getOpcao())).thenReturn(respostaEsperada);

        ResponseEntity<?> response = votacaoController.votar(requisicao);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(respostaEsperada, response.getBody());
    }

    @Test
    void buscarVoto_returnsVoteById() {
        String id = "1";
        String opcao = "SIM";
        String pautaId = "1";
        String cpf = "12345678901";
        LocalDateTime dataHora = LocalDateTime.now();
        Voto votoEsperado = new Voto(pautaId, cpf, dataHora, opcao);
        votoEsperado.setId(id);
        Voto votoTeste = new Voto(pautaId, cpf, dataHora, opcao);
        votoTeste.setId(id);
        when(votacaoService.buscarVoto(id)).thenReturn(votoTeste);

        ResponseEntity<?> response = votacaoController.buscarVoto(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(votoEsperado, response.getBody());
    }

    @Test
    void buscarVoto_returnsVoteByPautaAndCpf() {
        String id = "1";
        String opcao = "SIM";
        String pautaId = "1";
        String cpf = "12345678901";
        LocalDateTime dataHora = LocalDateTime.now();
        Voto votoEsperado = new Voto(pautaId, cpf, dataHora, opcao);
        votoEsperado.setId(id);
        Voto votoTeste = new Voto(pautaId, cpf, dataHora, opcao);
        votoTeste.setId(id);
        when(votacaoService.buscarVotoPorCpf(pautaId, cpf)).thenReturn(votoTeste);

        ResponseEntity<?> response = votacaoController.buscarVoto(pautaId, cpf);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(votoEsperado, response.getBody());
    }

    @Test
    void abrirSessao_throwsExceptionForInvalidUnidade() {
        RequisicaoSessaoDTO requisicao = new RequisicaoSessaoDTO();
        requisicao.setIdPauta("1");
        requisicao.setDuracao(1L);
        requisicao.setUnidade("SECONDS");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            votacaoController.abrirSessao(requisicao);
        });

        assertEquals("Unidade de tempo inválida. Use DAYS, HOURS ou MINUTES.", exception.getMessage());
    }

    @Test
    void encerrarSessao_throwsExceptionWhenIdIsInvalid() {
        String id = "invalid";
        doThrow(new IllegalArgumentException("Invalid ID")).when(votacaoService).encerrarSessao(id);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            votacaoController.encerrarSessao(id);
        });

        assertEquals("Invalid ID", exception.getMessage());
        verify(votacaoService, times(1)).encerrarSessao(id);
    }

    @Test
    void encerrarSessao_handlesServiceException() {
        String id = "1";
        doThrow(new RuntimeException("Service error")).when(votacaoService).encerrarSessao(id);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            votacaoController.encerrarSessao(id);
        });

        assertEquals("Service error", exception.getMessage());
        verify(votacaoService, times(1)).encerrarSessao(id);
    }
}