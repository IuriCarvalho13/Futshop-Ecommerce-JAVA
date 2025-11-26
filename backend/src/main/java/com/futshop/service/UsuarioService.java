package com.futshop.service;

import com.futshop.model.Usuario;
import com.futshop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario salvarUsuario(Usuario usuario) {
        // Garante que o usuário cadastrado pelo frontend é um cliente comum
        if (usuario.getId() == null) {
            usuario.setAdmin(false); 
        }
        return usuarioRepository.save(usuario);
    }

    // Método de Login que verifica credenciais
    public Optional<Usuario> login(String email, String senha) {
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            // Lógica de validação da senha. 
            if (usuario.getSenha().equals(senha)) { 
                return userOpt; // Senha correta
            }
        }
        return Optional.empty(); // Usuário não encontrado ou senha incorreta
    }
}