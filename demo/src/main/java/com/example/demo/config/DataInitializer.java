package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.Posto;
import com.example.demo.entity.Usuario;
import com.example.demo.enums.NivelAcesso;
import com.example.demo.repository.PostoRepository;
import com.example.demo.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PostoRepository postoRepository){
        return args -> {
            // Criar usuário admin
            if(usuarioRepository.findByEmail("admin@admin.com").isEmpty()){
                Usuario usuario = new Usuario();

                usuario.setEmail("admin@admin.com");
                usuario.setNivelAcesso(NivelAcesso.ADMIN);
                usuario.setSenha(passwordEncoder.encode("123456789"));

                usuarioRepository.save(usuario);

                System.out.println("Usuário ADMIN criado com sucesso: admin@admin.com / 123456789");
            }else{
                System.out.println("Usuário ADMIN já existe no banco!");
            }

            // Criar usuário padrão
            if(usuarioRepository.findByEmail("usuario@usuario.com").isEmpty()){
                Usuario usuarioPadrao = new Usuario();

                usuarioPadrao.setEmail("usuario@usuario.com");
                usuarioPadrao.setNivelAcesso(NivelAcesso.PADRAO); // ajuste conforme o nome real do enum
                usuarioPadrao.setSenha(passwordEncoder.encode("123456789"));

                usuarioRepository.save(usuarioPadrao);

                System.out.println("Usuário padrão criado com sucesso: usuario@usuario.com / 123456789");
            }else{
                System.out.println("Usuário padrão já existe no banco!");
            }

            // Criar os 21 postos (seguindo o frontend)
            if(postoRepository.count() <= 0){
                for(int i = 1; i <= 21; i++){
                    Posto posto = new Posto();
                    posto.setNome("Posto " + String.format("%02d", i));
                    posto.setDescricao("Posto de salva-vidas número " + i);
                    postoRepository.save(posto);
                }
                System.out.println("21 postos criados com sucesso!");
            }else{
                System.out.println("Postos já existem no banco!");
            }
        };
    }

}