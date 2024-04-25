package expressionevaluator.src.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import expressionevaluator.src.entities.model.Expression;
import expressionevaluator.src.entities.helperEntities.Operator;
import expressionevaluator.src.entities.helperEntities.TreeNode;
import expressionevaluator.src.entities.helperEntities.Variable;
import expressionevaluator.src.repository.ExpressionRepository;
import expressionevaluator.src.service.interfaces.EvaluationService;
import expressionevaluator.src.service.interfaces.ExpressionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;

@Service
public class ExpressionServiceImpl implements ExpressionService {
    private static final Map<String, Integer> precedenceMap;
    private final ExpressionRepository expressionRepository;
    private final EvaluationService evaluationService;
    static {
        precedenceMap = new HashMap<>();
        precedenceMap.put("||", 1);
        precedenceMap.put("&&", 2);
        precedenceMap.put("!", 7);
        precedenceMap.put("==", 3);
        precedenceMap.put("!=", 3);
        precedenceMap.put("<", 4);
        precedenceMap.put(">", 4);
        precedenceMap.put("<=", 4);
        precedenceMap.put(">=", 4);
        precedenceMap.put("+", 5);
        precedenceMap.put("-", 5);
        precedenceMap.put("*", 6);
        precedenceMap.put("/", 6);
        precedenceMap.put("%", 6);
    }

    public ExpressionServiceImpl(ExpressionRepository expressionRepository, EvaluationService evaluationService) {
        this.expressionRepository = expressionRepository;
        this.evaluationService = evaluationService;
    }
    public Expression saveExpression(Expression expression){
        expressionRepository.save(expression);
        return expression;
    }
    private String readVariable(String expression, Integer i, Predicate<Character> condition){
        StringBuilder variableBuilder = new StringBuilder();
        while (i < expression.length() && condition.test(expression.charAt(i))) {
            variableBuilder.append(expression.charAt(i));
            i++;
        }
        i--;
        return variableBuilder.toString();

    }
    private void pushOperatorToStack(Operator operator, Stack<Operator> operatorStack, Stack<Object> postfixStack){
        while (!operatorStack.isEmpty() && precedence(operatorStack.peek().getValue()) >= precedence(operator.getValue())) {
            Operator oper = operatorStack.pop();
            if(oper.getValue().equals("(") || oper.getValue().equals(")")) continue;
            postfixStack.push(oper);
        }
        operatorStack.push(operator);
    }
    private Stack<Object> infixToPostfix(String expression) {
        Predicate<Character> variableCondition = ch -> !operatorStart(ch) && ch != '(' && ch != ')' && ch != ' ';
        Predicate<Character> operatorCondition = ch -> !Character.isLetterOrDigit(ch) && ch != ' ' && ch != '(' && ch != ')' && ch != '\"';


        Stack<Operator> operatorStack = new Stack<>();
        Stack<Object> postfixStack = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == ' ') {
                continue;
            }

            if (Character.isLetter(ch) || ch == '_') {
                String readVariable = readVariable( expression, i, variableCondition);
                i += readVariable.length() - 1;
                Operator operator = null;
                switch (readVariable) {
                    case "null":
                        postfixStack.push(null);
                        break;
                    case "true":
                        postfixStack.push(true);
                        break;
                    case "false":
                        postfixStack.push(false);
                        break;
                    case "OR":
                        operator = new Operator("||");
                        pushOperatorToStack(operator, operatorStack, postfixStack);
                        break;
                    case "AND":
                        operator = new Operator("&&");
                        pushOperatorToStack(operator, operatorStack, postfixStack);
                        break;
                    default:
                        Variable variableOrOperand = new Variable(readVariable);
                        postfixStack.push(variableOrOperand);
                }

            } else if (Character.isDigit(ch)){
                String number = readVariable( expression, i, variableCondition);
                i += number.length() - 1;
                float castedNumber = Float.parseFloat(number);
                postfixStack.push(castedNumber);
            }else if (ch == '\"'){
                String stringValue = readVariable( expression, i, variableCondition);
                i += stringValue.length() - 1;

                postfixStack.push(stringValue.substring(1, stringValue.length() - 1));
            }else if (ch == '\''){
                String stringValue = readVariable( expression, i, variableCondition);
                i += stringValue.length() - 1;

                postfixStack.push(stringValue.replaceAll("'", ""));
            }else if (ch == '(') {
                operatorStack.push(new Operator(ch + "")); // Push opening parenthesis to the stack
            } else if (ch == ')') {
                while (!operatorStack.isEmpty() && !operatorStack.peek().getValue().equals("(")) {
                    Operator operator = operatorStack.pop();
                    postfixStack.push(operator);
                }
                if (!operatorStack.isEmpty() && operatorStack.peek().getValue().equals("(")) {
                    operatorStack.pop();
                }
            }else {
                String operatorValue = readVariable(expression, i, operatorCondition);
                i += operatorValue.length() - 1;
                Operator operator = new Operator(operatorValue);
                pushOperatorToStack(operator, operatorStack, postfixStack);
            }
        }

        while (!operatorStack.isEmpty()) {
            Operator oper = operatorStack.pop();
            if(oper.getValue().equals("(") || oper.getValue().equals(")")) continue;
            postfixStack.push(oper);
        }

        return postfixStack;
    }
    private static Stack<Object> flipStack(Stack<Object> stack) {
        Stack<Object> tempStack = new Stack<>();
        while (!stack.isEmpty()) {
            tempStack.push(stack.pop());
        }

        return tempStack;
    }
    private TreeNode buildExpressionTree(String expression) {
        Stack<Object> postfixExpression = infixToPostfix(expression);
        postfixExpression = flipStack(postfixExpression);
        Stack<TreeNode> stack = new Stack<>();
        while (!postfixExpression.isEmpty()) {
            Object token = postfixExpression.pop();
            if (token instanceof Operator) {
                if(((Operator) token).getValue().equals("!")){
                    TreeNode left = stack.pop();
                    TreeNode operatorNode = new TreeNode(token);
                    operatorNode.setLeft(left);
                    stack.push(operatorNode);
                }
                else{
                    TreeNode right = stack.pop();
                    TreeNode left = stack.pop();
                    TreeNode operatorNode = new TreeNode(token);
                    operatorNode.setLeft(left);
                    operatorNode.setRight(right);
                    stack.push(operatorNode);
                }

            } else {
                stack.push(new TreeNode(token));
            }

        }
        return stack.pop();
    }
    public String visualize(String expression) {
        TreeNode root = buildExpressionTree(expression);
        StringBuilder sb = new StringBuilder();
        visualizeHelper(root, "", true, sb);
        return sb.toString();
    }

    private void visualizeHelper(TreeNode node, String prefix, boolean isTail, StringBuilder sb) {
        if (node != null) {
            sb.append(prefix).append(isTail ? "└── " : "├── ").append(node.getValue()).append("\n");
            visualizeHelper(node.getLeft(), prefix + (isTail ? "    " : "│   "), false, sb);
            visualizeHelper(node.getRight(), prefix + (isTail ? "    " : "│   "), true, sb);
        }
    }
    private int precedence(String operator) {
        return precedenceMap.getOrDefault(operator, -1);
    }
    private boolean operatorStart(Character ch){
        return precedence(ch + "") != -1 || precedence(ch + "&") != -1 || precedence(ch + "|") != -1 || precedence(ch + "=") != -1;
    }
    private Object getVariableValue(Variable variable, JsonObject variables) throws Exception {
        String[] keys = variable.getValue().split("\\.");
        JsonObject currentObj = variables;
        JsonElement element = null;
        for (String key : keys) {
            element = currentObj.get(key);
            if (element != null && !element.isJsonNull()) {
                if (element.isJsonObject()) {
                    currentObj = element.getAsJsonObject();
                } else if (element.isJsonPrimitive()) {
                    if(element.getAsJsonPrimitive().isBoolean()) return element.getAsBoolean();
                    else if(element.getAsJsonPrimitive().isNumber()) return element.getAsFloat();
                    else if(element.getAsJsonPrimitive().isString()) return element.getAsString();

                }
            } else if(element != null && element.isJsonNull()){
                return null;
            } else {
                throw new Exception(String.format("Variable %s not found in JSON object", variable));
            }
        }
        return element;
    }

    private boolean evaluateExpressionDecrapated(Long expressionId, JsonObject inputVariables) throws Exception {

        var expressionEntity = expressionRepository.findById(expressionId).orElse(null);
        String expression;
        if(expressionEntity != null){
            expression = expressionEntity.getExpression();
        }
        else{
            throw new Exception("expression not found");
        }
        Stack<Object> postfixStack = infixToPostfix(expression);
        Stack<Object> helperStack = new Stack<>();
        while(!postfixStack.isEmpty()){
            Object element = postfixStack.pop();
            if(element instanceof Variable){
                element = getVariableValue((Variable) element, inputVariables);
            }
            if(element instanceof Operator) {
                helperStack.push(element);
            }
            else{
                Object element2 = null;
                if(!(helperStack.peek() instanceof Operator)) {
                    element2 = helperStack.pop();
                }
                else if(postfixStack.peek() instanceof Operator) {
                    helperStack.push(element);
                    continue;
                }
                else{
                    element2 = postfixStack.pop();
                    if(element2 instanceof Variable){
                        element2 = getVariableValue((Variable) element2, inputVariables);
                    }
                }
                if(helperStack.isEmpty() || !(helperStack.peek() instanceof Operator)) throw new RuntimeException("Logical expression is invalid");
                Operator operator = (Operator) helperStack.pop();
                Object result = evaluationService.resolve(element, element2, operator);
                postfixStack.push(result);
            }
        }
        return false;
    }
    private Object evaluateExpressionTree(TreeNode root, JsonObject inputVariables) throws Exception {


        var rootValue = root.getValue();
        if(rootValue instanceof Operator){
            var leftChild = root.getLeft().getValue();

            if(leftChild instanceof Operator){
                leftChild = evaluateExpressionTree(root.getLeft(), inputVariables);

            }
            else if(leftChild instanceof Variable){
                leftChild = getVariableValue((Variable) leftChild, inputVariables);
            }

            if(((Operator) rootValue).getValue().equals("&&") && !((Boolean) leftChild)) return false;
            else if(((Operator) rootValue).getValue().equals("||") && ((Boolean) leftChild)) return true;
            else if(((Operator) rootValue).getValue().equals("!")) return !((Boolean) leftChild) ;

            var rightChild = root.getRight().getValue();

            if(rightChild instanceof Operator){
                rightChild = evaluateExpressionTree(root.getRight(), inputVariables);

            }
            else if(rightChild instanceof Variable){
                rightChild = getVariableValue((Variable) rightChild, inputVariables);
            }
            return evaluationService.resolve(leftChild, rightChild, (Operator) rootValue);

        }
        else if(rootValue instanceof Variable){
            return getVariableValue((Variable) rootValue, inputVariables);
        }
        return rootValue;
    }
    public boolean evaluateExpression(Long expressionId, JsonObject inputVariables) throws Exception {
        var expressionEntity = expressionRepository.findById(expressionId).orElse(null);
        String expression;
        if(expressionEntity != null){
            expression = expressionEntity.getExpression();
        }
        else{
            throw new Exception("expression not found");
        }
        TreeNode root = buildExpressionTree(expression);
        return (boolean) evaluateExpressionTree(root, inputVariables);
    }
}
