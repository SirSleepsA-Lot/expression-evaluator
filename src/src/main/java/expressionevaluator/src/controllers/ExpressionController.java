package expressionevaluator.src.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import expressionevaluator.src.entities.model.Expression;
import expressionevaluator.src.service.ExpressionService;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Stack;

@RestController
@RequestMapping("")
public class ExpressionController {

    @Autowired
    private ExpressionService expressionService;

    @PostMapping("/evaluate")
    public ResponseEntity<Expression> createExpression(@RequestBody Expression expression) {
        Expression expressionNew = expressionService.saveExpression(expression);
        return ResponseEntity.ok(expressionNew);
    }
    @PostMapping("/expression")
    public ResponseEntity<String> printExpressionTree(@RequestBody String Expression) {
        Stack<Object> node = expressionService.infixToPostfix(Expression);
        return ResponseEntity.ok(expressionService.getStackContents(node));
    }
    @PostMapping("/expression/{id}")
    public ResponseEntity<Boolean> printExpressionTree(@PathVariable(value = "id") Long expressionId, @RequestBody Map<String, Object> requestBody) throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(requestBody);
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        Boolean evaluation = expressionService.evaluateExpression(expressionId, jsonObject);
        return ResponseEntity.ok(true);
    }

}
