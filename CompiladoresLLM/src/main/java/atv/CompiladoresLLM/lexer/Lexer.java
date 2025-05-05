package atv.CompiladoresLLM.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private static final String[] KEYWORDS = { "if", "else", "while", "int", "float", "return" };

    private boolean isKeyword(String word) {
        for (String kw : KEYWORDS) {
            if (kw.equals(word)) return true;
        }
        return false;
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int i = 0, line = 1, column = 1;

        while (i < input.length()) {
            char current = input.charAt(i);

            if (Character.isWhitespace(current)) {
                if (current == '\n') {
                    line++;
                    column = 1;
                } else {
                    column++;
                }
                i++;
                continue;
            }

            int startColumn = column;

            // Identificadores e palavras-chave
            if (Character.isLetter(current)) {
                StringBuilder sb = new StringBuilder();
                while (i < input.length() && Character.isLetterOrDigit(input.charAt(i))) {
                    sb.append(input.charAt(i++));
                    column++;
                }
                String word = sb.toString();
                Token.TokenType type = isKeyword(word) ? Token.TokenType.KEYWORD : Token.TokenType.IDENT;
                tokens.add(new Token(type, word, line, startColumn));
                continue;
            }

            // Números inteiros
            if (Character.isDigit(current)) {
                StringBuilder sb = new StringBuilder();
                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    sb.append(input.charAt(i++));
                    column++;
                }
                tokens.add(new Token(Token.TokenType.INT, sb.toString(), line, startColumn));
                continue;
            }

            // Strings entre aspas
            if (current == '"') {
                StringBuilder sb = new StringBuilder();
                sb.append(current);
                i++;
                column++;
                boolean closed = false;
                while (i < input.length()) {
                    char c = input.charAt(i);
                    sb.append(c);
                    i++;
                    column++;
                    if (c == '"') {
                        closed = true;
                        break;
                    }
                    if (c == '\n') {
                        line++;
                        column = 1;
                    }
                }
                String value = sb.toString();
                if (closed) {
                    tokens.add(new Token(Token.TokenType.STRING, value, line, startColumn));
                } else {
                    tokens.add(new Token(Token.TokenType.UNKNOWN, value, line, startColumn));
                }
                continue;
            }

            // Comentário de linha
            if (current == '/' && i + 1 < input.length() && input.charAt(i + 1) == '/') {
                StringBuilder sb = new StringBuilder();
                sb.append("//");
                i += 2;
                column += 2;
                while (i < input.length() && input.charAt(i) != '\n') {
                    sb.append(input.charAt(i++));
                    column++;
                }
                tokens.add(new Token(Token.TokenType.COMMENT, sb.toString(), line, startColumn));
                continue;
            }

            // Comentário de bloco
            if (current == '/' && i + 1 < input.length() && input.charAt(i + 1) == '*') {
                StringBuilder sb = new StringBuilder();
                sb.append("/*");
                i += 2;
                column += 2;
                boolean closed = false;
                while (i < input.length()) {
                    char c = input.charAt(i);
                    sb.append(c);
                    if (c == '\n') {
                        line++;
                        column = 1;
                    } else {
                        column++;
                    }
                    if (c == '*' && i + 1 < input.length() && input.charAt(i + 1) == '/') {
                        sb.append('/');
                        i += 2;
                        column += 2;
                        closed = true;
                        break;
                    } else {
                        i++;
                    }
                }
                String value = sb.toString();
                Token.TokenType type = closed ? Token.TokenType.COMMENT : Token.TokenType.UNKNOWN;
                tokens.add(new Token(type, value, line, startColumn));
                continue;
            }

            // Símbolos reconhecidos
            if ("+-*/=;(){}[]<>!&|^~,".indexOf(current) >= 0) {
                tokens.add(new Token(Token.TokenType.SYMBOL, String.valueOf(current), line, column));
                i++;
                column++;
                continue;
            }

            // Qualquer outro caractere é erro
            tokens.add(new Token(Token.TokenType.UNKNOWN, String.valueOf(current), line, column));
            i++;
            column++;
        }

        tokens.add(new Token(Token.TokenType.EOF, "", line, column));
        return tokens;
    }
}
