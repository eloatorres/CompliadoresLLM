package atv.CompiladoresLLM.semantic;

import atv.CompiladoresLLM.parser.ParseTreeNode;
import java.util.*;

public class SemanticAnalyzer {

    private final Map<String, String> symbolTable = new HashMap<>();
    private final Set<String> validTypes = Set.of("int", "float", "string", "bool");
    private final List<String> errors = new ArrayList<>();

    public String analisar(ParseTreeNode raiz) {
        symbolTable.clear();
        errors.clear();
        analisarRec(raiz);
        if (errors.isEmpty()) {
            return "Sem erros semânticos.";
        } else {
            return String.join("\n", errors);
        }
    }

    private void analisarRec(ParseTreeNode node) {
        if (node == null) return;

        String symbol = node.getSymbol();
        // Declaração simples ou com atribuição
        if ("Decl".equals(symbol) || "DeclAtrib".equals(symbol)) {
            // Decl tem 2 filhos, DeclAtrib tem 3 filhos
            if (node.getChildren().size() >= 2) {
                String tipo = node.getChildren().get(0).getSymbol();
                String id = node.getChildren().get(1).getSymbol();
                if (!validTypes.contains(tipo)) {
                    errors.add("Erro semântico: tipo inválido '" + tipo + "'.");
                } else if (symbolTable.containsKey(id)) {
                    errors.add("Erro semântico: variável '" + id + "' já declarada.");
                } else {
                    symbolTable.put(id, tipo);
                }
            } else {
                errors.add("Erro semântico: declaração malformada.");
            }
            // não descer em filhos
            return;
        }

        // uso de identificadores (nós folha)
        if (node.getChildren().isEmpty()) {
            String sym = symbol;
            if (!validTypes.contains(sym) && sym.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                if (!symbolTable.containsKey(sym)) {
                    errors.add("Erro semântico: variável '" + sym + "' usada sem declaração.");
                }
            }
        }

        for (ParseTreeNode child : node.getChildren()) {
            analisarRec(child);
        }
    }
}
