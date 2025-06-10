package com.one.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos desconhecidos ao desserializar JSON
public record DadosLivro(
        @JsonAlias("title") String titulo, // Mapeia o título do livro
        @JsonAlias("authors") List<Autor> autores, // Lista de autores
        @JsonAlias("languages") List<String> idioma, // Lista de idiomas disponíveis
        @JsonAlias("download_count") Integer numeroDeDonwload, // Número total de downloads
        @JsonAlias("subjects") List<String> temas // Lista de temas associados ao livro
) {
}
