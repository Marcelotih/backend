package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.config.JwtUtil;
import com.example.demo.entity.Posto;
import com.example.demo.enums.NivelAcesso;
import com.example.demo.repository.PostoRepository;

@SpringBootTest

@ActiveProfiles("test")
public class CheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostoRepository postoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;
    private Posto postoSalvo;

    @BeforeEach
    void setUp() {
        postoRepository.deleteAll();
        
        Posto posto = new Posto();
        posto.setNome("Posto Central");
        posto.setDescricao("Posto para teste de check-in");
        postoSalvo = postoRepository.save(posto);

        token = jwtUtil.generateToken("usuario@teste.com", NivelAcesso.PADRAO.toString());
    }

    @Test
    @DisplayName("Deve realizar o check-in e o upload do arquivo com sucesso")
    void deveRealizarCheckinComSucesso() throws Exception {
        MockMultipartFile foto = new MockMultipartFile(
                "foto", 
                "foto_teste.png", 
                "image/png", 
                "conteudo_da_imagem".getBytes()
        );

        mockMvc.perform(multipart("/check/in")
                .file(foto)
                .param("postoId", postoSalvo.getId().toString())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posto").value("Posto Central"))
                .andExpect(jsonPath("$.horario").exists());
    }
}