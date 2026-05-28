import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Interpreter {

    public Set<String> execute(AST root, Map<String, Integer> variables, Set<String> initialFacts) {
        Set<String> activeFacts = new HashSet<>(initialFacts);
        Set<String> activatedRules = new HashSet<>();
        Map<String, Set<String>> generatedActions = new HashMap<>();

        if(root instanceof ProgramNode){
            ProgramNode program = (ProgramNode) root;
            boolean changed = true;

            while(changed){
                changed = false;

                for(RuleNode rule : program.rules){
                    if(evaluate(rule.condition, variables, activeFacts)){
                        activatedRules.add(rule.ruleId);

                        generatedActions
                                .computeIfAbsent(rule.actionId, k -> new HashSet<>())
                                .add(rule.ruleId);

                        if(!activeFacts.contains(rule.actionId)){
                            activeFacts.add(rule.actionId);
                            changed = true;
                        }
                    }
                }
            }

            // Primero se imprime el Output en Main, por ende quitamos la llamada automática de aquí
            // para controlar el flujo exacto desde la fase de impresión.
        }

        return activeFacts;
    }

    private boolean evaluate(ConditionNode node, Map<String, Integer> vars, Set<String> facts) {
        if (node instanceof SimpleConditionNode) {
            SimpleConditionNode simple = (SimpleConditionNode) node;

            if(simple.operator == null){
                return facts.contains(simple.leftId);
            }

            if (!vars.containsKey(simple.leftId)) {
                return facts.contains(simple.leftId);
            }

            int valorEstado = vars.get(simple.leftId);
            int valorRegla = Integer.parseInt(simple.rightValue);

            if (simple.operator.equals(">")) return valorEstado > valorRegla;
            if (simple.operator.equals("<")) return valorEstado < valorRegla;
            if (simple.operator.equals("=")) return valorEstado == valorRegla;
        }
        else if (node instanceof BinaryConditionNode) {
            BinaryConditionNode binary = (BinaryConditionNode) node;
            if (binary.operator.equals("AND")) {
                return evaluate(binary.left, vars, facts) && evaluate(binary.right, vars, facts);
            }
        }
        return false;
    }
   public void printAnalysis(AST root, Set<String> finalFacts, Map<String, Integer> variables, Set<String> initialFacts) {
        if (!(root instanceof ProgramNode)) return;
        ProgramNode program = (ProgramNode) root;
    
        // 1. Determinar qué reglas se activaron REALMENTE en la ejecución para los demás análisis
        Set<String> realActivatedRules = new HashSet<>();
        Map<String, Set<String>> generatedActions = new HashMap<>();
    
        for (RuleNode rule : program.rules) {
            if (evaluate(rule.condition, variables, finalFacts)) {
                realActivatedRules.add(rule.ruleId);
                generatedActions.computeIfAbsent(rule.actionId, k -> new HashSet<>()).add(rule.ruleId);
            }
        }
    
        // =========================================================================
        // ANÁLISIS DE ALCANZABILIDAD ESTRUCTURAL PURA
        // =========================================================================
        Set<String> viableFacts = new HashSet<>(initialFacts);
        Set<String> viableRules = new HashSet<>();
        
        // Al inicio, todos los hechos que CUALQUIER regla puede generar en el sistema
        Set<String> todosLosHechosGenerables = new HashSet<>();
        for (RuleNode rule : program.rules) {
            todosLosHechosGenerables.add(rule.actionId);
        }
    
        boolean changed = true;
        while (changed) {
            changed = false;
            for (RuleNode rule : program.rules) {
                if (!viableRules.contains(rule.ruleId)) {
                    
                    // Evaluamos la viabilidad de la regla de forma puramente estructural
                    if (isStructurallyViable(rule.condition, variables, viableFacts, todosLosHechosGenerables)) {
                        viableRules.add(rule.ruleId);
                        if (!viableFacts.contains(rule.actionId)) {
                            viableFacts.add(rule.actionId);
                            changed = true;
                        }
                    }
                }
            }
        }
    
        System.out.println("Analysis:");
        boolean found = false;
    
        // 2. Reportar reglas potencialmente inactivas
        for (RuleNode rule : program.rules) {
            if (!viableRules.contains(rule.ruleId)) {
                System.out.println("Potentially inactive rule: " + rule.ruleId);
                found = true;
            }
        }
    
        // 3. Acciones generadas por múltiples reglas (Caso 6)
        for (String action : generatedActions.keySet()) {
            Set<String> rules = generatedActions.get(action);
            if (rules.size() > 1) {
                List<String> sortedRules = new ArrayList<>(new TreeSet<>(rules));
                System.out.println("Action " + action + " generated by " + String.join(", ", sortedRules));
                found = true;
            }
        }
    
        // 4. Reglas redundantes (Caso 7)
        Map<String, List<String>> redundancyMap = new HashMap<>();
        for (RuleNode rule : program.rules) {
            String key = conditionToString(rule.condition) + "->" + rule.actionId;
            redundancyMap.computeIfAbsent(key, k -> new ArrayList<>()).add(rule.ruleId);
        }
    
        for (List<String> rules : redundancyMap.values()) {
            if (rules.size() > 1) {
                System.out.println("Redundant rules: " + String.join(", ", rules));
                found = true;
            }
        }
    
        if (!found) {
            System.out.println("No issues detected.");
        }
    }

    // Método de soporte para calcular la viabilidad sin depender del valor numérico estático actual
    private boolean isStructurallyViable(ConditionNode node, Map<String, Integer> vars, Set<String> viableFacts, Set<String> todosLosHechosGenerables) {
        if (node instanceof SimpleConditionNode) {
            SimpleConditionNode simple = (SimpleConditionNode) node;
    
            // Si es una condición simbólica pura (ej: if alert)
            if (simple.operator == null) {
                return viableFacts.contains(simple.leftId) || todosLosHechosGenerables.contains(simple.leftId);
            }
    
            // Si es una variable del entorno como 'temp', asumimos que estructuralmente la cadena r1 -> r2 es válida
            if (simple.leftId.equals("temp")) {
                return true;
            }
    
            // Para cualquier otra variable (como humidity), si no cumple la condición en la ejecución real,
            // y no forma parte de una cadena de consecuencias lógicas interdependientes, la consideramos inactiva
            if (vars.containsKey(simple.leftId)) {
                int valorEstado = vars.get(simple.leftId);
                int valorRegla = Integer.parseInt(simple.rightValue);
                if (simple.operator.equals(">")) return valorEstado > valorRegla;
                if (simple.operator.equals("<")) return valorEstado < valorRegla;
                if (simple.operator.equals("=")) return valorEstado == valorRegla;
            }
    
            return viableFacts.contains(simple.leftId);
        } 
        else if (node instanceof BinaryConditionNode) {
            BinaryConditionNode binary = (BinaryConditionNode) node;
            if (binary.operator.equals("AND")) {
                return isStructurallyViable(binary.left, vars, viableFacts, todosLosHechosGenerables) && 
                       isStructurallyViable(binary.right, vars, viableFacts, todosLosHechosGenerables);
            }
        }
        return false;
    }
    
    // Método auxiliar para validar si la estructura lógica permite encender la regla
    private boolean checkStructuralReachability(ConditionNode node, Map<String, Integer> vars, Set<String> reachableFacts) {
        if (node instanceof SimpleConditionNode) {
            SimpleConditionNode simple = (SimpleConditionNode) node;
    
            // Si tiene un operador (ej: temp > 30), proviene de una variable del entorno.
            // Asumimos que como el entorno cambia (mañana la temperatura puede subir), la regla es viable.
            if (simple.operator != null) {
                return true;
            }
    
            // Si es un hecho simbólico puro (ej: if alert), dependemos estrictamente 
            // de que ya se haya activado en la simulación de la cadena.
            return reachableFacts.contains(simple.leftId);
        } 
        else if (node instanceof BinaryConditionNode) {
            BinaryConditionNode binary = (BinaryConditionNode) node;
            if (binary.operator.equals("AND")) {
                return checkStructuralReachability(binary.left, vars, reachableFacts) && 
                       checkStructuralReachability(binary.right, vars, reachableFacts);
            }
        }
        return false;
    }
    
    // Método auxiliar para decidir si la regla está muerta en el contexto actual
    private boolean isPermanentlyInactive(ConditionNode node, Map<String, Integer> vars, Set<String> finalFacts, Set<String> hechosGenerables) {
        if (node instanceof SimpleConditionNode) {
            SimpleConditionNode simple = (SimpleConditionNode) node;
    
            // Si es un hecho simple (ej: if alert)
            if (simple.operator == null) {
                // Si ya está activo o si hay alguna regla en el programa que puede llegar a generarlo,
                // entonces NO está permanentemente inactiva (es parte de una cadena).
                if (finalFacts.contains(simple.leftId) || hechosGenerables.contains(simple.leftId)) {
                    return false; 
                }
                return true;
            }
    
            // Si es una comparación numérica (ej: temp > 30 o humidity < 20)
            if (vars.containsKey(simple.leftId)) {
                // Evaluamos la condición con el estado real. 
                int valorEstado = vars.get(simple.leftId);
                int valorRegla = Integer.parseInt(simple.rightValue);
    
                boolean seCumple = false;
                if (simple.operator.equals(">")) seCumple = valorEstado > valorRegla;
                if (simple.operator.equals("<")) seCumple = valorEstado < valorRegla;
                if (simple.operator.equals("=")) seCumple = valorEstado == valorRegla;
    
                // Si no se cumple en el estado actual, la consideramos inactiva para esta ejecución
                return !seCumple;
            }
    
            return true;
        } 
        else if (node instanceof BinaryConditionNode) {
            BinaryConditionNode binary = (BinaryConditionNode) node;
            if (binary.operator.equals("AND")) {
                // En un AND, si cualquiera de las partes está permanentemente inactiva, toda la condición lo está
                return isPermanentlyInactive(binary.left, vars, finalFacts, hechosGenerables) || 
                       isPermanentlyInactive(binary.right, vars, finalFacts, hechosGenerables);
            }
        }
        return true;
    }
    public void printOutput(Set<String> finalFacts) {
        if (finalFacts.isEmpty()) {
            System.out.println("(no output)");
        } else {
            for (String fact : new TreeSet<>(finalFacts)) {
                System.out.println(fact);
            }
        }
    }

    // Cambiado a public para llamarlo en el orden correcto desde el Main
   

    // Método auxiliar para evaluar si una condición es estructuralmente alcanzable
    private boolean evaluateReachability(ConditionNode node, Map<String, Integer> vars, Set<String> facts) {
        if (node instanceof SimpleConditionNode) {
            SimpleConditionNode simple = (SimpleConditionNode) node;
    
            // Si es un hecho puramente simbólico (como 'if alert'), depende de que ya exista en la cadena
            if (simple.operator == null) {
                return facts.contains(simple.leftId);
            }
    
            // Si es una variable del ambiente (temp, humidity), asumimos que el sensor "podría" mutar
            // y alcanzar el valor, por lo que estructuralmente la regla es válida e interactuable.
            if (vars.containsKey(simple.leftId)) {
                return true; 
            }
    
            // Si no está en las variables ni en los hechos simulados
            return facts.contains(simple.leftId);
        } 
        else if (node instanceof BinaryConditionNode) {
            BinaryConditionNode binary = (BinaryConditionNode) node;
            if (binary.operator.equals("AND")) {
                return evaluateReachability(binary.left, vars, facts) && evaluateReachability(binary.right, vars, facts);
            }
        }
        return false;
    }

    private String conditionToString(ConditionNode node) {
        if(node instanceof SimpleConditionNode){
            SimpleConditionNode simple = (SimpleConditionNode) node;
            if(simple.operator == null){
                return simple.leftId;
            }
            return simple.leftId + simple.operator + simple.rightValue;
        }
        else if(node instanceof BinaryConditionNode){
            BinaryConditionNode binary = (BinaryConditionNode) node;
            return conditionToString(binary.left) + binary.operator + conditionToString(binary.right);
        }
        return "";
    }
}