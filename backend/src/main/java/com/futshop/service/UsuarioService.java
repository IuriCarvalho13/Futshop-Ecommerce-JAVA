// Pacote onde o service está localizado
package com.futshop.service;

import com.futshop.model.Usuario;
import com.futshop.repository.UsuarioRepository;
// Importa a anotação para injeção automática (opcional aqui, usada no campo)
import org.springframework.beans.factory.annotation.Autowired;
// Marca a classe como um Service do Spring (componente de lógica de negócio)
import org.springframework.stereotype.Service;

import java.util.Optional;

// Anotação que registra essa classe como um bean de serviço no contexto do Spring
@Service
public class UsuarioService {

    // Injeta o repositório de usuários para operações de persistência (CRUD)
    @Autowired
    private UsuarioRepository usuarioRepository;

    // -----------------------
    // Método para salvar/atualizar usuário
    // -----------------------
    public Usuario salvarUsuario(Usuario usuario) {
        // Se o ID for nulo, significa que é um novo usuário vindo do frontend
        // Garantimos que novos cadastros padrão não serão admin
        if (usuario.getId() == null) {
            usuario.setAdmin(false); 
        }
        // Persiste o usuário no banco (cria ou atualiza conforme o ID)
        return usuarioRepository.save(usuario);
    }

    // -----------------------
    // Método de login que verifica credenciais
    // -----------------------
    public Optional<Usuario> login(String email, String senha) {
        // Busca o usuário pelo email (retorna Optional para tratar ausência)
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(email);

        // Se o usuário foi encontrado, verifica a senha
        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            // ATENÇÃO: aqui a comparação é feita em texto claro.
            // Em produção sempre armazene e compare hashes de senha (ex.: BCrypt).
            if (usuario.getSenha().equals(senha)) { 
                // Senha correta -> retorna o Optional contendo o usuário
                return userOpt;
            }
        }
        // Usuário não encontrado ou senha incorreta -> retorna Optional vazio
        return Optional.empty();
    }
}
