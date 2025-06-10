package com.one.literalura.service;

import com.one.literalura.dto.LivroDTO;
import com.one.literalura.model.Livro;
import com.one.literalura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivroService {

    @Autowired
    private LivroRepository repositorio;

    public List<LivroDTO> listarLivrosRegistrados(){
        return converteDados(repositorio.findAll());
    }

    private List<LivroDTO> converteDados(List<Livro> livros) {
        return livros.stream()
                .map(s -> new LivroDTO(s.getId(), s.getTitulo(), s.getAutores(), s.getIdioma(), s.getNumeroDeDonwload()))
                .collect(Collectors.toList());
    }

    // Método para exibir detalhes formatados do livro
    public void exibirDetalhesLivro(Livro livro) {
        if (livro == null || livro.getTitulo() == null) {
            System.out.println("\n❌ Erro: O livro não foi encontrado.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n📖 ").append("**Livro:** ").append(livro.getTitulo()).append("\n");
        sb.append("✍ **Autor(es):** ").append(
                String.join(", ", livro.getAutores())
        ).append("\n");
        sb.append("🌍 **Idioma:** ").append(String.join(", ", livro.getIdioma())).append("\n");
        sb.append("📥 **Número de downloads:** ").append(livro.getNumeroDeDonwload()).append("\n");

        sb.append("\n🎭 **Principais Temas:**\n");
        sb.append("✔ Preconceito racial e social\n");
        sb.append("✔ Ambição e poder\n");
        sb.append("✔ Influência do meio sobre o indivíduo\n");
        sb.append("✔ Exploração e desigualdade\n");

        System.out.println(sb.toString());
    }
}
