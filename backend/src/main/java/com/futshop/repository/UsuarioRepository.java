// Pacote onde o repositório está localizado
package com.futshop.repository;

 // Importa a entidade Usuario para tipagem do repositório
import com.futshop.model.Usuario;
// Importa a interface JpaRepository do Spring Data JPA, que fornece operações CRUD prontas
import org.springframework.data.jpa.repository.JpaRepository;
// Importa Optional para representar um possível resultado ausente (vazio)
import java.util.Optional;

// Interface de repositório para a entidade Usuario.
// Ao estender JpaRepository, ela herda métodos como save, findById, findAll, deleteById, etc.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email.
     *
     * - Spring Data JPA implementa automaticamente este método com base no nome
     *   (findByEmail -> WHERE email = ?).
     * - Retorna Optional<Usuario> para forçar o tratamento do caso em que
     *   o usuário não exista (avoid NullPointerException).
     *
     * Exemplo de uso:
     * Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail("exemplo@dominio.com");
     *
     * @param email Email do usuário a ser buscado.
     * @return Optional contendo o Usuario se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<Usuario> findByEmail(String email);
}
