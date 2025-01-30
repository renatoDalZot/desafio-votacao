package com.renatodz.desafio_votacao.service;

import com.renatodz.desafio_votacao.domain.dto.PautaDTO;
import com.renatodz.desafio_votacao.domain.model.Pauta;
import com.renatodz.desafio_votacao.exception.DatabaseException;
import com.renatodz.desafio_votacao.exception.ResourceNotFoundException;
import com.renatodz.desafio_votacao.repository.PautaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional // Visa evitar operações inconsistentes. Em caso de erro, a operação deve ser revertida
public class PautaService {

    private final PautaRepository pautaRepository;
    private final VotacaoService votacaoService;

    public PautaService(PautaRepository pautaRepository, VotacaoService votacaoService) {
        this.pautaRepository = pautaRepository;
        this.votacaoService = votacaoService;
    }

    public Pauta criarPauta(PautaDTO pautaDTO) {
        try {
            return pautaRepository.save(Pauta.fromPautaDTO(pautaDTO));
        } catch (Exception e) {
            log.error("Erro de acesso ao banco de dados.", e);
            throw new DatabaseException("Erro de acesso ao banco de dados. A pauta não foi criada. Tente " +
                    "novamente mais tarde.", e);
        }
    }

    public Pauta buscarPautaPeloId(String id) {
        try {
            var pauta = pautaRepository.findById(id).orElseThrow();
            if (pauta.getDataApuracao() == null && pauta.isSessaoEncerrada()) {
                pauta = votacaoService.apurarVotacao(pauta);
            }
            return pauta;
        } catch (NoSuchElementException e) {
            log.error("Pauta não encontrada.", e);
            throw new ResourceNotFoundException("Pauta não encontrada. Verifique o ID informado e tente " +
                    "novamente.");
        } catch (DataAccessException e) {
            log.error("Erro de acesso ao banco de dados.", e);
            throw new DatabaseException("Erro de acesso ao banco de dados. A pauta não foi encontrada. " +
                    "Tente novamente mais tarde.", e);
        }
    }

    public Page<Pauta> listarPautasPaginado(Pageable pageable) {
        try {
            return pautaRepository.findAll(pageable);
        } catch (DataAccessException e) {
            log.error("Erro de acesso ao banco de dados.", e);
            throw new DatabaseException("Erro de acesso ao banco de dados. As pautas não foram " +
                    "encontradas. Tente novamente mais tarde.", e);
        }
    }

    public Iterable<Pauta> listarPautas() {
        try {
            return pautaRepository.findAll();
        } catch (DataAccessException e) {
            log.error("Erro de acesso ao banco de dados.", e);
            throw new DatabaseException("Erro de acesso ao banco de dados. As pautas não foram " +
                    "encontradas. Tente novamente mais tarde.", e);
        }
    }

    public void deletarPauta(String id) {
        try {
            pautaRepository.deleteById(id);
        } catch (DataAccessException e) {
            log.error("Erro de acesso ao banco de dados.", e);
            throw new DatabaseException("Erro de acesso ao banco de dados. A pauta não foi encontrada. " +
                    "Tente novamente mais tarde.", e);
        }
    }
}