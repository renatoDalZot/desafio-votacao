package com.renatodz.desafio_votacao.client;

import com.renatodz.desafio_votacao.domain.dto.RespostaServicoValidacaoCpfDTO;
import com.renatodz.desafio_votacao.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith(MockitoExtension.class)
class ServicoValidacaoCPFClientTest {

    private ServicoValidacaoCPFClient servicoValidacaoCPFClient;
    private TestLogger testLogger = TestLoggerFactory.getTestLogger(ServicoValidacaoCPFClient.class);

    @BeforeEach
    void setUp() {
        servicoValidacaoCPFClient = new ServicoValidacaoCPFClient();
        testLogger = TestLoggerFactory.getTestLogger(ServicoValidacaoCPFClient.class);
    }

    @Test
    void deveReceberVotoHabilitadoOuJogarExcecao() {
        // Arrange
        String cpf = "12345678901";
        RespostaServicoValidacaoCpfDTO response = new RespostaServicoValidacaoCpfDTO();
        try {
            // Act
            response = servicoValidacaoCPFClient.getAssociadoId(cpf);
        } catch (ResourceNotFoundException e) {
            // Assert
            assertEquals("UNABLE_TO_VOTE", e.getMessage());
            return;
        }
        assertEquals("ABLE_TO_VOTE", response.getStatus());
    }
}