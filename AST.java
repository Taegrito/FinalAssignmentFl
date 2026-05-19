import java.util.ArrayList;
import java.util.List;

abstract class AST {
    public abstract void print(String indent);
}

class ProgramNode extends AST {
    List<RuleNode> rules;

    public ProgramNode() {
        rules = new ArrayList<>();
    }

    @Override
    public void print(String indent) {
        for (RuleNode rule : rules) {
            rule.print(indent);
        }
    }
}

class RuleNode extends AST {
    String ruleId;
    ConditionNode condition;
    String actionId;

    public RuleNode(String ruleId, ConditionNode condition, String actionId) {
        this.ruleId = ruleId;
        this.condition = condition;
        this.actionId = actionId;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "[Rule: " + ruleId + "]");
        System.out.println(indent + "  ├── Condition:");
        if (condition != null) {
            condition.print(indent + "  │     ");
        }
        System.out.println(indent + "  └── Action: " + actionId);
    }
}

abstract class ConditionNode extends AST {}

class SimpleConditionNode extends ConditionNode {
    String leftId;
    String operator;
    String rightValue;

    public SimpleConditionNode(String leftId, String operator, String rightValue) {
        this.leftId = leftId;
        this.operator = operator;
        this.rightValue = rightValue;
    }

    @Override
    public void print(String indent) {
        if(operator==null){
            System.out.println(indent + leftId);
        }else{
            System.out.println(indent + leftId + " " + operator + " " + rightValue);
        }
    }
}

class BinaryConditionNode extends ConditionNode {
    ConditionNode left;
    String operator;
    ConditionNode right;

    public BinaryConditionNode(ConditionNode left, String operator, ConditionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public void print(String indent) {
        System.out.println(indent + "[" + operator + "]");
        System.out.println(indent + "  ├── Left:");
        left.print(indent + "  │     ");
        System.out.println(indent + "  └── Right:");
        right.print(indent + "        ");
    }
}