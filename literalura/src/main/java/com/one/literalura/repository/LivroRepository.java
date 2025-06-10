package com.one.literalura.repository;

import com.one.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Define o repositório como um componente gerenciado pelo Spring
public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByTituloContainingIgnoreCase(String nomeLivro); // Busca livros por título, ignorando maiúsculas/minúsculas

    List<Livro> findByIdiomaContainingIgnoreCase(String idioma); // Busca livros por idioma

    @Query("SELECT DISTINCT l.idioma FROM Livro l ORDER BY l.idioma") // Lista idiomas únicos disponíveis no banco
    List<String> listarIdiomasDisponiveis();

    boolean existsByTituloIgnoreCase(String titulo); // Verifica se o título já existe no banco de dados
}

