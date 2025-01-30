package com.renatodz.desafio_votacao.exception;

import com.renatodz.desafio_votacao.domain.dto.CustomErrorResponse;
import com.renatodz.desafio_votacao.domain.dto.RespostaServicoValidacaoCpfDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalExceptionHandler {

    // Exceção criada para simular o retorno 404 do serviço de validação de CPF
    @ExceptionHandler(CpfNotFoundException.class)
    public ResponseEntity<RespostaServicoValidacaoCpfDTO> handleCpfNotFoundException(CpfNotFoundException ex) {
        RespostaServicoValidacaoCpfDTO response = new RespostaServicoValidacaoCpfDTO();
        response.setStatus(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomErrorResponse> handleBadRequestException(BadRequestException ex) {
        return buildErrorResponse(ex.getMessage(), "Bad Request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(), "Illegal Argument", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), "Resource Not Found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomErrorResponse> handleDatabaseException(DatabaseException ex) {
        return buildErrorResponse(ex.getMessage(), "Database Exception", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<CustomErrorResponse> buildErrorResponse(String message, String details, HttpStatus status) {
        CustomErrorResponse response = new CustomErrorResponse(LocalDateTime.now(), message, details);
        return new ResponseEntity<>(response, status);
    }
}