# Projeto: CompiladoresLLM

## Visão Geral

**CompiladoresLLM** é uma aplicação web em Java (Spring Boot + Thymeleaf) que integra três fases de compilação para uma linguagem simples:

1. **Análise Léxica** – Escaneamento do código-fonte para gerar tokens (identificadores, palavras‑chave, literais, símbolos, comentários, etc.), rastreando linha/coluna.
2. **Análise Sintática** – Parser recursivo-descendente LL(1) que valida a estrutura de declarações, atribuições e expressões, gerando uma árvore sintática.
3. **Análise Semântica** – Verificação de declarações, escopo, checagem de tipo em atribuições e operadores, detectando usos indevidos de variáveis.

O sistema também:

* Calcula e exibe os conjuntos **FIRST** e **FOLLOW** para a gramática usando `FirstFollowCalculator`.
* Fornece testes automatizados (JUnit) para garantir o comportamento do parser.

---

## Gramática LL(1)

```bnf
Program   → StmtList
StmtList  → Stmt ( ';' Stmt )*
Stmt      → Decl | DeclAtrib | Block | Expr
Decl      → Tipo id
DeclAtrib → Tipo id '=' Número
Block     → '{' StmtList '}'
Expr      → E
E         → T E'
E'        → '&' T E' | '|' T E' | ε
T         → F T'
T'        → '^' F T' | ε
F         → '~' F | '(' E ')' | id | Número

Tipo      → 'int' | 'float' | 'string' | 'bool'
Número    → [0-9]+
id        → [a-zA-Z_][a-zA-Z0-9_]*

Comentário, literais string e outros símbolos opcionais são reconhecidos no lexer.
```

---

## 1. Análise Léxica

Implementada em **`Lexer.java`**. Reconhece:

* **Palavras-chave**: `int`, `float`, `string`, `bool`, `if`, `else`, `while`, `return`, etc.
* **Identificadores**: nomes de variáveis/funções.
* **Literais**: números inteiros, strings entre aspas.
* **Operadores**: `+`, `-`, `*`, `/`, `=`, `&`, `|`, `^`, `~`, comparadores (`<`, `>`), etc.
* **Delimitadores**: `;`, `,`, `(`, `)`, `{}`, `[]`.
* **Comentários**: de linha (`//`) e bloco (`/* ... */`).
* **EOF**: fim de arquivo.

Cada token carrega tipo, lexema, linha e coluna.

---

## 2. Análise Sintática

Implementada em **`RecursiveDescentParser.java`** (LL(1)).

* **Program** e **StmtList** para múltiplas sentenças.
* **Decl** (declaração) e **DeclAtrib** (decl + atribuição numérica).
* **Block**: escopo entre chaves.
* **Expressões** com operadores: unário (`~`), binários (`&`, `|`, `^`), literais, identificadores.
* Validação de ausência de tokens extras.
* Em caso de erro, lança `RuntimeException` com mensagem clara.

A árvore sintática é representada por **`ParseTreeNode`** e convertida em HTML pelo **`ParserService`**.

---

## 3. Análise Semântica

Implementada em **`SemanticAnalyzer.java`**:

* **Registro de declarações**: popula tabela de símbolos (nome→tipo).
* **Verificação de duplicidade**: erro se redeclarar variável.
* **Checagem de atribuição**: valida compatibilidade de tipo (ex: `int x = 3`).
* **Verificação de uso**: erro se usar variável não declarada.
* **Checagem de tipos em expressões**:

  * `~`, `&`, `|` demandam booleanos.
  * `^` demanda inteiros.

Erros semânticos são coletados e exibidos juntos.

---

## 4. FIRST e FOLLOW

Classe **`FirstFollowCalculator.java`** calcula automaticamente os conjuntos:

```bash
mvn compile
java -cp target/classes atv.CompiladoresLLM.parser.FirstFollowCalculator
```

Imprime:

```
==== CONJUNTOS FIRST ====
First(E) = {...}
...
==== CONJUNTOS FOLLOW ====
Follow(E) = {...}
...
```

---

## 5. Testes Automatizados

Arquivo: **`RecursiveDescentParserTest.java`**.

Para executar:

```bash
mvn test
```

* **Expressões válidas** usam `assertDoesNotThrow()`.
* **Expressões inválidas** usam `assertThrows()`.

---

## 6. Exemplos de Teste

| Entrada               | Léxico                    | Sintaxe                              | Árvore              | Semântico                                        |
| --------------------- | ------------------------- | ------------------------------------ | ------------------- | ------------------------------------------------ |
| `int x;`              | int, x, `;`               | válido (`Decl`)                      | Decl→int, x         | sem erros                                        |
| `int x = 3;`          | int, x, `=`, 3, `;`       | válido (`DeclAtrib`)                 | DeclAtrib→int, x, 3 | sem erros                                        |
| `float a = 5; a ^ 2;` | float,..., `^`, 2, `;`    | válido em 2 sentenças (`Program`)    | árvore completa     | sem erros (`^` entre inteiros OK)                |
| `bool b = ~a;`        | bool, b, `=`, `~`, a, `;` | sintático OK (`DeclAtrib`)           | DeclAtrib→bool,b,\~ | `Erro semântico: operador '~' requer booleano.`  |
| `{ int y; y & y; }`   | `{`,`int`,`y`,`;`,...     | válido (`Block`)                     | Block→StmtList      | sem erros (`&` em booleanos)                     |
| `id & 5;`             | id, `&`, 5, `;`           | válido (`Expr`)                      | Expr→E              | `Erro semântico: operador '&' requer booleanos.` |
| `string s = "ok";`    | string, s, `=`, "ok",`;`  | sintático: erro em literal string    | —                   | —                                                |
| `int x x;`            | int, x, x, `;`            | erro sintático: token inesperado 'x' | —                   | —                                                |

---

## 7. Como Executar

```bash
# 1. Compilar
mvn compile

# 2. Rodar servidor
mvn spring-boot:run
```

Abra no navegador: [http://localhost:8080](http://localhost:8080)

Digite expressões e veja:

* **Análise Léxica** (lista de tokens)
* **Análise Sintática** (mensagem + árvore)
* **Análise Semântica** (erros ou OK)

---

## Estrutura do Projeto

```
CompiladoresLLM/
├── src/main/java/atv/CompiladoresLLM
│   ├── controller/ParserController.java
│   ├── service/ParserService.java
│   ├── parser/
│   │   ├── RecursiveDescentParser.java
│   │   ├── ParseTreeNode.java
│   │   └── FirstFollowCalculator.java
│   ├── semantic/SemanticAnalyzer.java
└── src/test/java/... /RecursiveDescentParserTest.java
```
