package atv.CompiladoresLLM.parser;

import java.util.*;

public class FirstFollowCalculator {

    private static final Map<String, List<List<String>>> grammar = new HashMap<>();
    private static final Map<String, Set<String>> first = new HashMap<>();
    private static final Map<String, Set<String>> follow = new HashMap<>();
    private static final String EPSILON = "ε";
    private static final String START_SYMBOL = "E";

    static {
        grammar.put("E", Arrays.asList(Arrays.asList("T", "E'")));
        grammar.put("E'", Arrays.asList(
                Arrays.asList("&", "T", "E'"),
                Arrays.asList("|", "T", "E'"),
                Arrays.asList(EPSILON)
        ));
        grammar.put("T", Arrays.asList(Arrays.asList("F", "T'")));
        grammar.put("T'", Arrays.asList(
                Arrays.asList("^", "F", "T'"),
                Arrays.asList(EPSILON)
        ));
        grammar.put("F", Arrays.asList(
                Arrays.asList("~", "F"),
                Arrays.asList("(", "E", ")"),
                Arrays.asList("id")
        ));
    }

    public static void main(String[] args) {
        computeFirst();
        computeFollow();

        System.out.println("==== CONJUNTOS FIRST ====");
        first.forEach((nt, set) -> System.out.println("First(" + nt + ") = " + set));
        System.out.println("\n==== CONJUNTOS FOLLOW ====");
        follow.forEach((nt, set) -> System.out.println("Follow(" + nt + ") = " + set));
    }

    private static void computeFirst() {
        for (String nonTerminal : grammar.keySet()) {
            first.put(nonTerminal, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (String nt : grammar.keySet()) {
                for (List<String> production : grammar.get(nt)) {
                    for (int i = 0; i < production.size(); i++) {
                        String symbol = production.get(i);
                        Set<String> firstSet = getFirst(symbol);
                        boolean hasEpsilon = firstSet.contains(EPSILON);
                        firstSet.remove(EPSILON);
                        if (first.get(nt).addAll(firstSet)) {
                            changed = true;
                        }
                        if (!hasEpsilon) break;
                        if (i == production.size() - 1) {
                            if (first.get(nt).add(EPSILON)) changed = true;
                        }
                    }
                }
            }
        } while (changed);
    }

    private static Set<String> getFirst(String symbol) {
        if (!grammar.containsKey(symbol)) {
            return new HashSet<>(Collections.singletonList(symbol));
        }
        return new HashSet<>(first.get(symbol));
    }

    private static void computeFollow() {
        for (String nonTerminal : grammar.keySet()) {
            follow.put(nonTerminal, new HashSet<>());
        }

        follow.get(START_SYMBOL).add("$");

        boolean changed;
        do {
            changed = false;
            for (String lhs : grammar.keySet()) {
                for (List<String> production : grammar.get(lhs)) {
                    for (int i = 0; i < production.size(); i++) {
                        String B = production.get(i);
                        if (!grammar.containsKey(B)) continue;

                        Set<String> followB = follow.get(B);
                        int j = i + 1;
                        while (j < production.size()) {
                            String beta = production.get(j);
                            Set<String> firstBeta = getFirst(beta);
                            boolean hadEpsilon = firstBeta.contains(EPSILON);
                            firstBeta.remove(EPSILON);
                            if (followB.addAll(firstBeta)) changed = true;
                            if (!hadEpsilon) break;
                            j++;
                        }
                        if (j == production.size()) {
                            if (followB.addAll(follow.get(lhs))) changed = true;
                        }
                    }
                }
            }
        } while (changed);
    }
}
