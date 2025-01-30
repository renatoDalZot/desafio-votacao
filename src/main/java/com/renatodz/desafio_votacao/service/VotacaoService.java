package com.renatodz.desafio_votacao.service;

import com.renatodz.desafio_votacao.client.ServicoValidacaoCPFClient;
import com.renatodz.desafio_votacao.domain.dto.RespostaVotoDTO;
import com.renatodz.desafio_votacao.domain.model.Pauta;
import com.renatodz.desafio_votacao.domain.model.Voto;
import com.renatodz.desafio_votacao.exception.BadRequestException;
import com.renatodz.desafio_votacao.exception.DatabaseException;
import com.renatodz.desafio_votacao.exception.ResourceNotFoundException;
import com.renatodz.desafio_votacao.repository.PautaRepository;
import com.renatodz.desafio_votacao.repository.VotoRepository;
import com.renatodz.desafio_votacao.util.UtilitariosAritmeticos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional // Visa evitar operações inconsistentes. Em caso de erro, a operação deve ser revertida
public class VotacaoService {

    private final PautaRepository pautaRepository;
    private final ServicoValidacaoCPFClient servicoValidacaoCPFClient;
    private final VotoRepository votoRepository;
    private final String SIM = "SIM";
    private final String NAO = "NAO";

    public VotacaoService(PautaRepository pautaRepository, ServicoValidacaoCPFClient
            servicoValidacaoCPFClient, VotoRepository votoRepository) {
        this.pautaRepository = pautaRepository;
        this.servicoValidacaoCPFClient = servicoValidacaoCPFClient;
        this.votoRepository = votoRepository;
    }

    public Pauta abrirSessao(String idPauta, long duracao, ChronoUnit unidade) {
        try {
            var pauta = getPauta(idPauta);
            if (pauta.isSessaoAlgumaVezAberta()) {
                log.error("A sessão de votação para esta pauta já foi aberta em {}.", pauta.getSessao().getInicio());
                throw new BadRequestException("A sessão de votação para esta pauta já foi aberta em " +
                        pauta.getSessao().getInicio().toString() + ".");
            }
            pauta.abrirSessao(LocalDateTime.now(), LocalDateTime.now().plus(duracao, unidade));
            return pautaRepository.save(pauta);
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

    public Pauta reabrirSessao(String idPauta, long duracao, ChronoUnit unidade) {
        try {
            var pauta = getPauta(idPauta);
            if (!pauta.isSessaoAlgumaVezAberta()) {
                log.error("A sessão de votação para esta pauta ainda não foi aberta.");
                throw new BadRequestException("A sessão de votação para esta pauta ainda não foi aberta.");
            }
            if (pauta.getDataApuracao() != null) {
                log.error("A sessão de votação para esta pauta já foi apurada e não pode mais ser aberta.");
                throw new BadRequestException("A sessão de votação para esta pauta já foi apurada e não " +
                        "pode mais ser aberta.");
            }
            pauta.abrirSessao(LocalDateTime.now(), LocalDateTime.now().plus(duracao, unidade));
            return pautaRepository.save(pauta);
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

    public void encerrarSessao(String idPauta) {
        try {
            var pauta = getPauta(idPauta);
            if (pauta.isSessaoFechada()) {
                log.error("A sessão de votação para esta pauta não está aberta.");
                throw new BadRequestException("A sessão de votação para esta pauta não está aberta.");
            }
            pauta.encerrarSessao();
            pautaRepository.save(pauta);
            log.info("Sessão de votação encerrada com sucesso.");
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

    public RespostaVotoDTO votar(String idPauta, String cpf, String opcao) {
        validarCPF(cpf);
        opcao = validarEFormatarOpcao(opcao);
        verificarVotoExistente(idPauta, cpf);
        verificarAutorizacaoCPF(cpf);
        Pauta pauta = verificarPauta(idPauta);
        Voto voto = criarESalvarVoto(idPauta, cpf, opcao);
        return new RespostaVotoDTO(voto.getId(), "Voto registrado com sucesso");
    }

    public Pauta apurarVotacao(Pauta pauta) {
        try {
            pauta.setDataApuracao(LocalDateTime.now());
            var votos = votoRepository.findAllByPautaId(pauta.getId());
            var votosNao = votos.stream().filter(voto -> voto.getOpcao().equals(NAO)).count();
            var votosSim = votos.stream().filter(voto -> voto.getOpcao().equals(SIM)).count();

            pauta.setAprovada(votosSim > votosNao);
            pauta.setVotosSim(votosSim);
            pauta.setVotosNao(votosNao);
            return pautaRepository.save(pauta);
        } catch (DataAccessException e) {
            log.error("Erro de acesso ao banco de dados.", e);
            throw new DatabaseException("Erro de acesso ao banco de dados, durante a apuração da " +
                    "votação realizada.", e);
        }
    }

    public Voto buscarVoto(String id) {
        try {
            return votoRepository.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            log.error("Voto não encontrado.", e);
            throw new ResourceNotFoundException("Voto não encontrado. Verifique o CPF informado e " +
                    "tente novamente.");
        }
    }

    public Voto buscarVotoPorCpf(String idPauta, String cpf) {
        try {
            return votoRepository.findByAssociadoCPF(cpf).stream()
                    .filter(voto -> voto.getPautaId().equals(idPauta))
                    .findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            log.error("Voto não encontrado.", e);
            throw new ResourceNotFoundException("Voto não encontrado. Verifique o CPF informado e " +
                    "tente novamente.");
        }
    }

    private void verificarVotoExistente(String idPauta, String cpf) {
        if (votoRepository.existsByPautaIdAndAssociadoCPF(idPauta, cpf)) {
            log.error("Este CPF já possui um voto registrado para esta pauta.");
            throw new BadRequestException("Este CPF já possui um voto registrado para esta pauta.");
        }
    }

    private void verificarAutorizacaoCPF(String cpf) {
        log.info("Verificando a autorização do CPF para a operação desejada.");
        try {
            var respostaCpfValidation = servicoValidacaoCPFClient.getAssociadoId(cpf);
            if (!respostaCpfValidation.getStatus().equals("ABLE_TO_VOTE")) {
                log.error("Não foi possível verificar a autorização deste CPF para a operação desejada.");
                throw new ResourceNotFoundException("Não foi possível verificar a autorização deste CPF " +
                        "para a operação desejada. Verifique o número informado e tente novamente.");
            }
        } catch (Exception e) {
            log.error("Não foi possível verificar a autorização deste CPF para a operação desejada.", e);
            throw new ResourceNotFoundException("Não foi possível verificar a autorização deste CPF " +
                    "para a operação desejada. Verifique o número informado e tente novamente.");
        }
    }

    private Pauta verificarPauta(String idPauta) {
        log.info("Verificando a pauta solicitada.");
        try {
            Pauta pauta = getPauta(idPauta);
            if (pauta.isSessaoFechada()) {
                log.error("A sessão de votação para esta pauta não está aberta. {}", pauta.getSessao().toString());
                throw new BadRequestException("A sessão de votação para esta pauta não está aberta. " +
                        pauta.getSessao().toString() + ".");
            }
            return pauta;
        } catch (NoSuchElementException e) {
            log.error("Pauta não encontrada.", e);
            throw new ResourceNotFoundException("Pauta não encontrada. Verifique o ID informado e " +
                    "tente novamente.");
        }
    }

    private Voto criarESalvarVoto(String idPauta, String cpf, String opcao) {
        Voto voto = new Voto(idPauta, cpf, LocalDateTime.now(), opcao);
        log.info("Registrando o voto.");
        try {
            votoRepository.save(voto);
        } catch (DataAccessException e) {
            log.error("Erro de acesso ao banco de dados.", e);
            throw new DatabaseException("Erro de acesso ao banco de dados. O voto não foi registrado. " +
                    "Tente novamente mais tarde.", e);
        }
        log.info("Voto registrado com sucesso.");
        return voto;
    }

    private Pauta getPauta(String idPauta) {
        return pautaRepository.findById(idPauta).orElseThrow();
    }

    private static void validarCPF(String cpf) {
        log.info("Verificando se o CPF é válido.");
        if (!UtilitariosAritmeticos.isCPF(cpf)) {
            log.error("O número informado não corresponde a um CPF válido.");
            throw new BadRequestException("O número informado não corresponde a um CPF válido.");
        }
    }

    private String validarEFormatarOpcao(String opcao) {
        log.info("Validando e formatando a opção de voto.");
        if (!opcao.equalsIgnoreCase("Sim") && !opcao.equalsIgnoreCase("Não"))
            throw new BadRequestException("O opção informada para o voto não é válida. Informe 'Sim' ou 'Não'.");
        return opcao.charAt(0) == 'S' || opcao.charAt(0) == 's' ? SIM : NAO;
    }
}