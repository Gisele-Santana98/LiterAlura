package com.one.literalura.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "livros") // Define a tabela no banco de dados
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de ID
    private Long id;

    @Column(unique = true, nullable = false) // Impede títulos duplicados ou nulos
    private String titulo;
    private Integer numeroDeDonwload;

    @Column(nullable = false) // Garante que autores sejam armazenados corretamente
    private String autores;
    @Column(nullable = false) // Garante que idiomas sejam preenchidos
    private String idioma;
    @Column(nullable = false) // Campo para armazenar os temas do livro
    private String temas;

    // Construtor padrão necessário para o JPA
    public Livro() {
    }

    // Construtor que recebe um objeto DadosLivro e converte para um Livro
    public Livro(DadosLivro dadosLivro) {
        // Debug para verificar os dados antes de salvar
        System.out.println("\n📖 Título recebido: " + (dadosLivro.titulo() != null ? dadosLivro.titulo() : "Título desconhecido"));
        System.out.println("✍ Autores recebidos antes de salvar: " + (dadosLivro.autores() != null ? dadosLivro.autores() : "Autor desconhecido"));
        System.out.println("🌍 Idiomas recebidos: " + (dadosLivro.idioma() != null ? dadosLivro.idioma() : "Idioma desconhecido"));
        System.out.println("🎭 Temas recebidos: " + (dadosLivro.temas() != null ? dadosLivro.temas() : "Tema desconhecido"));

        // Verifica se os campos obrigatórios são válidos antes de atribuí-los
        if (dadosLivro == null || dadosLivro.titulo() == null || dadosLivro.titulo().isBlank()) {
            throw new IllegalArgumentException("❌ Título do livro é obrigatório.");
        }

        this.titulo = dadosLivro.titulo();
        this.numeroDeDonwload = dadosLivro.numeroDeDonwload();

        // Se autores estiverem nulos ou vazios, define como "Autor desconhecido"
        this.autores = (dadosLivro.autores() == null || dadosLivro.autores().isEmpty())
                ? "Autor desconhecido"
                : dadosLivro.autores().stream()
                .map(Autor::nome)
                .collect(Collectors.joining(", "));

        // Se idioma estiver nulo ou vazio, define como "Idioma desconhecido"
        this.idioma = (dadosLivro.idioma() == null || dadosLivro.idioma().isEmpty())
                ? "Idioma desconhecido"
                : String.join(", ", dadosLivro.idioma());

        // Se temas estiverem nulos ou vazios, define como "Tema desconhecido"
        this.temas = (dadosLivro.temas() == null || dadosLivro.temas().isEmpty())
                ? "Tema desconhecido"
                : String.join(", ", dadosLivro.temas());
    }

    public Livro(String titulo, List<String> autores, List<String> idiomas, List<String> temas, int downloads) {
        // Debug para verificar os dados antes de salvar
        System.out.println("\n📖 Criando livro manualmente: " + (titulo != null ? titulo : "Título desconhecido"));
        System.out.println("✍ Autores: " + (autores != null ? autores : "Autor desconhecido"));
        System.out.println("🌍 Idiomas: " + (idiomas != null ? idiomas : "Idioma desconhecido"));
        System.out.println("🎭 Temas: " + (temas != null ? temas : "Tema desconhecido"));

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("❌ Título do livro é obrigatório.");
        }

        this.titulo = titulo;
        this.numeroDeDonwload = downloads;

        // Se autores estiverem nulos ou vazios, define como "Autor desconhecido"
        this.autores = (autores == null || autores.isEmpty()) ? "Autor desconhecido" : String.join(", ", autores);

        // Se idioma estiver nulo ou vazio, define como "Idioma desconhecido"
        this.idioma = (idiomas == null || idiomas.isEmpty()) ? "Idioma desconhecido" : String.join(", ", idiomas);

        // Se temas estiverem nulos ou vazios, define como "Tema desconhecido"
        this.temas = (temas == null || temas.isEmpty()) ? "Tema desconhecido" : String.join(", ", temas);
    }

    public Livro(String titulo, List<String> autores, List<String> idiomas, int downloads) {
    }

    // Métodos getter e setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getNumeroDeDonwload() { return numeroDeDonwload; }
    public void setNumeroDeDonwload(Integer numeroDeDonwload) { this.numeroDeDonwload = numeroDeDonwload; }

    public String getAutores() { return autores; }
    public void setAutores(String autores) { this.autores = autores; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getTemas() { return temas; }
    public void setTemas(String temas) { this.temas = temas; }

    @Override
    public String toString() {
        return "📖 Título: " + titulo +
                "\n✍ Autor(es): " + autores +
                "\n🌍 Idioma: " + idioma +
                "\n🎭 Temas: " + temas +
                "\n📥 Número de downloads: " + numeroDeDonwload;
    }
}


