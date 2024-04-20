package expressionevaluator.src.service;

import expressionevaluator.src.model.Node;
import org.springframework.stereotype.Service;

import java.util.Stack;
@Service
public class ExpressionService {
    public Node generateExpressionTree(String expression) {
        expression = expression.replaceAll("\\s", ""); // Remove all white spaces
        Stack<Node> stack = new Stack<>();

        for (char ch : expression.toCharArray()) {
            if (ch == '(') {
                continue;
            } else if (ch == ')') {
                Node right = stack.pop();
                Node operator = stack.pop();
                Node left = stack.pop();

                operator.setLeftNode(left);
                operator.setRightNode(right);
                stack.push(operator);
            } else if (isOperator(ch)) {
                stack.push(new Node(String.valueOf(ch)));
            } else {
                // If it's an operand, push it as a Node
                stack.push(new Node(String.valueOf(ch)));
            }
        }

        // At the end, there should be only one element in the stack, which is the root
        if (!stack.isEmpty()) {
            return stack.pop();
        }

        // If somehow the stack is empty, return null
        return null;
    }

    private boolean isOperator(char ch) {
        return ch == '&' || ch == '|' || ch == '!';
    }
    public String printTreeToString(Node node) {
        StringBuilder sb = new StringBuilder();

        printTreeToStringHelper(node, sb);

        return sb.toString();
    }

    private void printTreeToStringHelper(Node node, StringBuilder sb) {
        if (node == null) {
            return;
        }

        // Append current node's operand to the StringBuilder
        sb.append(node.getOperand()).append(" ");

        // If the node has left or right nodes, add parentheses
        if (node.getLeftNode() != null || node.getRightNode() != null) {
            sb.append("( ");
        }

        // Recur for left child
        printTreeToStringHelper(node.getLeftNode(), sb);

        // Add operator if it's an internal node
        if (node.getLeftNode() != null || node.getRightNode() != null) {
            sb.append(node.getOperand()).append(" ");
        }

        // Recur for right child
        printTreeToStringHelper(node.getRightNode(), sb);

        // If the node has left or right nodes, add closing parentheses
        if (node.getLeftNode() != null || node.getRightNode() != null) {
            sb.append(") ");
        }
    }
}
