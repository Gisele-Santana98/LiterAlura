package com.one.literalura.repository;

import com.one.literalura.model.Idioma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

// Repositório JPA para manipulação da entidade Idioma
public interface IdiomaRepository extends JpaRepository<Idioma, Long> {

    boolean existsByNomeIgnoreCase(String nome); // Verifica se um idioma já existe ignorando maiúsculas/minúsculas

    @Modifying
    @Transactional // Garante que a operação será feita den_tro de uma transação
    @Query(value = "INSERT INTO idiomas (nome) VALUES (:nome)", nativeQuery = true) // Query SQL nativa para inserção
    void saveIdioma(@Param("nome") String nome); // Salva um novo idioma no banco de dados
}
