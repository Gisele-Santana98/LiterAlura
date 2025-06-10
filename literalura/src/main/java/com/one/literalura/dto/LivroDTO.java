package com.one.literalura.dto;

// Define um DTO para a entidade Livro
public record LivroDTO (Long id, // Identificador do livro
                        String titulo, // Título
                        String autores, // Nome dos autores
                        String idioma, // Idioma do livro
                        Integer numeroDeDonwload) { // Número de downloads
}
