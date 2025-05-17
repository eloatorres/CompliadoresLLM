package atv.CompiladoresLLM.parser;

import java.util.*;

public class RecursiveDescentParser {

    private List<String> tokens;
    private int position;

    public ParseTreeNode parse(String input) {
        tokens = tokenize(input);
        position = 0;

        ParseTreeNode root;
        String first = peek();
        if (isTipo(first)) {
            root = parseDeclOrAssign();
        } else {
            root = parseE();
        }

        // Verifica tokens restantes
        if (position < tokens.size()) {
            String unexpected = peek();
            throw new RuntimeException("Erro sintático: token inesperado '" + unexpected + "'");
        }
        return root;
    }

    private boolean isTipo(String token) {
        return Set.of("int", "float", "string", "bool").contains(token);
    }

    private boolean isIdentificador(String token) {
        return token != null
                && token.matches("[a-zA-Z_][a-zA-Z0-9_]*")
                && !isTipo(token);
    }

    private boolean isNumber(String token) {
        return token != null && token.matches("\\d+");
    }

    /**
     * Trata declarações simples ou declarações com atribuição:
     * DeclOrAssign -> tipo id [ '=' número ]
     */
    private ParseTreeNode parseDeclOrAssign() {
        ParseTreeNode node = new ParseTreeNode("Decl");
        // Consome tipo
        String tipo = consume();
        node.addChild(new ParseTreeNode(tipo));

        // Consome identificador
        String ident = peek();
        if (!isIdentificador(ident)) {
            throw new RuntimeException("Erro sintático: esperado identificador após tipo");
        }
        node.addChild(new ParseTreeNode(consume()));

        // Se houver '=', consome atribuição de literal numérico
        if ("=".equals(peek())) {
            node = new ParseTreeNode("DeclAtrib");
            node.addChild(new ParseTreeNode(tipo));
            node.addChild(new ParseTreeNode(ident));
            consume(); // '='
            String num = peek();
            if (!isNumber(num)) {
                throw new RuntimeException("Erro sintático: esperado número após '='");
            }
            node.addChild(new ParseTreeNode(consume()));
        }
        return node;
    }

    private List<String> tokenize(String input) {
        return Arrays.asList(input
                .replace("(", " ( ")
                .replace(")", " ) ")
                .replace("&", " & ")
                .replace("|", " | ")
                .replace("^", " ^ ")
                .replace("~", " ~ ")
                .replace("=", " = ")
                .trim().split("\\s+"));
    }

    private String peek() {
        return position < tokens.size() ? tokens.get(position) : null;
    }

    private String consume() {
        return tokens.get(position++);
    }

    private boolean match(String expected) {
        if (expected.equals(peek())) {
            consume();
            return true;
        }
        return false;
    }

    private ParseTreeNode parseE() {
        ParseTreeNode node = new ParseTreeNode("E");
        node.addChild(parseT());
        node.addChild(parseEPrime());
        return node;
    }

    private ParseTreeNode parseEPrime() {
        ParseTreeNode node = new ParseTreeNode("E'");
        String token = peek();
        if ("&".equals(token) || "|".equals(token)) {
            node.addChild(new ParseTreeNode(consume()));
            node.addChild(parseT());
            node.addChild(parseEPrime());
        } else {
            node.addChild(new ParseTreeNode("ε"));
        }
        return node;
    }

    private ParseTreeNode parseT() {
        ParseTreeNode node = new ParseTreeNode("T");
        node.addChild(parseF());
        node.addChild(parseTPrime());
        return node;
    }

    private ParseTreeNode parseTPrime() {
        ParseTreeNode node = new ParseTreeNode("T'");
        String token = peek();
        if ("^".equals(token)) {
            node.addChild(new ParseTreeNode(consume()));
            node.addChild(parseF());
            node.addChild(parseTPrime());
        } else {
            node.addChild(new ParseTreeNode("ε"));
        }
        return node;
    }

    private ParseTreeNode parseF() {
        ParseTreeNode node = new ParseTreeNode("F");
        String token = peek();
        if ("~".equals(token)) {
            node.addChild(new ParseTreeNode(consume()));
            node.addChild(parseF());
        } else if ("(".equals(token)) {
            node.addChild(new ParseTreeNode(consume()));
            node.addChild(parseE());
            if (!match(")")) {
                throw new RuntimeException("Erro sintático: esperado ')'");
            }
            node.addChild(new ParseTreeNode(")"));
        } else if (isIdentificador(token) || isNumber(token)) {
            node.addChild(new ParseTreeNode(consume()));
        } else {
            throw new RuntimeException("Erro sintático: token inesperado '" + token + "'");
        }
        return node;
    }
}
