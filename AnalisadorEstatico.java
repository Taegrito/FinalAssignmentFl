import java.util.HashMap;
import java.util.Map;
public class AnalisadorEstatico {

    // Tabla de símbolos para almacenar el nombre de la variable/hecho y su tipo (NUMBER o ACTION)
    private Map<String, String> symbolTable;

    public AnalisadorEstatico() {
        symbolTable = new HashMap<>();
        // Se predefinen las variables del ambiente físico como tipos numéricos (NUMBER)
        symbolTable.put("temp", "NUMBER");
        symbolTable.put("humidity", "NUMBER");
        // Se predefine el hecho global básico como un tipo de acción (ACTION)
        symbolTable.put("alert", "ACTION");
    }
    public void analyze(AST root) {
        // Verifica si el nodo raíz corresponde efectivamente a un programa completo
        if (root instanceof ProgramNode) {
            ProgramNode program = (ProgramNode) root;
            
            // --- PASO 1: POBLACIÓN DINÁMICA DE SÍMBOLOS ---
            // Se realiza una primera pasada sobre todas las reglas para registrar 
            // las variables y hechos que el usuario introdujo en el código.
            for (RuleNode rule : program.rules) {
                // Si la acción que ejecuta la regla no está en la tabla de símbolos, se registra como ACTION
                if (!symbolTable.containsKey(rule.actionId)) {
                    symbolTable.put(rule.actionId, "ACTION");
                }
                // Registra de forma recursiva los componentes internos de la condición de la regla
                registerVariables(rule.condition);
            }

            // --- PASO 2: ANÁLISIS Y VALIDACIÓN SEMÁNTICA ---
            // Segunda pasada para verificar que no haya inconsistencias ni variables huérfanas
            for (RuleNode rule : program.rules) {
                analyzeRule(rule);
            }
        }
    }
    private void registerVariables(ConditionNode condition) {
        // Caso base: La condición es simple (ej: 'temp > 30' o 'if alert')
        if (condition instanceof SimpleConditionNode) {
            SimpleConditionNode simple = (SimpleConditionNode) condition;
            
            // Si tiene un operador relacional (<, >, =), significa que el identificador es una variable numérica
            if (simple.operator != null && !symbolTable.containsKey(simple.leftId)) {
                symbolTable.put(simple.leftId, "NUMBER");
            } 
            // Si NO tiene un operador relacional, significa que es la validación de un hecho simbólico (booleano)
            else if (simple.operator == null && !symbolTable.containsKey(simple.leftId)) {
                symbolTable.put(simple.leftId, "ACTION");
            }
        } 
        // Caso recursivo: La condición es compuesta/binaria (unida por un operador lógico como AND)
        else if (condition instanceof BinaryConditionNode) {
            BinaryConditionNode binary = (BinaryConditionNode) condition;
            // Se procesa recursivamente la rama izquierda del operador lógico
            registerVariables(binary.left);
            // Se procesa recursivamente la rama derecha del operador lógico
            registerVariables(binary.right);
        }
    }
    private void analyzeRule(RuleNode rule) {
        // Redirige el flujo para examinar minuciosamente la estructura de la condición
        analyzeCondition(rule.condition);
    }

    private void analyzeCondition(ConditionNode condition) {
        // Caso base: Validación de una condición atómica o simple
        if (condition instanceof SimpleConditionNode) {
            SimpleConditionNode simple = (SimpleConditionNode) condition;
            
            // Verifica si el identificador de la izquierda existe en la tabla de símbolos
            if (!symbolTable.containsKey(simple.leftId)) {
                // Si no se encuentra registrado en ninguna parte, se detiene el compilador por error semántico
                throw new RuntimeException("Error Semántico: La variable o hecho '" + simple.leftId + "' no existe.");
            }
        } 
        // Caso recursivo: Validación de árboles binarios de condiciones (Expresiones compuestas por AND)
        else if (condition instanceof BinaryConditionNode) {
            BinaryConditionNode binary = (BinaryConditionNode) condition;
            // Desciende y analiza semánticamente el lado izquierdo del árbol de condiciones
            analyzeCondition(binary.left);
            // Desciende y analiza semánticamente el lado derecho del árbol de condiciones
            analyzeCondition(binary.right);
        }
    }
}