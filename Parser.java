import java.util.*;

public class Parser {

    // Lista de tokens generados por el lexer
    List<Token> tokens;

    // Posición actual dentro de la lista de tokens
    int position;

    // Constructor del parser
    public Parser(List<Token> tokens) {

        // Guarda la lista de tokens recibida
        this.tokens = tokens;

        // El parser comienza desde la primera posición
        this.position = 0;
    }

    // Retorna el token actual que se está analizando
    public Token currentToken() {

        return tokens.get(position);
    }

    // Método que valida el token esperado
    public void eat(TokenType expected) {

        // Verifica si el token actual coincide con el esperado
        if (currentToken().type == expected) {

            // Avanza a la siguiente posición
            position++;

        } else {

            // Lanza error sintáctico si el token no coincide
            throw new RuntimeException("Error sintáctico. Se esperaba " + expected + " pero se encontró " + currentToken().type);
        }
    }

    // Método principal del parser
    // Construye el AST completo del programa
    public AST parseProgram() {

        // Se crea el nodo principal del programa
        ProgramNode program = new ProgramNode();

        // Mientras existan reglas se siguen parseando
        while(currentToken().type == TokenType.RULE){

            // Agrega cada regla a la lista del programa
            program.rules.add(parseRule());
        }

        // Verifica que el archivo termine correctamente
        eat(TokenType.EOF);

        // Retorna el AST completo
        return program;
    }

    // Parsea una regla completa
    public RuleNode parseRule() {

        // Consume la palabra RULE
        eat(TokenType.RULE);

        // Obtiene el identificador de la regla
        String ruleId = currentToken().value;

        // Consume el identificador
        eat(TokenType.ID);

        // Consume :
        eat(TokenType.COLON);

        // Consume IF
        eat(TokenType.IF);

        // Parsea la condición de la regla
        ConditionNode condition = parseCondition();

        // Consume THEN
        eat(TokenType.THEN);

        // Obtiene el identificador de la acción
        String actionId = currentToken().value;

        // Parsea la acción
        parseAction();

        // Construye y retorna el nodo de la regla
        return new RuleNode(ruleId, condition, actionId);
    }

    // Parsea las condiciones de la regla
    public ConditionNode parseCondition() {

        // Obtiene la parte izquierda de la condición
        String leftId = currentToken().value;

        // Consume el identificador
        eat(TokenType.ID);

        // Verifica si la condición es un hecho simple
        if(currentToken().type == TokenType.THEN){

            // Retorna una condición simple sin operador
            return new SimpleConditionNode(leftId, null, null);
        }

        // Obtiene el operador de la condición
        String operator = currentToken().value;

        // Parsea el operador
        parseOperator();

        // Obtiene el valor derecho de la condición
        String rightValue = currentToken().value;

        // Consume el valor
        eat(TokenType.VALUE);

        // Crea la condición simple inicial
        ConditionNode currentCondition = new SimpleConditionNode(leftId, operator, rightValue);

        // Mientras existan operadores AND
        while (currentToken().type == TokenType.AND) {

            // Consume AND
            eat(TokenType.AND);
            // Obtiene el siguiente identificador
            String nextLeftId = currentToken().value;
            // Consume el identificador
            eat(TokenType.ID);
            // Obtiene el siguiente operador
            String nextOperator = currentToken().value;
            // Parsea el operador
            parseOperator();
            // Obtiene el siguiente valor
            String nextRightValue = currentToken().value;
            // Consume el valor
            eat(TokenType.VALUE);
            // Construye la nueva condición simple
            ConditionNode nextSimple = new SimpleConditionNode(nextLeftId, nextOperator, nextRightValue);
            // Une ambas condiciones en un nodo binario
            currentCondition = new BinaryConditionNode(currentCondition, "AND", nextSimple);
        }

        // Retorna la condición final
        return currentCondition;
    }

    // Parsea los operadores válidos
    public void parseOperator() {

        // Verifica si el operador es >
        if (currentToken().type == TokenType.GREATER) {

            eat(TokenType.GREATER);
        // Verifica si el operador es <
        } else if (currentToken().type == TokenType.LESS) {

            eat(TokenType.LESS);
        // Verifica si el operador es =
        } else if (currentToken().type == TokenType.EQUAL) {

            eat(TokenType.EQUAL);
        } else {

            // Lanza error si el operador no es válido
            throw new RuntimeException("Operador inválido");
        }
    }

    // Parsea la acción de la regla
    public void parseAction() {

        // Consume el identificador de la acción
        eat(TokenType.ID);
    }
}