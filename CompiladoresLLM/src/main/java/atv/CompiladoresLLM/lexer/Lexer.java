package atv.CompiladoresLLM.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private static final String[] PALAVRAS_CHAVE = {
        "if", "else", "while", "for", "return", "int", "float", "bool", "string"
    };

    private static boolean ehPalavraChave(String palavra) {
        for (String p : PALAVRAS_CHAVE) {
            if (p.equals(palavra)) return true;
        }
        return false;
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int i = 0, linha = 1, coluna = 1;

        while (i < input.length()) {
            char atual = input.charAt(i);

            // Espaços e quebras de linha
            if (Character.isWhitespace(atual)) {
                if (atual == '\n') {
                    linha++;
                    coluna = 1;
                } else {
                    coluna++;
                }
                i++;
                continue;
            }

            int colunaInicio = coluna;

            // Identificadores e palavras-chave
            if (Character.isLetter(atual)) {
                StringBuilder sb = new StringBuilder();
                while (i < input.length() && Character.isLetterOrDigit(input.charAt(i))) {
                    sb.append(input.charAt(i++));
                    coluna++;
                }
                String palavra = sb.toString();
                Token.TokenType tipo = ehPalavraChave(palavra) ? Token.TokenType.PALAVRA_CHAVE : Token.TokenType.IDENTIFICADOR;
                tokens.add(new Token(tipo, palavra, linha, colunaInicio));
                continue;
            }

            // Números inteiros
            if (Character.isDigit(atual)) {
                StringBuilder sb = new StringBuilder();
                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    sb.append(input.charAt(i++));
                    coluna++;
                }
                tokens.add(new Token(Token.TokenType.NUMERO_INTEIRO, sb.toString(), linha, colunaInicio));
                continue;
            }

            // Cadeias de caracteres entre aspas
            if (atual == '"') {
                StringBuilder sb = new StringBuilder();
                sb.append(atual);
                i++;
                coluna++;
                boolean fechado = false;
                while (i < input.length()) {
                    char c = input.charAt(i);
                    sb.append(c);
                    i++;
                    coluna++;
                    if (c == '"') {
                        fechado = true;
                        break;
                    }
                    if (c == '\n') {
                        linha++;
                        coluna = 1;
                    }
                }
                String valor = sb.toString();
                Token.TokenType tipo = fechado ? Token.TokenType.CADEIA : Token.TokenType.DESCONHECIDO;
                tokens.add(new Token(tipo, valor, linha, colunaInicio));
                continue;
            }

            // Comentários de linha
            if (atual == '/' && i + 1 < input.length() && input.charAt(i + 1) == '/') {
                StringBuilder sb = new StringBuilder("//");
                i += 2;
                coluna += 2;
                while (i < input.length() && input.charAt(i) != '\n') {
                    sb.append(input.charAt(i++));
                    coluna++;
                }
                tokens.add(new Token(Token.TokenType.COMENTARIO, sb.toString(), linha, colunaInicio));
                continue;
            }

            // Comentários de bloco
            if (atual == '/' && i + 1 < input.length() && input.charAt(i + 1) == '*') {
                StringBuilder sb = new StringBuilder("/*");
                i += 2;
                coluna += 2;
                boolean fechado = false;
                while (i < input.length()) {
                    char c = input.charAt(i);
                    sb.append(c);
                    if (c == '\n') {
                        linha++;
                        coluna = 1;
                    } else {
                        coluna++;
                    }
                    if (c == '*' && i + 1 < input.length() && input.charAt(i + 1) == '/') {
                        sb.append('/');
                        i += 2;
                        coluna += 2;
                        fechado = true;
                        break;
                    } else {
                        i++;
                    }
                }
                Token.TokenType tipo = fechado ? Token.TokenType.COMENTARIO : Token.TokenType.DESCONHECIDO;
                tokens.add(new Token(tipo, sb.toString(), linha, colunaInicio));
                continue;
            }

            // Delimitadores
            if (";,:" .indexOf(atual) >= 0) {
                tokens.add(new Token(Token.TokenType.DELIMITADOR, String.valueOf(atual), linha, coluna));
                i++;
                coluna++;
                continue;
            }

            // Símbolos e operadores
            if ("+-*/=(){}[]<>!&|^~".indexOf(atual) >= 0) {
                tokens.add(new Token(Token.TokenType.SIMBOLO, String.valueOf(atual), linha, coluna));
                i++;
                coluna++;
                continue;
            }

            // Caractere não reconhecido
            tokens.add(new Token(Token.TokenType.DESCONHECIDO, String.valueOf(atual), linha, coluna));
            i++;
            coluna++;
        }

        tokens.add(new Token(Token.TokenType.EOF, "", linha, coluna));
        return tokens;
    }
}
