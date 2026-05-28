package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.AuthDTO;
import com.example.demo.entity.Usuario;
import com.example.demo.enums.NivelAcesso;
import com.example.demo.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest

@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");
        usuario.setSenha(passwordEncoder.encode("senha123"));
        usuario.setNivelAcesso(NivelAcesso.PADRAO);
        usuarioRepository.save(usuario);
    }

    @Test
    @DisplayName("Deve fazer login com sucesso e retornar o token JWT")
    void loginComSucesso() throws Exception {
        AuthDTO authDTO = new AuthDTO("teste@email.com", "senha123");
        String json = objectMapper.writeValueAsString(authDTO);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tipo").value("PADRAO"));
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized para senha incorreta")
    void loginSenhaIncorreta() throws Exception {
        AuthDTO authDTO = new AuthDTO("teste@email.com", "senha_errada");
        String json = objectMapper.writeValueAsString(authDTO);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized para utilizador não encontrado")
    void loginUsuarioNaoEncontrado() throws Exception {
        AuthDTO authDTO = new AuthDTO("nao_existe@email.com", "senha123");
        String json = objectMapper.writeValueAsString(authDTO);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }
}