// Pacote onde este controller está localizado
package com.futshop.controller;

// Importa a classe Usuario do pacote model
import com.futshop.model.Usuario;
// Importa o Service que contém a lógica de negócio relacionada a usuários
import com.futshop.service.UsuarioService;
// Importa a anotação para injeção automática de dependências do Spring
import org.springframework.beans.factory.annotation.Autowired;
// Importa enum de status HTTP
import org.springframework.http.HttpStatus;
// Importa o wrapper de resposta HTTP do Spring
import org.springframework.http.ResponseEntity;
// Importa anotações para controllers REST (mapeamento de rotas)
import org.springframework.web.bind.annotation.*;

// Importa utilitário Optional para lidar com valores possivelmente ausentes
import java.util.Optional;

// Permite requisições CORS apenas do endereço especificado (frontend local)
@CrossOrigin(origins = "http://127.0.0.1:5500") 
// Indica que esta classe é um controller REST (retorna JSON por padrão)
@RestController
// Define o caminho base para todos os endpoints deste controller
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // Injeta o UsuarioService para usar métodos de negócio (salvar, login, etc.)
    @Autowired
    private UsuarioService usuarioService;

    // ----------------------------
    // Endpoint para cadastro
    // Método: POST /api/usuarios/cadastrar
    // ----------------------------
    // Mapeia requisições POST para /cadastrar
    @PostMapping("/cadastrar")
    // Método que recebe um JSON (corpo) convertido para a entidade Usuario
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Garante que o novo usuário não terá privilégios de administrador
            // Observação: idealmente essa regra também está aplicada no Service
            usuario.setAdmin(false); 
            // Chama o Service para salvar o usuário (validações e persistência)
            Usuario novoUsuario = usuarioService.salvarUsuario(usuario);
            // Retorna o usuário criado com status 201 Created
            return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
        } catch (Exception e) {
            // Em caso de erro (ex.: validação), retorna 400 Bad Request com mensagem
            return new ResponseEntity<>("Erro ao cadastrar usuário: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // ----------------------------
    // Endpoint para login
    // Método: POST /api/usuarios/login
    // ----------------------------
    // Mapeia requisições POST para /login
    @PostMapping("/login")
    // Recebe credenciais (email e senha) no corpo da requisição como Usuario
    public ResponseEntity<?> loginUsuario(@RequestBody Usuario credenciais) {
        // Chama o Service de login, que retorna Optional<Usuario> se autenticar
        Optional<Usuario> usuarioOpt = usuarioService.login(credenciais.getEmail(), credenciais.getSenha());

        // Verifica se a autenticação foi bem-sucedida
        if (usuarioOpt.isPresent()) {
            // Recupera o usuário autenticado
            Usuario usuario = usuarioOpt.get();
            // Retorna o usuário completo (pode incluir isAdmin) com 200 OK
            return new ResponseEntity<>(usuario, HttpStatus.OK); 
        } else {
            // Se credenciais inválidas, retorna 401 Unauthorized com mensagem simples
            return new ResponseEntity<>("Credenciais inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }
}
