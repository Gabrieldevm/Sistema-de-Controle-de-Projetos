# 🚀 Sistema de Controle de Projetos – Atividade 3 (AC2)

Sistema completo de controle de projetos desenvolvido com **Spring Boot 3**, **Spring Data JPA**, **H2 Database**, **Java 17** e **Protótipo Frontend Reativo Moderno**.

---

## 📋 Sumário Executivo da Implementação

| Critério de Avaliação | Implementação no Código | Pontuação |
| :--- | :--- | :---: |
| **Definição dos repositórios com queries customizadas** | Consultas personalizadas com JPQL/Derived Queries no `ProjetoRepository`, `FuncionarioRepository` e `SetorRepository` | **1,5 pts** |
| **Implementação das classes de serviço com devidas validações** | `ProjetoService`, `FuncionarioService` e `SetorService` com validação de campos, datas e duplicidade | **1,5 pts** |
| **Tratamento de erros em JSON integrados ao frontend** | `GlobalExceptionHandler` (`@RestControllerAdvice`) tratando 400, 404 e validações, integrado à UI | **1,5 pts** |
| **Criação dos controllers conforme especificado** | `ProjetoController`, `FuncionarioController` e `SetorController` com métodos e assinaturas exigidas | **1,5 pts** |
| **Protótipo frontend listando projetos** | Dashboard com visualização em Cards e Tabela, status e equipe vinculada | **1,0 pt** |
| **Protótipo frontend permitindo inserção de novos projetos** | Modal interativo com validação e feedback em tempo real | **1,0 pt** |
| **Guia de Respostas a Perguntas Técnicas** | Explicações arquiteturais detalhadas para defesa técnica | **2,0 pts** |
| **Total** | | **10,0 pts** |

---

## 🏗️ 1. Modelagem das Entidades (JPA)

As entidades seguem fielmente o diagrama UML:

- **`Projeto`** (`projetos`):
  - Atributos: `id` (Integer, PK), `descricao` (String), `dataInicio` (LocalDate), `dataFim` (LocalDate).
  - Relacionamento: `@ManyToMany` com `Funcionario` via tabela intermediária `projeto_funcionario`.
- **`Funcionario`** (`funcionarios`):
  - Atributos: `id` (Integer, PK), `nome` (String).
  - Relacionamento: `@ManyToOne` com `Setor` (`@JoinColumn(name = "setor_id")`).
  - Relacionamento: `@ManyToMany(mappedBy = "funcionarios")` com `Projeto`.
- **`Setor`** (`setores`):
  - Atributos: `id` (Integer, PK), `nome` (String).
  - Relacionamento: `@OneToMany(mappedBy = "setor")` com `Funcionario`.

---

## 🔍 2. Consultas Personalizadas nos Repositórios

### a. `ProjetoRepository.findByIdWithFuncionarios(Integer id)`
```java
@Query("SELECT p FROM Projeto p LEFT JOIN FETCH p.funcionarios WHERE p.id = :id")
Optional<Projeto> findByIdWithFuncionarios(@Param("id") Integer id);
```
> **Objetivo:** Retorna os dados do projeto trazendo antecipadamente (`LEFT JOIN FETCH`) a lista de funcionários vinculados em um único `SELECT`, eliminando o problema do *N+1* e viabilizando o carregamento seguro fora de escopo transacional.

### b. `ProjetoRepository.findByDataInicioBetween(LocalDate inicio, LocalDate fim)`
```java
@Query("SELECT p FROM Projeto p WHERE p.dataInicio BETWEEN :dataInicio AND :dataFim")
List<Projeto> findByDataInicioBetween(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
```
> **Objetivo:** Retorna todos os projetos cujo início ocorra dentro do intervalo estipulado.

### c. `FuncionarioRepository.findProjetosByFuncionarioId(Integer idFuncionario)`
```java
@Query("SELECT p FROM Projeto p JOIN p.funcionarios f WHERE f.id = :idFuncionario")
List<Projeto> findProjetosByFuncionarioId(@Param("idFuncionario") Integer idFuncionario);
```
> **Objetivo:** Retorna todos os projetos aos quais determinado funcionário está associado através de junção com a coleção de funcionários.

### d. `SetorRepository.findAllWithFuncionarios()`
```java
@Query("SELECT DISTINCT s FROM Setor s LEFT JOIN FETCH s.funcionarios")
List<Setor> findAllWithFuncionarios();
```
> **Objetivo:** Lista todos os setores com seus respectivos colaboradores vinculados de forma unificada.

---

## ⚙️ 3. Camadas de Serviço e Regras de Negócio

- **`ProjetoService`**:
  - `adicionar(ProjetoDTO)`: Valida descrição obrigatória, data de início obrigatória e assegura que a `dataFim` não seja anterior à `dataInicio`.
  - `buscarProjetoPorId(Integer)`: Busca via `findByIdWithFuncionarios` e lança `ResourceNotFoundException` caso o ID não exista. Converte para `DadosProjetoDTO`.
  - `vincularFuncionario(Integer idProjeto, Integer idFuncionario)`: Localiza o projeto e o colaborador; se o colaborador já estiver vinculado, lança `RegraNegocioException` impedindo duplicidade.
  - `buscarProjetosPorIntervalo(LocalDate, LocalDate)`: Valida ordenação das datas e executa a query customizada.
- **`FuncionarioService`**:
  - `adicionar(FuncionarioDTO)`: Valida nome e associa ao `Setor` informado, validando a existência do setor.
  - `buscarProjetos(Integer idFuncionario)`: Valida a existência do funcionário e executa a consulta customizada `findProjetosByFuncionarioId`.
- **`SetorService`**:
  - `adicionar(SetorDTO)`: Valida nome obrigatório e persiste o setor.
  - `buscarSetorPorId(Integer idSetor)`: Busca o setor com seus colaboradores vinculados.

---

## 🌐 4. Endpoints dos Controllers

### `ProjetoController` (`/projetos`)
- `POST /projetos`: Adiciona um projeto (`adicionar(projeto: ProjetoDTO) : void`, status `201 Created`).
- `GET /projetos/{id}`: Busca projeto com funcionários vinculados (`buscarProjetoPorId(id: Integer) : DadosProjetoDTO`).
- `POST /projetos/{idProjeto}/funcionarios/{idFuncionario}`: Vincula funcionário ao projeto (`vincularFuncionario(idProjeto, idFuncionario) : void`).
- `GET /projetos`: Lista todos os projetos com equipe (alimenta o frontend).
- `GET /projetos/intervalo?dataInicio=...&dataFim=...`: Executa a query de intervalo de datas.

### `FuncionarioController` (`/funcionarios`)
- `POST /funcionarios`: Adiciona funcionário (`adicionar(funcionario: FuncionarioDTO) : void`, status `201 Created`).
- `GET /funcionarios/{idFuncionario}/projetos`: Lista os projetos do funcionário (`buscarProjetos(idFuncionario) : List<DadosProjetoDTO>`).
- `GET /funcionarios`: Lista todos os funcionários com seus respectivos setores.

### `SetorController` (`/setores`)
- `POST /setores`: Adiciona um setor (`adicionar(setor: SetorDTO) : void`, status `201 Created`).
- `GET /setores/{idSetor}`: Busca setor por ID (`buscarSetorPorId(idSetor) : DadosSetorDTO`).
- `GET /setores`: Lista todos os setores com seus funcionários vinculados.

---

## 🛡️ 5. Tratamento de Erros Padronizado em JSON (`GlobalExceptionHandler`)

Quando ocorre qualquer inconsistência, o backend responde com código HTTP semântico (400, 404) e corpo JSON padronizado:

```json
{
  "timestamp": "2026-10-21 20:08:07",
  "status": 400,
  "error": "Bad Request",
  "message": "O funcionário 'Lucas Silva' já está vinculado ao projeto 'Modernização do Portal Corporativo'.",
  "path": "/projetos/1/funcionarios/1"
}
```

O frontend intercepta esse JSON e apresenta toasts e alertas visuais destacados ao usuário.

---

## 💻 6. Como Executar o Sistema

### Pré-requisitos
- JDK 17+ instalado
- Terminal PowerShell ou Bash

### Execução do Backend e Frontend
Na raiz do projeto (`c:\Users\xav\Downloads\ac2b`), execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Após a inicialização:
- **Aplicação Web (Protótipo):** [http://localhost:8080/](http://localhost:8080/)
- **Console do Banco H2:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:projetosdb`
  - Usuário: `sa`
  - Senha: *(em branco)*

### Execução dos Testes Automatizados
```powershell
.\mvnw.cmd test
```
*Todos os 13 testes unitários e de integração (MockMvc) são executados com 100% de aprovação.*

---

## 🎯 7. Guia para a Pergunta Técnica (2,0 pts)

Abaixo estão respostas prontas e detalhadas para as principais perguntas técnicas que o avaliador pode formular:

### Pergunta 1: Por que utilizar `LEFT JOIN FETCH` na consulta `findByIdWithFuncionarios`?
> **Resposta:** Em relacionamentos JPA com carregamento preguiçoso (`FetchType.LAZY`), acessar uma coleção de relacionamentos fora da transação pode gerar uma `LazyInitializationException`. Além disso, se percorrermos múltiplos projetos, o JPA geraria uma query adicional para cada registro (o problema do *N+1*). O `LEFT JOIN FETCH` instrui o Hibernate a realizar um único `JOIN` no banco e instanciar o objeto com sua coleção de funcionários já preenchida na memória, garantindo alta performance e eliminando exceções de inicialização.

### Pergunta 2: Qual a importância do uso de DTOs (`ProjetoDTO`, `DadosProjetoDTO`) em vez de retornar as entidades JPA diretamente no Controller?
> **Resposta:** O uso do padrão DTO (Data Transfer Object) oferece três grandes benefícios:
> 1. **Segurança e Encapsulamento:** Impede o vazamento de detalhes internos do banco e atributos que não devem ser expostos ou editados por clientes externos.
> 2. **Prevenção de Loops de Serialização Infinita:** Entidades com relacionamentos bidirecionais (`@ManyToMany` ou `@OneToMany`/`@ManyToOne`) provocam recursão infinita no serializador Jackson (JSON) caso não sejam tratadas. O DTO corta esse ciclo de dependência.
> 3. **Controle de Contrato de API:** Permite desacoplar a evolução do esquema do banco de dados das interfaces expostas aos clientes e frontends.

### Pergunta 3: Como funciona o `@RestControllerAdvice` e o tratamento centralizado de exceções?
> **Resposta:** O `@RestControllerAdvice` atua como um interceptador global (orientado a aspectos) que captura exceções lançadas por qualquer Controller Spring. Classes como `ResourceNotFoundException` e `RegraNegocioException`, além de erros de validação do Bean Validation (`MethodArgumentNotValidException`), são convertidas em respostas HTTP semânticas (404 para não encontrado, 400 para erros de validação ou regra de negócio) com uma estrutura JSON padronizada (`ErrorResponseDTO`). Isso elimina blocos `try/catch` repetitivos nos controllers e fornece um contrato consistente para o frontend.

### Pergunta 4: Como foi modelada a relação Many-to-Many entre `Projeto` e `Funcionario` no JPA?
> **Resposta:** Na entidade `Projeto`, foi definida a anotação `@ManyToMany` com `@JoinTable`, especificando a tabela intermediária `projeto_funcionario` e suas chaves estrangeiras (`projeto_id` e `funcionario_id`). Na entidade `Funcionario`, foi configurado o lado inverso usando `@ManyToMany(mappedBy = "funcionarios")`. Métodos auxiliares de conveniência (`adicionarFuncionario` e `removerFuncionario`) sincronizam as coleções em memória dos dois lados da relação.

### Pergunta 5: Por que utilizar a anotação `@Transactional` nos métodos de serviço?
> **Resposta:** A anotação `@Transactional` define os limites de uma transação com o banco de dados. Em operações de escrita (como `vincularFuncionario`), garante atomicidade: se ocorrer qualquer erro ou validação no meio do processo, todas as alterações são revertidas (rollback). Em operações de leitura, `@Transactional(readOnly = true)` otimiza o desempenho no Hibernate, pois desativa o mecanismo de dirty checking (verificação de alterações) e evita locks desnecessários.
