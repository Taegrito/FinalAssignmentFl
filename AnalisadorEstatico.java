import java.util.HashMap;
import java.util.Map;

public class AnalisadorEstatico {

    private Map<String, String> symbolTable;

    public AnalisadorEstatico() {
        symbolTable = new HashMap<>();
        
        symbolTable.put("temp", "NUMBER");
        symbolTable.put("humidity", "NUMBER");
        symbolTable.put("alert", "ACTION");
    }

    public void analyze(AST root) {
        if (root instanceof RuleNode) {
            analyzeRule((RuleNode) root);
        }
    }

    private void analyzeRule(RuleNode rule) {
        System.out.println("Iniciando análisis estático para la regla: " + rule.ruleId);

        analyzeCondition(rule.condition);

        if (!symbolTable.containsKey(rule.actionId)) {
            throw new RuntimeException("Error Semántico: La acción '" + rule.actionId + "' no está definida.");
        }
        
        System.out.println("¡Análisis estático completado con éxito! El programa es semánticamente correcto.");
    }

    private void analyzeCondition(ConditionNode condition) {
        if (condition instanceof SimpleConditionNode) {
            SimpleConditionNode simple = (SimpleConditionNode) condition;
            if (!symbolTable.containsKey(simple.leftId)) {
                throw new RuntimeException("Error Semántico: La variable '" + simple.leftId + "' no existe.");
            }

            String leftType = symbolTable.get(simple.leftId);
            if (!leftType.equals("NUMBER")) {
                throw new RuntimeException("Error Semántico: No se puede aplicar el operador '" 
                    + simple.operator + "' sobre la variable '" + simple.leftId + "' porque es de tipo " + leftType);
            }


        } else if (condition instanceof BinaryConditionNode) {
            BinaryConditionNode binary = (BinaryConditionNode) condition;
            
            analyzeCondition(binary.left);
            analyzeCondition(binary.right);
        }
    }
}