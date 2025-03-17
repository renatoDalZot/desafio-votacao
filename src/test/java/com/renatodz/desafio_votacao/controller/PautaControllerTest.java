package com.renatodz.desafio_votacao.controller;

import com.renatodz.desafio_votacao.domain.model.Pauta;
import com.renatodz.desafio_votacao.domain.dto.PautaDTO;
import com.renatodz.desafio_votacao.service.PautaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PautaControllerTest {

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private PautaController pautaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarPauta_createsPautaSuccessfully() {
        PautaDTO pautaDTO = new PautaDTO();
        Pauta pauta = new Pauta();
        when(pautaService.criarPauta(pautaDTO)).thenReturn(pauta);

        ResponseEntity<Pauta> response = pautaController.criarPauta(pautaDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pauta, response.getBody());
    }

    @Test
    void listarPautas_returnsPagedPautas() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Pauta> page = new PageImpl<>(Collections.emptyList());
        when(pautaService.listarPautasPaginado(pageable)).thenReturn(page);

        ResponseEntity<Page<Pauta>> response = pautaController.listarPautas(0, 10, "id");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
    }

    @Test
    void listarPautas_returnsAllPautas() {
        Iterable<Pauta> pautas = Collections.emptyList();
        when(pautaService.listarPautas()).thenReturn(pautas);

        ResponseEntity<Iterable<Pauta>> response = pautaController.listarPautas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pautas, response.getBody());
    }

    @Test
    void buscarPauta_returnsPautaById() {
        String id = "1";
        Pauta pauta = new Pauta();
        when(pautaService.buscarPautaPeloId(id)).thenReturn(pauta);

        ResponseEntity<Pauta> response = pautaController.buscarPauta(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pauta, response.getBody());
    }

    @Test
    void deletarPauta_deletesPautaById() {
        String id = "1";
        doNothing().when(pautaService).deletarPauta(id);

        ResponseEntity<Void> response = pautaController.deletarPauta(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(pautaService, times(1)).deletarPauta(id);
    }
}