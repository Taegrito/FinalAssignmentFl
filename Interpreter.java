import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Interpreter {

    public Set<String> execute(AST root, Map<String, Integer> variables, Set<String> initialFacts) {

        Set<String> activeFacts = new HashSet<>(initialFacts);

        if(root instanceof ProgramNode){

            ProgramNode program = (ProgramNode) root;

            boolean changed=true;

            while(changed){

                changed=false;

                for(RuleNode rule : program.rules){

                    if(evaluate(rule.condition,variables,activeFacts)){

                        if(!activeFacts.contains(rule.actionId)){
                            activeFacts.add(rule.actionId);
                            changed=true;
                        }

                    }

                }

            }

        }

        return activeFacts;
    }

    private boolean evaluate(ConditionNode node, Map<String, Integer> vars, Set<String> facts) {

        if (node instanceof SimpleConditionNode) {

            SimpleConditionNode simple = (SimpleConditionNode) node;

            if(simple.operator==null){
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
                return evaluate(binary.left, vars, facts)
                        && evaluate(binary.right, vars, facts);
            }
        }

        return false;
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
}