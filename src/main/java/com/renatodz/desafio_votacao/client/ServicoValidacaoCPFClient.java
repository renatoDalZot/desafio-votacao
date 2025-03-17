package com.renatodz.desafio_votacao.client;

import com.renatodz.desafio_votacao.domain.dto.RespostaServicoValidacaoCpfDTO;
import com.renatodz.desafio_votacao.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

//Facade/Client Fake para simular a validação de um CPF
@Component
@Slf4j
public class ServicoValidacaoCPFClient {

    private final Random random = new Random();

    public RespostaServicoValidacaoCpfDTO getAssociadoId(String cpf) {
        log.info("Validando CPF: {}", cpf);
        boolean ableToVote = random.nextBoolean();
        log.info("CPF: {} - aptidão: {}", cpf, ableToVote);
        if (ableToVote) throw new ResourceNotFoundException("UNABLE_TO_VOTE");
        var response = new RespostaServicoValidacaoCpfDTO();
        response.setStatus("ABLE_TO_VOTE");
        return response;
    }
}
