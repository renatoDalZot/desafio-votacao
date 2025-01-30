package com.renatodz.desafio_votacao.controller;

import com.renatodz.desafio_votacao.domain.dto.RequisicaoSessaoDTO;
import com.renatodz.desafio_votacao.domain.dto.RequisicaoVotacaoDTO;
import com.renatodz.desafio_votacao.domain.model.Pauta;
import com.renatodz.desafio_votacao.service.VotacaoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@RestController
@RequestMapping("/v1/sessao")
public class VotacaoController {

    private final VotacaoService pautaService;

    public VotacaoController(VotacaoService pautaService) {
        this.pautaService = pautaService;
    }

    @PostMapping("/abrir")
    public ResponseEntity<Pauta> abrirSessao(@RequestBody @Valid RequisicaoSessaoDTO requisicao) {
        log.info("Recebida requisição para abrir sessão da pauta com idPauta: {} e duração: {} {}. Data: {}", requisicao.getIdPauta(),
                requisicao.getDuracao(), requisicao.getUnidade(), LocalDateTime.now());
        validarUnidadeDuracao(requisicao.getUnidade());
        Pauta pauta = pautaService.abrirSessao(requisicao.getIdPauta(), requisicao.getDuracao(), ChronoUnit.valueOf(requisicao.getUnidade()));
        return new ResponseEntity<>(pauta, HttpStatus.OK);
    }

    @PostMapping("/reabrir")
    public ResponseEntity<Pauta> reabrirSessao(@RequestBody @Valid RequisicaoSessaoDTO requisicao) {
        log.info("Recebida requisição para reabrir sessão da pauta com idPauta: {} e duração: {} {}. Data: {}", requisicao.getIdPauta(),
                requisicao.getDuracao(), requisicao.getUnidade(), LocalDateTime.now());
        validarUnidadeDuracao(requisicao.getUnidade());
        Pauta pauta = pautaService.reabrirSessao(requisicao.getIdPauta(), requisicao.getDuracao(), ChronoUnit.valueOf(requisicao.getUnidade()));
        return new ResponseEntity<>(pauta, HttpStatus.OK);
    }

    @PostMapping("/encerrar/{id}")
    public ResponseEntity<String> encerrarSessao(@PathVariable String id) {
        log.info("Recebida requisição para encerrar sessão da pauta com id {}. Data: {}", id, LocalDateTime.now());
        pautaService.encerrarSessao(id);
        return ResponseEntity.ok("Sessão encerrada com sucesso.");
    }

    @PostMapping("/votar")
    public ResponseEntity<?> votar(@RequestBody RequisicaoVotacaoDTO requisicao) {
        log.info("Recebida requisição para votar na pauta id {}, por cpf {}. Data: {}", requisicao.getIdPauta(),
                requisicao.getCpfAssociado(), LocalDateTime.now());
        return ResponseEntity.ok(pautaService.votar(requisicao.getIdPauta(), requisicao.getCpfAssociado(),
                requisicao.getOpcao()));
    }

    @GetMapping("/voto/{id}")
    public ResponseEntity<?> buscarVoto(@PathVariable String id) {
        log.info("Recebida requisição para buscar voto com id {}. Data: {}", id, LocalDateTime.now());
        return ResponseEntity.ok(pautaService.buscarVoto(id));
    }

    @GetMapping("/voto/{pauta}/{cpf}")
    public ResponseEntity<?> buscarVoto(@PathVariable String pauta, @PathVariable String cpf) {
        log.info("Recebida requisição para buscar voto com idPauta {} e cpf {}. Data: {}", pauta, cpf, LocalDateTime.now());
        return ResponseEntity.ok(pautaService.buscarVotoPorCpf(pauta, cpf));
    }

    private void validarUnidadeDuracao(String unidade) {
        if (!unidade.equals("DAYS") && !unidade.equals("HOURS") && !unidade.equals("MINUTES")) {
            throw new IllegalArgumentException("Unidade de tempo inválida. Use DAYS, HOURS ou MINUTES.");
        }
    }

}
