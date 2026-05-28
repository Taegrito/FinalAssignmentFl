import java.util.ArrayList;
import java.util.List;

// Clase abstracta principal del AST
// Todos los nodos del árbol heredan de esta clase
abstract class AST {

    // Método abstracto para imprimir el árbol
    // Cada nodo lo implementa de manera diferente
    public abstract void print(String indent);
}

// Nodo principal del programa
// Guarda todas las reglas del sistema
class ProgramNode extends AST {

    // Lista donde se almacenan las reglas
    List<RuleNode> rules;

    // Constructor del programa
    public ProgramNode() {

        // Se inicializa la lista de reglas
        rules = new ArrayList<>();
    }

    @Override
    public void print(String indent) {

        // Recorre e imprime todas las reglas del programa
        for (RuleNode rule : rules) {
            rule.print(indent);
        }
    }
}

// Nodo que representa una regla completa
class RuleNode extends AST {

    // Identificador de la regla
    String ruleId;
    // Condición de la regla
    ConditionNode condition;
    // Acción que se ejecutará si la condición es verdadera
    String actionId;

    // Constructor de la regla
    public RuleNode(String ruleId, ConditionNode condition, String actionId) {

        this.ruleId = ruleId;
        this.condition = condition;
        this.actionId = actionId;
    }

    @Override
    public void print(String indent) {

        // Imprime el nombre de la regla
        System.out.println(indent + "[Rule: " + ruleId + "]");

        // Imprime la sección de condición
        System.out.println(indent + "  ├── Condition:");

        // Verifica que exista una condición
        if (condition != null) {

            // Imprime recursivamente la condición
            condition.print(indent + "  │     ");
        }

        // Imprime la acción de la regla
        System.out.println(indent + "  └── Action: " + actionId);
    }
}

// Clase abstracta para representar condiciones
// Todas las condiciones heredan de esta clase
abstract class ConditionNode extends AST {}

// Nodo para condiciones simples
class SimpleConditionNode extends ConditionNode {

    // Parte izquierda de la condición
    String leftId;
    // Operador lógico o relacional
    String operator;
    // Valor derecho de la condición
    String rightValue;

    // Constructor de la condición simple
    public SimpleConditionNode(String leftId, String operator, String rightValue) {

        this.leftId = leftId;
        this.operator = operator;
        this.rightValue = rightValue;
    }

    @Override
    public void print(String indent) {

        // Si el operador es null significa que es un hecho simple
        if(operator == null){

            // Imprime únicamente el identificador
            System.out.println(indent + leftId);

        } else {

            // Imprime la condición completa
            System.out.println(indent + leftId + " " + operator + " " + rightValue);
        }
    }
}

// Nodo para condiciones compuestas
class BinaryConditionNode extends ConditionNode {

    // Condición izquierda
    ConditionNode left;
    // Operador lógico
    String operator;
    // Condición derecha
    ConditionNode right;

    // Constructor de la condición binaria
    public BinaryConditionNode(ConditionNode left, String operator, ConditionNode right) {

        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public void print(String indent) {

        // Imprime el operador lógico principal
        System.out.println(indent + "[" + operator + "]");
        // Imprime la rama izquierda
        System.out.println(indent + "  ├── Left:");
        // Imprime recursivamente el lado izquierdo
        left.print(indent + "  │     ");
        // Imprime la rama derecha
        System.out.println(indent + "  └── Right:");
        // Imprime recursivamente el lado derecho
        right.print(indent + "        ");
    }
}