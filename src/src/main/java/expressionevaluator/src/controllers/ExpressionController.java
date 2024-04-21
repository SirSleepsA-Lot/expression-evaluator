package expressionevaluator.src.controllers;

import expressionevaluator.src.model.Node;
import expressionevaluator.src.service.ExpressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Stack;

@RestController
@RequestMapping("/expression")
public class ExpressionController {

    @Autowired
    private ExpressionService expressionService;

    @PutMapping("/expression")
    public ResponseEntity<String> printExpressionTree(@RequestBody String Expression) {
        Stack<Object> node = expressionService.infixToPostfix(Expression);
        return ResponseEntity.ok(expressionService.getStackContents(node));
    }
}
