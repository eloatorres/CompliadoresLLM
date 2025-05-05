package atv.CompiladoresLLM.lexer;

public class Token {
    public enum TokenType {
        IDENT, INT, FLOAT, STRING, KEYWORD, SYMBOL, COMMENT, WHITESPACE, UNKNOWN, EOF
    }

    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;

    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() { return type; }
    public String getValue() { return value; }
    public int getLine() { return line; }
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" (linha: %d, coluna: %d)", type, value, line, column);
    }
}
