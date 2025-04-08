package atv.CompiladoresLLM.parser;

import java.util.Arrays;
import java.util.List;

public class RecursiveDescentParser {

    private List<String> tokens;
    private int position;

    public ParseTreeNode parse(String input) {
        tokens = tokenize(input);
        position = 0;
        ParseTreeNode root = parseE();
        if (position < tokens.size()) {
            throw new RuntimeException("Erro: tokens restantes após o final da expressão.");
        }
        return root;
    }

    private List<String> tokenize(String input) {
        return Arrays.asList(input
                .replace("(", " ( ")
                .replace(")", " ) ")
                .replace("&", " & ")
                .replace("|", " | ")
                .replace("^", " ^ ")
                .replace("~", " ~ ")
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
                throw new RuntimeException("Erro: esperado ')'");
            }
            node.addChild(new ParseTreeNode(")"));
        } else if ("id".equals(token)) {
            node.addChild(new ParseTreeNode(consume()));
        } else {
            throw new RuntimeException("Erro: token inesperado '" + token + "'");
        }
        return node;
    }
}
