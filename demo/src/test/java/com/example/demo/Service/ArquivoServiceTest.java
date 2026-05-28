package com.example.demo.Service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.ArquivoService;

@SpringBootTest
@ActiveProfiles("test")
public class ArquivoServiceTest {

    @Autowired
    private ArquivoService arquivoService;

    @Test
    @DisplayName("Deve disparar RuntimeException quando ocorrer um erro interno de IO no upload")
    void deveLancarErroAoFalharIO() throws IOException {
        MultipartFile fileMock = mock(MultipartFile.class);
        
        // Força o arquivo mockado a disparar uma exceção de leitura física
        when(fileMock.getInputStream()).thenThrow(new IOException("Falha de IO Simulada"));

        assertThrows(RuntimeException.class, () -> {
            arquivoService.upload(fileMock);
        });
    }
}