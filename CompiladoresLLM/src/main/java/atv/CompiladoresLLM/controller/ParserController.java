package atv.CompiladoresLLM.controller;

import atv.CompiladoresLLM.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ParserController {

    @Autowired
    private ParserService parserService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/validar")
    public String validarExpressao(@RequestParam String expressao, Model model) {
        String resultado = parserService.analisarExpressao(expressao, model);
        model.addAttribute("resultado", resultado);
        model.addAttribute("expressao", expressao);
        return "index";
    }
}
