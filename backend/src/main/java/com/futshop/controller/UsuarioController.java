package com.futshop.controller;

import com.futshop.model.Usuario;
import com.futshop.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500") 
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para Cadastro
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario usuario) {
        try {
            // A regra de setAdmin(false) está agora no Service, mas garantimos que está aqui também.
            usuario.setAdmin(false); 
            Usuario novoUsuario = usuarioService.salvarUsuario(usuario);
            return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao cadastrar usuário: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para Login
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Usuario credenciais) {
        Optional<Usuario> usuarioOpt = usuarioService.login(credenciais.getEmail(), credenciais.getSenha());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Retorna o objeto do usuário completo, incluindo isAdmin
            return new ResponseEntity<>(usuario, HttpStatus.OK); 
        } else {
            // Retorna 401 (Unauthorized) se as credenciais forem inválidas
            return new ResponseEntity<>("Credenciais inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }
}