package expressionevaluator.src.service.interfaces;

import com.google.gson.JsonObject;
import expressionevaluator.src.entities.helperEntities.TreeNode;
import expressionevaluator.src.entities.model.Expression;

public interface ExpressionService {
    Expression saveExpression(Expression expression);
    String visualize(String expression);
    boolean evaluateExpression(Long expressionId, JsonObject inputVariables) throws Exception;
}
