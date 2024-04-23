package expressionevaluator.src.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import expressionevaluator.src.entities.model.Expression;
import expressionevaluator.src.entities.model.Operator;
import expressionevaluator.src.entities.model.Variable;
import expressionevaluator.src.repository.ExpressionRepository;
import expressionevaluator.src.service.interfaces.EvaluationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
@Service
public class ExpressionService {
    private static final Map<String, Integer> precedenceMap;
    private final ExpressionRepository expressionRepository;
    private final EvaluationService evaluationService;
    static {
        precedenceMap = new HashMap<>();
        precedenceMap.put("||", 1);  // Logical OR has lowest precedence
        precedenceMap.put("&&", 2);  // Logical AND has higher precedence
        precedenceMap.put("==", 3);  // Equality operators have higher precedence than logical AND
        precedenceMap.put("!=", 3);
        precedenceMap.put("<", 4);   // Comparison operators have higher precedence than equality operators
        precedenceMap.put(">", 4);
        precedenceMap.put("<=", 4);
        precedenceMap.put(">=", 4);
        precedenceMap.put("+", 5);   // Addition and subtraction
        precedenceMap.put("-", 5);
        precedenceMap.put("*", 6);   // Multiplication, division, and modulus
        precedenceMap.put("/", 6);
        precedenceMap.put("%", 6);
    }

    public ExpressionService(ExpressionRepository expressionRepository, EvaluationService evaluationService) {
        this.expressionRepository = expressionRepository;
        this.evaluationService = evaluationService;
    }
    public Expression saveExpression(Expression expression){
        expressionRepository.save(expression);
        return expression;
    }
    public String readVariable(String expression, Integer i){
        StringBuilder variableBuilder = new StringBuilder();
        while (i < expression.length() && !operatorStart(expression.charAt(i)) && expression.charAt(i) != '(' && expression.charAt(i) != ')') {
            variableBuilder.append(expression.charAt(i));
            i++;
        }
        i--; // Move back one step, as the loop will increment it again
        return variableBuilder.toString();

    }
    public Stack<Object> infixToPostfix(String expression) {
        expression = expression.replaceAll("\\s", "");
        Stack<Operator> operatorStack = new Stack<>();
        Stack<Object> postfixStack = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == ' ') {
                continue; // Skip whitespaces
            }

            if (Character.isLetter(ch) || ch == '_') {
                // If it's a variable or operand
                String readVariable = readVariable( expression, i);
                i += readVariable.length() - 1;
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
                    default:
                        Variable variableOrOperand = new Variable(readVariable);
                        postfixStack.push(variableOrOperand);
                }

            } else if (Character.isDigit(ch)){
                String number = readVariable( expression, i);
                i += number.length() - 1;
                float castedNumber = Float.parseFloat(number);
                postfixStack.push(castedNumber);
            }else if (ch == '\"'){
                String stringValue = readVariable( expression, i);
                i += stringValue.length() - 1;

                postfixStack.push(stringValue.substring(1, stringValue.length() - 1));
            }else if (ch == '(') {
                operatorStack.push(new Operator(ch + "")); // Push opening parenthesis to the stack
            } else if (ch == ')') {
                // Pop operators and push to postfix stack until matching '('
                while (!operatorStack.isEmpty() && !operatorStack.peek().getValue().equals("(")) {
                    Operator operator = operatorStack.pop();
                    postfixStack.push(operator);
                }
                if (!operatorStack.isEmpty() && operatorStack.peek().getValue().equals("(")) {
                    operatorStack.pop(); // Remove '(' from stack
                }
            } else {
                // If it's an operator
                StringBuilder operatorBuilder = new StringBuilder();
                while (i < expression.length() && !Character.isLetterOrDigit(expression.charAt(i)) && expression.charAt(i) != ' ' && expression.charAt(i) != '(' && expression.charAt(i) != ')' && expression.charAt(i) != '\"') {
                    operatorBuilder.append(expression.charAt(i));
                    i++;
                }
                i--; // Move back one step, as the loop will increment it again
                Operator operator = new Operator(operatorBuilder.toString());
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek().getValue()) >= precedence(operator.getValue())) {
                    Operator oper = operatorStack.pop();
                    if(oper.getValue().equals("(") || oper.getValue().equals(")")) continue;
                    postfixStack.push(oper);
                }
                operatorStack.push(operator); // Push current operator to stack
            }
        }

        // Pop remaining operators and push to postfix stack
        while (!operatorStack.isEmpty()) {
            Operator oper = operatorStack.pop();
            if(oper.getValue().equals("(") || oper.getValue().equals(")")) continue;
            postfixStack.push(oper);
        }

        return postfixStack;
    }

    private int precedence(String operator) {
        return precedenceMap.getOrDefault(operator, -1); // Get precedence from the map
    }
    private boolean operatorStart(Character ch){
        return precedence(ch + "") != -1 || precedence(ch + "&") != -1 || precedence(ch + "|") != -1 || precedence(ch + "=") != -1;
    }
    public String getStackContents(Stack<Object> stack) {
        StringBuilder result = new StringBuilder();
        for (Object obj : stack) {
            result.append(obj).append(" ");
        }
        return result.toString();
    }

    private Object getVariableValue(Variable variable, JsonObject variables) throws Exception {
        // Split the variable by '.' to handle nested keys
        String[] keys = variable.getValue().split("\\.");
        JsonObject currentObj = variables;
        for (String key : keys) {
            JsonElement element = currentObj.get(key);
            if (element != null && !element.isJsonNull()) {
                if (element.isJsonObject()) {
                    currentObj = element.getAsJsonObject();
                } else if (element.isJsonPrimitive()) {
                    if(element.getAsJsonPrimitive().isBoolean()) return element.getAsBoolean();
                    else if(element.getAsJsonPrimitive().isNumber()) return element.getAsFloat();
                    else if(element.getAsJsonPrimitive().isString()) return element.getAsString();

                }
            } else {
                // Key not found or value is null, return false
                throw new Exception(String.format("Variable %s not found in JSON object", variable));
            }
        }
        return false;
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

}
