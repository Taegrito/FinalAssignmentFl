import java.util.*;

public class Parser {

    List<Token> tokens;
    int position;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    public Token currentToken() {
        return tokens.get(position);
    }

    public void eat(TokenType expected) {
        if (currentToken().type == expected) {
            position++;
        } else {
            throw new RuntimeException(
                    "Error sintáctico. Se esperaba "
                            + expected
                            + " pero se encontró "
                            + currentToken().type
            );
        }
    }

    public AST parseProgram() {
        ProgramNode program = new ProgramNode();

        while(currentToken().type == TokenType.RULE){
            program.rules.add(parseRule());
        }

        eat(TokenType.EOF);

        return program;
    }

    public RuleNode parseRule() {
        eat(TokenType.RULE);

        String ruleId = currentToken().value;
        eat(TokenType.ID);

        eat(TokenType.COLON);
        eat(TokenType.IF);

        ConditionNode condition = parseCondition();

        eat(TokenType.THEN);

        String actionId = currentToken().value;
        parseAction();

        return new RuleNode(ruleId, condition, actionId);
    }

    public ConditionNode parseCondition() {

        String leftId = currentToken().value;
        eat(TokenType.ID);

        if(currentToken().type==TokenType.THEN){
            return new SimpleConditionNode(leftId,null,null);
        }

        String operator = currentToken().value;
        parseOperator();

        String rightValue = currentToken().value;
        eat(TokenType.VALUE);

        ConditionNode currentCondition =
                new SimpleConditionNode(leftId, operator, rightValue);

        while (currentToken().type == TokenType.AND) {

            eat(TokenType.AND);

            String nextLeftId = currentToken().value;
            eat(TokenType.ID);

            String nextOperator = currentToken().value;
            parseOperator();

            String nextRightValue = currentToken().value;
            eat(TokenType.VALUE);

            ConditionNode nextSimple =
                    new SimpleConditionNode(nextLeftId,nextOperator,nextRightValue);

            currentCondition =
                    new BinaryConditionNode(currentCondition,"AND",nextSimple);
        }

        return currentCondition;
    }

    public void parseOperator() {
        if (currentToken().type == TokenType.GREATER) {
            eat(TokenType.GREATER);
        } else if (currentToken().type == TokenType.LESS) {
            eat(TokenType.LESS);
        } else if (currentToken().type == TokenType.EQUAL) {
            eat(TokenType.EQUAL);
        } else {
            throw new RuntimeException("Operador inválido");
        }
    }

    public void parseAction() {
        eat(TokenType.ID);
    }
}