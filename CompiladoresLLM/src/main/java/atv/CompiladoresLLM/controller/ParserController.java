package atv.CompiladoresLLM.controller;

import atv.CompiladoresLLM.lexer.Lexer;
import atv.CompiladoresLLM.lexer.Token;
import atv.CompiladoresLLM.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ParserController {

    @Autowired
    private ParserService parserService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // (opcional, usado se o HTML ainda tiver <form action="/validar">)
    @PostMapping("/validar")
    public String validarExpressao(@RequestParam String expressao, ModelMap model) {
        String resultado = parserService.analisarExpressao(expressao, model);
        model.addAttribute("resultado", resultado);
        return "index";
    }

    // Análise léxica via JSON
    @PostMapping("/api/lexico")
    @ResponseBody
    public ResponseEntity<List<Token>> analisarLexico(@RequestBody String expressao) {
        if (expressao.startsWith("\"") && expressao.endsWith("\"")) {
            expressao = expressao.substring(1, expressao.length() - 1);
        }
        Lexer lexer = new Lexer();
        return ResponseEntity.ok(lexer.tokenize(expressao));
    }

    // Análise sintática e semântica via JSON
    @PostMapping("/api/sintatico")
    @ResponseBody
    public Map<String, String> analisarSintatico(@RequestBody String expressao) {
        expressao = expressao.replaceAll("^\"|\"$", ""); // Remove aspas extras do JSON

        ModelMap model = new ModelMap();
        String resultado = parserService.analisarExpressao(expressao, model);
        String arvoreHtml = (String) model.get("arvoreHtml");
        String resultadoSemantico = (String) model.get("resultadoSemantico");

        Map<String, String> response = new HashMap<>();
        response.put("resultado", resultado);
        response.put("arvoreHtml", arvoreHtml != null ? arvoreHtml : "");
        response.put("resultadoSemantico", resultadoSemantico != null ? resultadoSemantico : "");

        return response;
    }
}
