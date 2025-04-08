package atv.CompiladoresLLM.parser;

import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {
    private final String symbol;
    private final List<ParseTreeNode> children = new ArrayList<>();

    public ParseTreeNode(String symbol) {
        this.symbol = symbol;
    }

    public void addChild(ParseTreeNode child) {
        children.add(child);
    }

    public String getSymbol() {
        return symbol;
    }

    public List<ParseTreeNode> getChildren() {
        return children;
    }

    public void printTree(String prefix) {
        System.out.println(prefix + symbol);
        for (ParseTreeNode child : children) {
            child.printTree(prefix + "  ");
        }
    }
}
