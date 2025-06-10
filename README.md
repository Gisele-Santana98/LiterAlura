# 📚 Literalura - Consulta Literária com Java + Spring Boot

![Java](https://img.shields.io/badge/Java-17-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green?logo=springboot)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)

> Projeto que consome a API Gutendex para exibir livros, autores e idiomas de domínio público, salvando informações no banco de dados local.

---

## 🎯 Funcionalidades

📝 Menu interativo no terminal com as opções:

1. 🔍 Buscar livro pelo título  
2. 📚 Listar livros registrados  
3. ✍ Listar autores registrados  
4. 📆 Listar autores vivos em determinado ano  
5. 🌐 Listar livros por idioma  
6. 📈 Top 5 livros mais baixados  
7. 📉 Livros menos baixados  
8. 🌎 Idiomas disponíveis  
0. 🚪 Sair  

---

## 📸 Exemplo da Interface no Terminal

![Exemplo Terminal](https://raw.githubusercontent.com/seu-usuario/literalura/main/assets/terminal-exemplo.png)

> *Imagem ilustrativa do menu interativo. Pode ser substituída por um print real do seu terminal (formato PNG ou JPG).*

---

## 🧪 Tecnologias Usadas

- Java 21 
- Spring Boot 3  
- Spring Data JPA  
- Hibernate  
- H2 Database  
- Jackson (JSON)   
- API externa: [Gutendex](https://gutendex.com)

---

## 🗂️ Estrutura do Projeto


literalura
├── model/
│ ├── Livro.java
│ ├── Autor.java
│ └── Idioma.java
├── service/
│ ├── ConsumoApi.java
│ ├── ConverteDados.java
│ └── LivroService.java
├── repository/
│ ├── LivroRepository.java
│ └── IdiomaRepository.java
├── principal/
│ └── Principal.java (Classe principal com o menu)
└── resources/
└── application.properties

💡 Observações
A aplicação é interativa via terminal.

Os dados dos livros são persistidos localmente usando JPA.

Evita duplicações ao salvar os livros.

Algumas funcionalidades fazem uso de validações para garantir que os dados da API sejam completos.

👩‍💻 Desenvolvedora
Feito com ❤️ por Gisele

GitHub: @Gisele-Santana98

Email: gisy.ccb@gmail.com

LinkedIn: [linkedin.com/in/seu-usuario](https://www.linkedin.com/in/gisele-sousa98/)

