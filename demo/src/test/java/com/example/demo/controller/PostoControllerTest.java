package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.tomcat.util.http.parser.Authorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.PostoDTO;
import com.example.demo.entity.Posto;
import com.example.demo.enums.NivelAcesso;
import com.example.demo.repository.PostoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.protocol.x.Ok;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class PostoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwt;

    private String token;

    @Autowired
    private PostoRepository pr;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        this.objectMapper = new ObjectMapper();

        this.token = jwt.generateToken("tantofazcomotantofez@admin.com", NivelAcesso.ADMIN.toString());
    }

    @Test
    @DisplayName("Deve deletar posto pelo ID")
    void deletarId() throws Exception {
        Posto posto = new Posto();
        posto.setNome("Posto Para DELETARR por ID");
        posto.setDescricao("Posto buscavel");

        posto = pr.save(posto);

        mockMvc.perform(delete("/postos/" + posto.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        posto = pr.findById(posto.getId()).orElseThrow();

        assertFalse(posto.isAtivo());
    }

    @Test
    @DisplayName("Deve buscar posto pelo ID")
    void buscarPorId() throws Exception {
        Posto posto = new Posto();
        posto.setNome("Posto Para Buscar por ID");
        posto.setDescricao("Posto buscavel");

        posto = pr.save(posto);

        mockMvc.perform(get("/postos/" + posto.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.nome")
                                .value("Posto Para Buscar por ID"));

    }

    @Test
    @DisplayName("Deve criar um posto com sucesso")
    void criarPosto() throws Exception {
        PostoDTO postoDTO = new PostoDTO();

        postoDTO.setNome("Posto 12");
        postoDTO.setDescricao("Descrição do posto 12");

        String json = objectMapper.writeValueAsString(postoDTO);

        mockMvc.perform(
                post("/postos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Posto 12"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Deve listar todos os registros")
    void listarPostos() throws Exception {
        mockMvc.perform(get("/postos")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve dar badrequest ao criar posto")
    void criarBadRequest() throws Exception {
        mockMvc.perform(post("/postos")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Deve dar badrequest ao criar posto com nome vazio")
    void criarBadRequestNomeVazio() throws Exception {
        PostoDTO postoDTO = new PostoDTO();

        postoDTO.setNome("");
        postoDTO.setDescricao("Descrição do posto 12");

        String json = objectMapper.writeValueAsString(postoDTO);

        mockMvc.perform(
                post("/postos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve dar OK ao deletar posto com id inexistente")
    void deletarOKRequestIdInexistente() throws Exception {
        mockMvc.perform(delete("/postos/12")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Deve verificar se o posto existe antes de deletar")
    void deletarVerificarExistencia() throws Exception {
        Posto posto = new Posto();
        posto.setNome("Posto Para DELETARR por ID");
        posto.setDescricao("Posto buscavel");

        posto = pr.save(posto);

        mockMvc.perform(delete("/postos/" + posto.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        boolean exists = pr.existsById(posto.getId());

        assertTrue(exists);
    }
    @Test
    @DisplayName("Deve verificar se o posto é inativo após deletar")
    void deletarVerificarInativo() throws Exception {
        Posto posto = new Posto();
        posto.setNome("Posto Para DELETARR por ID");
        posto.setDescricao("Posto buscavel");

        posto = pr.save(posto);

        mockMvc.perform(delete("/postos/" + posto.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        posto = pr.findById(posto.getId()).orElseThrow();

        assertFalse(posto.isAtivo());
    }
}
   