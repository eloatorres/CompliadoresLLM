package parser;

import org.junit.jupiter.api.Test;

import atv.CompiladoresLLM.parser.RecursiveDescentParser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class RecursiveDescentParserTest {

    private final RecursiveDescentParser parser = new RecursiveDescentParser();

        @Test
    public void testExpressoesValidas() {
        List<String> validas = List.of(
            "id",
            "~ id",
            "id ^ id",
            "( id )",
            "( id & id )",
            "id | id",
            "~ ( id ^ id )",
            "id ^ ( id | id ) & id",
            "id & id | id ^ id",
            "~ ( ( id ) )",
            "( id ^ id ) & id"
        );

        for (String expr : validas) {
            try {
                parser.parse(expr);
                System.out.println("✅ Válida: " + expr);
            } catch (Exception e) {
                fail("❌ Expressão válida falhou: " + expr + " → " + e.getMessage());
            }
        }
    }

    @Test
    public void testExpressoesInvalidas() {
        List<String> invalidas = List.of(
            "id id",            // dois ids sem operador
            "id &",             // operador sem segundo operando
            "& id",             // operador no início
            "( id",             // parêntese não fechado
            "id ^ ^ id",        // operador duplicado
            "~ ) id (",         // ordem invertida
            "~ ( id ^ )",       // expressão incompleta
            "( id & )",         // falta de segundo termo
            "~",                // operador sozinho
            ""                  // string vazia
        );

        for (String expr : invalidas) {
            assertThrows(RuntimeException.class, () -> parser.parse(expr),
                "❌ Expressão inválida deveria falhar: " + expr);
        }
    }
}
