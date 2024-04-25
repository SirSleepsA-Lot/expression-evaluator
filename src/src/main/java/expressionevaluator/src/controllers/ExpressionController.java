package expressionevaluator.src.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import expressionevaluator.src.entities.model.Expression;
import expressionevaluator.src.entities.helperEntities.TreeNode;
import expressionevaluator.src.service.ExpressionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("")
public class ExpressionController {

    @Autowired
    private ExpressionServiceImpl expressionService;

    @PostMapping("/expression")
    public ResponseEntity<Expression> createExpression(@RequestBody Expression expression) {
        Expression expressionNew = expressionService.saveExpression(expression);
        return ResponseEntity.ok(expressionNew);
    }
    @PostMapping("/visualize")
    public ResponseEntity<String> printExpressionTree(@RequestBody String Expression) {
        String ret = expressionService.visualize(Expression);
        return ResponseEntity.ok(ret);
    }
    @PostMapping("/evaluate/{id}")
    public ResponseEntity<?> printExpressionTree(@PathVariable(value = "id") Long expressionId, @RequestBody Map<String, Object> requestBody) throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(requestBody);
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        Boolean evaluation = expressionService.evaluateExpression(expressionId, jsonObject);
        return ResponseEntity.ok(evaluation);
    }

}
