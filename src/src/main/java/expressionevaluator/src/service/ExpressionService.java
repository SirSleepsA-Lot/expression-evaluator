package expressionevaluator.src.service;

import expressionevaluator.src.model.Node;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
@Service
public class ExpressionService {
    private static final Map<String, Integer> precedenceMap;

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

    public Stack<Object> infixToPostfix(String expression) {
        expression = expression.replaceAll("\\s", "");
        Stack<String> operatorStack = new Stack<>();
        Stack<Object> postfixStack = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == ' ') {
                continue; // Skip whitespaces
            }

            if (Character.isLetterOrDigit(ch) || ch == '\"') {
                // If it's a variable or operand
                StringBuilder variableBuilder = new StringBuilder();
                while (i < expression.length() && !operatorStart(expression.charAt(i))) {
                    variableBuilder.append(expression.charAt(i));
                    i++;
                }
                i--; // Move back one step, as the loop will increment it again
                String variableOrOperand = variableBuilder.toString();
                postfixStack.push(variableOrOperand);
            } else if (ch == '(') {
                operatorStack.push(ch + ""); // Push opening parenthesis to the stack
            } else if (ch == ')') {
                // Pop operators and push to postfix stack until matching '('
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    String operator = operatorStack.pop();
                    postfixStack.push(operator);
                }
                if (!operatorStack.isEmpty() && operatorStack.peek().equals("(")) {
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
                String operator = operatorBuilder.toString();
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(operator)) {
                    postfixStack.push(operatorStack.pop());
                }
                operatorStack.push(operator); // Push current operator to stack
            }
        }

        // Pop remaining operators and push to postfix stack
        while (!operatorStack.isEmpty()) {
            postfixStack.push(operatorStack.pop());
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
    
}
