package com.one.literalura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "idiomas") // Nome da tabela no banco de dados
public class Idioma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nome;

    // Construtor vazio (necessário para o JPA)
    public Idioma() {}

    // Construtor para salvar idiomas
    public Idioma(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return
                "id=" + id +
                ", nome='" + nome + '\'';
    }
}
