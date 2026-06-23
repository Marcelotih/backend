package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.RecuperacaoSolicitacaoDTO;
import com.example.demo.dto.RecuperarSenhaDTO;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService extends BaseService<Usuario, UsuarioDTO> {

    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public UsuarioService(UsuarioRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Transactional
    public void solicitarCodigo(RecuperacaoSolicitacaoDTO dto) {
        String email = dto.getEmail();

        Usuario usuario = repository.findByEmail(email)
                .orElseThrow();

        String codigo = String.valueOf(10000000 + new Random().nextInt(90000000));
        usuario.setCodigoRecuperacao(codigo);
        usuario.setCodigoRecuperacaoExpiracao(LocalDateTime.now().plusMinutes(20));

        repository.save(usuario);
        try {

            emailService.enviarEmail(email, "SOLICITAÇÃO DE RECUPERAÇÃO DE SENHA", "SEU CODIGO É:" + codigo);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "deu erro ai chefia: ");
        }
    }
    // cole antes do último } da classe
@Transactional
public void criarUsuario(String email, String senha) {
    if (repository.findByEmail(email).isPresent()) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já existe.");
    }
    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setSenha(passwordEncoder.encode(senha));
    repository.save(usuario);
}
    @Transactional
    public void trocarSenha(RecuperarSenhaDTO dto) {
        String email = dto.getEmail();
        String codigo = dto.getCodigo();
        String novaSenha = dto.getNovaSenha();

        Usuario usuario = repository.findByEmail(email).orElseThrow();
        if (usuario.getCodigoRecuperacao() == null || usuario.getCodigoRecuperacaoExpiracao() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nenhuma solicitação de recuperação foi feita.");
        }
        if (!usuario.getCodigoRecuperacao().equals(codigo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "codigo incorreto");
        }
        if (usuario.getCodigoRecuperacaoExpiracao().isBefore(LocalDateTime.now())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "codigo expirado");
        }
        
        String novaSenhaCriptografada = passwordEncoder.encode(novaSenha);
        usuario.setSenha(novaSenhaCriptografada);
        usuario.setCodigoRecuperacao(null);
        usuario.setCodigoRecuperacaoExpiracao(null);
        repository.save(usuario);






    }
}
