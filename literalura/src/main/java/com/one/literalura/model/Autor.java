package com.one.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignora propriedades desconhecidas no JSON recebido
public record Autor(
        @JsonAlias("name") String nome, // Mapeia o nome do autor
        @JsonAlias("birth_year") Integer anoNascimento, // Mapeia o ano de nascimento
        @JsonAlias("death_year") Integer anoFalecimento) { // Mapeia o ano de falecimento
}
