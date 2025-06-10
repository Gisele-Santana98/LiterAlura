package com.one.literalura.principal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.one.literalura.model.Autor;
import com.one.literalura.model.DadosLivro;
import com.one.literalura.model.Idioma;
import com.one.literalura.model.Livro;
import com.one.literalura.repository.IdiomaRepository;
import com.one.literalura.repository.LivroRepository;
import com.one.literalura.service.ConsumoApi;
import com.one.literalura.service.ConverteDados;
import com.one.literalura.service.LivroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal implements CommandLineRunner {

    private final LivroRepository repositorio; // Repositório para manipulação de livros
    private Scanner leitura = new Scanner(System.in); // Scanner para entrada do usuario

    @Autowired
    private ConsumoApi consumo; // Serviço para consumir API externa
    @Autowired
    private ConverteDados conversor; // Serviço para conversão de dados

    private final String ENDERECO = "https://gutendex.com/books/?search="; // URL base da API
    private List<DadosLivro> dadosLivros = new ArrayList<>();

    private List<Livro> livros = new ArrayList<>();
    private Optional<Livro> livroBusca; // Evita NullPointerException ao buscar livros

    @Autowired
    private IdiomaRepository idiomaRepository; // Repositório para manipulação de idiomas
    @Autowired
    private LivroService livroService; // Serviço para manipulação de livros

    public Principal(LivroRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) throws Exception {
        exibeMenu(); // Executa o menu assim que a aplicação inicia
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    \n--- MENU GUTENDEX ---
                    1 - Buscar livro pelo título
                    2 - Listar livros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos em determinado ano
                    5 - Listar livros por idioma
                    6 - Listar o top 5 dos livros mais baixados
                    7 - Listar livros menos baixados
                    8 - Listar idiomas disponíveis para leitura
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt(); // Captura a opção do usuario
            leitura.nextLine(); // Consome quebra de linha para evitar erros

            // Executa a ação conforme a opção escolhida
            switch (opcao) {
                case 1 -> buscarLivroPeloTitulo();
                case 2 -> listarLivrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivos();
                case 5 -> listarLivrosPorIdioma();
                case 6 -> listarTop5LivrosMaisBaixados();
                case 7 -> listarLivrosMenosBaixados();
                case 8 -> listarIdiomasDisponiveis();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!"); // Mensagem de erro para entradas inválidas
            }
        }
    }

    private DadosLivro getDadosLivro(String nomeLivro) {
        var json = consumo.obterDados(ENDERECO + nomeLivro.replace(" ", "+")); // Faz requisição à API
        System.out.println("\n📝 JSON recebido: " + json); // Exibe o JSON recebido

        if (json == null || json.isEmpty()) {
            System.out.println("\n❌ Erro: A API não retornou resultados."); // Trata erro de resposta vazia
            return null;
        }
        try {
            JsonNode rootNode = new ObjectMapper().readTree(json); // Converte JSON para árvore de nós
            JsonNode resultsNode = rootNode.path("results"); // Obtém nó de resultados

            if (resultsNode.isEmpty() || resultsNode.get(0) == null) {
                System.out.println("\n❌ Nenhum resultado encontrado na API."); // Verifica se há resultados
                return null;
            }
            // Retorna apenas o primeiro resultado encontrado
            return new ObjectMapper().treeToValue(resultsNode.get(0), DadosLivro.class);
        } catch (JsonProcessingException e) {
            System.out.println("\n❌ Erro ao processar o JSON: " + e.getMessage()); // Captura erro de processamento
            return null;
        }
    }

    private void salvarLivroNoBanco(DadosLivro dados) {
        if (dados == null || dados.titulo() == null || dados.titulo().isBlank() ||
                dados.autores() == null || dados.autores().isEmpty() ||
                dados.idioma() == null || dados.idioma().isEmpty() ||
                dados.temas() == null || dados.temas().isEmpty()) {

            System.out.println("\n❌ O livro possui informações incompletas e não será salvo no banco.");
            return;
        }

        if (!repositorio.existsByTituloIgnoreCase(dados.titulo())) {
            Livro livro = new Livro(dados.titulo(),
                    dados.autores().stream().map(Autor::nome).collect(Collectors.toList()),
                    dados.idioma(),
                    dados.temas(),
                    dados.numeroDeDonwload());
            repositorio.save(livro);
            System.out.println("\n✅ Livro salvo no banco de dados!");
        } else {
            System.out.println("\n⚠️ Livro já existe no banco, não será salvo novamente.");
        }
    }



    private void buscarLivroPeloTitulo() {
        System.out.println("Digite o nome do livro.");
        var nomeLivro = leitura.nextLine().trim();

        // Verifica se o livro já está cadastrado no banco
        Optional<Livro> livroExistente = repositorio.findByTituloContainingIgnoreCase(nomeLivro);
        if (livroExistente.isPresent()) {
            // Exibe as informações do livro antes de evitar a duplicação
            System.out.println("\n📖 Livro já cadastrado no banco:");
            livroService.exibirDetalhesLivro(livroExistente.get());
            return; // Sai do método sem buscar na API novamente
        }

        // Caso o livro não esteja no banco, busca na API
        DadosLivro dados = getDadosLivro(nomeLivro);
        if (dados != null && dados.titulo() != null) {
            System.out.println("\n📖 Livro encontrado na API: " + dados.titulo());

            // Valida se todos os dados essenciais do livro estão presentes
            if (dados.autores() == null || dados.autores().isEmpty() ||
                    dados.idioma() == null || dados.idioma().isEmpty() ||
                    dados.temas() == null || dados.temas().isEmpty()) {
                System.out.println("\n⚠️ O livro encontrado tem dados incompletos e não será salvo.");
                return;
            }

            // Exibe os detalhes do livro
            livroService.exibirDetalhesLivro(new Livro(dados));

            // Salva o livro no banco apenas se estiver completo
            salvarLivroNoBanco(dados);
        } else {
            System.out.println("\n❌ Erro ao buscar o livro na API.");
        }
    }


    private void listarLivrosRegistrados() {
        livros = repositorio.findAll(); // Obtém todos os livros do banco
        if (livros.isEmpty()) {
            System.out.println("\n❌ Nenhum livro encontrado no banco de dados."); // Verifica se há livros cadastrados
            return;
        }
        System.out.println("\n📚 Lista de Livros Registrados:");
        System.out.println("----------------------------------------------------------");

        livros.stream()
                .sorted(Comparator.comparing(livro -> Optional.ofNullable(livro.getTitulo()).orElse(""),
                        Comparator.nullsLast(String::compareTo))) // Ordena os livros pelo título, tratando valores nulos
                .forEach(livro -> {
                    // Exibe informações do livro, garantindo que valores nulos sejam substituídos por um texto padrão
                    System.out.println("\n📖 **Título:** " + Optional.ofNullable(livro.getTitulo()).orElse("(Título não disponível)"));
                    System.out.println("✍ **Autor(es):** " + Optional.ofNullable(livro.getAutores()).orElse("(Autor não disponível)"));
                    System.out.println("🌍 **Idioma:** " + Optional.ofNullable(livro.getIdioma()).orElse("(Idioma não disponível)"));
                    System.out.println("📥 **Número de downloads:** " + Optional.ofNullable(livro.getNumeroDeDonwload()).orElse(0));
                    System.out.println("----------------------------------------------------------");
                });
    }

    private void listarAutoresRegistrados() {
        System.out.println("Autores registrados:");
        livros = repositorio.findAll(); // Obtém todos os livros registrados

        Set<String> autores = livros.stream()
                .map(Livro::getAutores)
                .filter(Objects::nonNull) // Evita erro ao processar valores nulos
                .flatMap(a -> Arrays.stream(a.split(","))) // Divide autores separados por vírgula
                .map(String::trim) // Remove espaços extras nos nomes
                .collect(Collectors.toSet()); // Armazena em um conjunto para evitar duplicatas

        autores.stream()
                .sorted() // Ordena os autores alfabeticamente
                .forEach(System.out::println); // Exibe a lista de autores no console
    }

    private void listarAutoresVivos() {
        System.out.println("Digite o ano para busca:");
        int ano = leitura.nextInt(); // Captura o ano informado pelo usuario
        leitura.nextLine();

        livros = repositorio.findAll(); // Obtém todos os livros do banco
        List<String> autoresVivos = livros.stream()
                .filter(livro -> livro.getNumeroDeDonwload() != null &&
                        livro.getNumeroDeDonwload() >= ano) // Filtra livros por número de downloads
                .map(Livro::getAutores) // Obtém os autores
                .flatMap(a -> Arrays.stream(a.split(","))) // Divide autores separados por vírgula
                .map(String::trim) // Remove espaços extras
                .distinct() // Remove duplicatas
                .collect(Collectors.toList()); // Converte para lista

        // Exibe os autores encontrados ou mensagem caso nenhum seja identificado
        if (autoresVivos.isEmpty()) {
            System.out.println("Nenhum autor encontrado para o ano informado.");
        } else {
            System.out.println("Autores vivos no ano " + ano + ":");
            autoresVivos.forEach(System.out::println);
        }
    }

    private void listarLivrosPorIdioma() {
        System.out.println("Digite o idioma para busca (ex: en, fr, pt):");
        String idioma = leitura.nextLine().toLowerCase(); // Captura e padroniza o idioma informado pelo usuário

        List<Livro> livrosPorIdioma = repositorio.findByIdiomaContainingIgnoreCase(idioma); // Busca livros que contêm o idioma informado

        if (livrosPorIdioma.isEmpty()) {  // Verifica se há livros no idioma desejado e exibe a lista
            System.out.println("Nenhum livro encontrado para o idioma informado.");
        } else {
            System.out.println("Livros no idioma " + idioma + ":");
            livrosPorIdioma.forEach(System.out::println); // Exibe cada livro encontrado
        }
    }

    private void listarTop5LivrosMaisBaixados() {
        System.out.println("\n🔝 Buscando os Top 5 Livros Mais Baixados na API...");
        var json = consumo.obterDados("https://gutendex.com/books/?sort=download_count"); // Faz requisição à API

        if (json == null || json.isEmpty()) {
            System.out.println("\n❌ Erro: A API não retornou resultados."); // Verifica resposta vazia
            return;
        }

        try {
            JsonNode rootNode = new ObjectMapper().readTree(json); // Converte JSON em árvore de nós
            JsonNode resultsNode = rootNode.path("results"); // Obtém nó de resultados

            if (resultsNode.isEmpty()) {
                System.out.println("\n❌ Nenhum resultado encontrado."); // Trata erro de resposta vazia
                return;
            }

            // Itera sobre os top 5 livros mais baixados
            for (int i = 0; i < Math.min(5, resultsNode.size()); i++) {
                JsonNode livroNode = resultsNode.get(i);

                // Obtém os dados do livro, definindo valores padrão caso estejam ausentes
                String titulo = livroNode.has("title") ? livroNode.get("title").asText() : "Título desconhecido";
                List<String> idiomas = livroNode.has("languages") ?
                        new ObjectMapper().convertValue(livroNode.get("languages"), new TypeReference<List<String>>() {})
                        : List.of("Idioma desconhecido");
                int downloads = livroNode.has("download_count") ? livroNode.get("download_count").asInt() : 0;
                List<String> autores = livroNode.has("authors") ?
                        livroNode.get("authors").findValuesAsText("name")
                        : List.of("Autor desconhecido");
                List<String> temas = livroNode.has("subjects") ?
                        livroNode.get("subjects").findValuesAsText("name")
                        : List.of("Tema desconhecido");

                // Exibe as informações do livro, mesmo que alguns dados estejam ausentes
                System.out.println("\n📖 **Título:** " + titulo);
                System.out.println("✍ **Autor(es):** " + String.join(", ", autores));
                System.out.println("🌍 **Idioma(s):** " + String.join(", ", idiomas));
                System.out.println("🎭 **Temas:** " + String.join(", ", temas));
                System.out.println("📥 **Número de downloads:** " + downloads);
                System.out.println("-----------------------------------");

                // Salva no banco se ainda não estiver cadastrado
                if (!repositorio.existsByTituloIgnoreCase(titulo)) {
                    Livro livro = new Livro(titulo, autores, idiomas, temas, downloads);
                    repositorio.save(livro);
                    System.out.println("\n✅ Livro salvo no banco!");
                } else {
                    System.out.println("\n⚠️ Livro já existe no banco, não será salvo novamente.");
                }
            }
        } catch (JsonProcessingException e) {
            System.out.println("\n❌ Erro ao processar o JSON: " + e.getMessage());
        }
    }


    private void listarLivrosMenosBaixados() {
        System.out.println("\n🔍 Buscando os Livros Menos Baixados na API...");
        var json = consumo.obterDados("https://gutendex.com/books/?sort=-download_count"); // Faz requisição à API
        System.out.println("\n📝 JSON recebido: " + json);

        if (json == null || json.isEmpty()) {
            System.out.println("\n❌ Erro: A API não retornou resultados.");
            return;
        }

        try {
            JsonNode rootNode = new ObjectMapper().readTree(json);
            JsonNode resultsNode = rootNode.path("results");

            if (resultsNode.isEmpty()) {
                System.out.println("\n❌ Nenhum resultado encontrado.");
                return;
            }

            // Itera sobre os 5 últimos livros da lista (menos baixados)
            for (int i = Math.max(0, resultsNode.size() - 5); i < resultsNode.size(); i++) {
                JsonNode livroNode = resultsNode.get(i);

                // Obtém título (mesmo que outros dados estejam ausentes)
                String titulo = livroNode.has("title") ? livroNode.get("title").asText() : "Título desconhecido";
                List<String> idiomas = livroNode.has("languages") ?
                        new ObjectMapper().convertValue(livroNode.get("languages"), new TypeReference<List<String>>() {})
                        : List.of("Informação não disponível");
                List<String> autores = livroNode.has("authors") ?
                        livroNode.get("authors").findValuesAsText("name")
                        : List.of("Informação não disponível");
                List<String> temas = livroNode.has("subjects") ?
                        livroNode.get("subjects").findValuesAsText("name")
                        : List.of("Informação não disponível");
                int downloads = livroNode.has("download_count") ? livroNode.get("download_count").asInt() : 0;

                // Exibe o título do livro, mesmo que outros campos estejam ausentes
                System.out.println("\n📖 **Título:** " + titulo);
                System.out.println("✍ **Autor(es):** " + String.join(", ", autores));
                System.out.println("🌍 **Idioma(s):** " + String.join(", ", idiomas));
                System.out.println("🎭 **Temas:** " + String.join(", ", temas));
                System.out.println("📥 **Número de downloads:** " + downloads);
                System.out.println("-----------------------------------");

                // Salva no banco mesmo que alguns dados estejam ausentes, garantindo pelo menos título
                if (!repositorio.existsByTituloIgnoreCase(titulo)) {
                    Livro livro = new Livro(titulo, autores, idiomas, temas, downloads);
                    repositorio.save(livro);
                    System.out.println("\n✅ Livro salvo no banco!");
                } else {
                    System.out.println("\n⚠️ Livro já existe no banco, não será salvo novamente.");
                }
            }
        } catch (JsonProcessingException e) {
            System.out.println("\n❌ Erro ao processar o JSON: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n❌ Erro inesperado: " + e.getMessage());
        }
    }


    private void listarIdiomasDisponiveis() {
        System.out.println("\n🌎 Buscando Idiomas Disponíveis na API...");
        var json = consumo.obterDados("https://gutendex.com/books/"); // Faz requisição à API

        if (json == null || json.isEmpty()) {
            System.out.println("\n❌ Erro: A API não retornou resultados."); // Trata erro de resposta vazia
            return;
        }
        try {
            JsonNode rootNode = new ObjectMapper().readTree(json); // Converte JSON para árvore de nós
            JsonNode resultsNode = rootNode.path("results"); // Obtém os resultados

            if (resultsNode.isEmpty()) {
                System.out.println("\n❌ Nenhum resultado encontrado."); // Verifica se há dados retornados
                return;
            }
            Set<String> idiomasUnicos = new HashSet<>(); // Armazena idiomas únicos
            for (JsonNode livroNode : resultsNode) {
                List<String> idiomas = new ObjectMapper().convertValue(livroNode.get("languages"), new TypeReference<List<String>>() {});
                idiomasUnicos.addAll(idiomas); // Adiciona idiomas encontrados
            }
            System.out.println("\n🌎 Idiomas encontrados:");
            idiomasUnicos.forEach(System.out::println); // Exibe lista de idiomas

            // Salva os idiomas únicos no banco, evitando duplicatas
            for (String idioma : idiomasUnicos) {
                if (!idiomaRepository.existsByNomeIgnoreCase(idioma)) {
                    idiomaRepository.save(new Idioma(idioma));
                    System.out.println("\n✅ Idioma salvo no banco: " + idioma);
                } else {
                    System.out.println("\n⚠️ Idioma já existe no banco, não será salvo novamente.");
                }
            }
        } catch (JsonProcessingException e) {
            System.out.println("\n❌ Erro ao processar o JSON: " + e.getMessage()); // Captura erro na conversão do JSON
        }
    }
}

