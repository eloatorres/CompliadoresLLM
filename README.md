# Projeto: CompliadoresLLM

## Visão Geral
O projeto **CompliadoresLLM** é uma aplicação web em Java com Spring Boot que implementa um **analisador sintático recursivo descendente LL(1)**. O objetivo é:

- Validar a sintaxe de expressões baseadas em uma gramática formal (BNF)
- Calcular automaticamente os conjuntos **FIRST** e **FOLLOW**
- Gerar e exibir a **árvore sintática** da expressão analisada
- Exibir os resultados visualmente em uma página web
- Executar **testes automatizados** com expressões válidas e inválidas

---

## Gramática utilizada

A gramática usada foi adaptada para forma LL(1):

```
E  → T E'
E' → & T E' | | T E' | ε
T  → F T'
T' → ^ F T' | ε
F  → ~ F | ( E ) | id
```

Essa gramática suporta:
- Identificadores: `id`
- Operadores: `&`, `|`, `^`, `~`
- Agrupamento: parênteses `()`

---

## Funcionalidades do Projeto

### 1. **Analisador Sintático LL(1)**
- Implementado em `RecursiveDescentParser.java`
- Valida se uma expressão está sintaticamente correta
- Gera uma **árvore sintática** com nós para cada regra aplicada

### 2. **Árvore Sintática Visual**
- Gerada pela classe `ParseTreeNode`
- Convertida em HTML recursivamente em `ParserService`
- Exibida na tela com estrutura de listas e CSS hierárquico

### 3. **Interface Web com Spring Boot + Thymeleaf**
- Formulário para digitar expressão (`index.html`)
- Botão para submeter e exibir resultado
- Mostra mensagem de erro ou árvore sintática se válida

### 4. **Cálculo de FIRST e FOLLOW**
- Implementado em `FirstFollowCalculator.java`
- Executa análise completa dos conjuntos FIRST e FOLLOW
- Usa a mesma gramática do parser
- Imprime os conjuntos no terminal

**Para rodar o cálculo de FIRST e FOLLOW:**
```bash
mvn compile
java atv.CompiladoresLLM.parser.FirstFollowCalculator
```

### 5. **Testes Automatizados (JUnit)**
- Arquivo: `RecursiveDescentParserTest.java`
- Testa diversas expressões válidas e inválidas
- Garante robustez do analisador sintático

**Para executar os testes:**
```bash
mvn test
```

Os testes usam:
- `assertDoesNotThrow` para expressões válidas
- `assertThrows` para garantir que erros sejam lançados em expressões erradas

---

## Como Executar o Projeto

### 1. Iniciar o servidor Spring Boot:
```bash
mvn spring-boot:run
```

### 2. Acessar no navegador:
[http://localhost:8080](http://localhost:8080)

### 3. Inserir uma expressão e clicar em "Validar":
- Exibe mensagem de erro ou árvore sintática gerada

### Exemplos de entradas válidas:
```
id
~ id
id & id
id | id ^ id
~ ( id & id )
```

### Exemplos de entradas inválidas:
```
id id
& id
id ^ ^ id
( id
```

---

## Estrutura do Projeto (src)

```
CompiladoresLLM
├── controller
│   └── ParserController.java
├── service
│   └── ParserService.java
├── parser
│   ├── RecursiveDescentParser.java
│   ├── ParseTreeNode.java
│   └── FirstFollowCalculator.java
├── resources
│   ├── templates/index.html
│   └── application.properties
├── CompiladoresLLMApplication.java
└── test
    └── RecursiveDescentParserTest.java
```

---

