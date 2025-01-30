package com.renatodz.desafio_votacao.controller;

import com.renatodz.desafio_votacao.domain.model.Pauta;
import com.renatodz.desafio_votacao.domain.dto.PautaDTO;
import com.renatodz.desafio_votacao.service.PautaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/pautas") //Estratégia de versionamento de API '/v1/' visando a coexistência entre
// versões em produção
@Slf4j
public class PautaController {

    private final PautaService pautaService;

    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @PostMapping
    public ResponseEntity<Pauta> criarPauta(@Valid @RequestBody PautaDTO request) {
        return  new ResponseEntity<>(pautaService.criarPauta(request), HttpStatus.CREATED);
    }

    @GetMapping("/listarpaginado")
    public ResponseEntity<Page<Pauta>> listarPautas(@RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    @RequestParam(defaultValue = "id") String sortBy) {
        log.info("Recebida requisição para listar pautas paginado");
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Pauta> pautas = pautaService.listarPautasPaginado(pageable);
        return new ResponseEntity<>(pautas, HttpStatus.OK);
    }

    @GetMapping("/listar")
    public ResponseEntity<Iterable<Pauta>> listarPautas() {
        log.info("Recebida requisição para listar pautas");
        Iterable<Pauta> pautas = pautaService.listarPautas();
        return new ResponseEntity<>(pautas, HttpStatus.OK);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Pauta> buscarPauta(@PathVariable String id) {
        log.info("Recebida requisição para listar pauta com id: {}", id);
        Pauta pauta = pautaService.buscarPautaPeloId(id);
        return new ResponseEntity<>(pauta, HttpStatus.OK);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarPauta(@PathVariable String id) {
        log.info("Recebida requisição para deletar pauta com id: {}", id);
        pautaService.deletarPauta(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
