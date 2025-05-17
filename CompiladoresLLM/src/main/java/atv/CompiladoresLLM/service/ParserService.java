package atv.CompiladoresLLM.service;

import atv.CompiladoresLLM.parser.ParseTreeNode;
import atv.CompiladoresLLM.parser.RecursiveDescentParser;
import atv.CompiladoresLLM.semantic.SemanticAnalyzer;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class ParserService {

    public String analisarExpressao(String expressao, ModelMap model) {
        RecursiveDescentParser parser = new RecursiveDescentParser();
        try {
            ParseTreeNode raiz = parser.parse(expressao);
            model.addAttribute("arvoreHtml", gerarHtmlArvore(raiz));

            // 🔽 Análise semântica
            SemanticAnalyzer semantico = new SemanticAnalyzer();
            String resultadoSemantico = semantico.analisar(raiz);
            model.addAttribute("resultadoSemantico", resultadoSemantico);

            return "Expressão válida!";
        } catch (Exception e) {
            model.addAttribute("resultadoSemantico", "Erro semântico: " + e.getMessage());
            return "Erro sintático: " + e.getMessage();
        }
    }

    private String gerarHtmlArvore(ParseTreeNode node) {
        StringBuilder sb = new StringBuilder();
        gerarHtmlRecursivo(node, sb);
        return sb.toString();
    }

    private void gerarHtmlRecursivo(ParseTreeNode node, StringBuilder sb) {
        sb.append("<ul><li>").append(node.getSymbol());
        for (ParseTreeNode child : node.getChildren()) {
            gerarHtmlRecursivo(child, sb);
        }
        sb.append("</li></ul>");
    }
}
