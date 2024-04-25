package expressionevaluator.src;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import expressionevaluator.src.entities.helperEntities.Operator;
import expressionevaluator.src.entities.model.Expression;
import expressionevaluator.src.repository.ExpressionRepository;
import expressionevaluator.src.service.EvaluationServiceImpl;
import expressionevaluator.src.service.ExpressionServiceImpl;
import expressionevaluator.src.service.interfaces.EvaluationService;
import expressionevaluator.src.service.interfaces.ExpressionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ExpressionServiceTest {
    @Test
    void testSaveExpression() {
        ExpressionRepository expressionRepository = Mockito.mock(ExpressionRepository.class);
        EvaluationService evaluationService = Mockito.mock(EvaluationService.class);
        ExpressionService expressionService = new ExpressionServiceImpl(expressionRepository, evaluationService);
        Expression expression = new Expression("x && y");

        when(expressionRepository.save(expression)).thenReturn(expression);

        assertEquals(expression, expressionService.saveExpression(expression));
    }

    @Test
    void testEvaluateExpressionFromTaskCase() throws Exception {
        ExpressionRepository expressionRepositoryMock = Mockito.mock(ExpressionRepository.class);


        EvaluationService evaluationService = new EvaluationServiceImpl();
        ExpressionService expressionService = new ExpressionServiceImpl(expressionRepositoryMock, evaluationService);
        Expression expression = new Expression("(customer.firstName == \"JHON\" && customer.salary < 100) OR (customer.address != null && customer.address.city == \"Washington\")");
        String jsonString = "{\n" +
                "  \"customer\":\n" +
                "  {\n" +
                "    \"firstName\": \"JHON\",\n" +
                "    \"lastName\": \"DOE\", \n" +
                "    \"address\":\n" +
                "    {\n" +
                "      \"city\": \"Chicago\",\n" +
                "      \"zipCode\": 1234, \n" +
                "      \"street\": \"56th\", \n" +
                "      \"houseNumber\": 2345\n" +
                "    },\n" +
                "    \"salary\": 99,\n" +
                "    \"type\": \"BUSINESS\"\n" +
                "  }\n" +
                "}";

        // Parse the JSON string to create a JsonObject
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();



        when(expressionRepositoryMock.findById(1L)).thenReturn(java.util.Optional.of(expression));
        assertTrue(expressionService.evaluateExpression(1L, jsonObject), "Failed to evaluate expression");
    }
    @Test
    void testEvaluateExpressionWithNull() throws Exception {
        ExpressionRepository expressionRepositoryMock = Mockito.mock(ExpressionRepository.class);


        EvaluationService evaluationService = new EvaluationServiceImpl();
        ExpressionService expressionService = new ExpressionServiceImpl(expressionRepositoryMock, evaluationService);
        Expression expression = new Expression("customer.address == null ");
        String jsonString = "{\n" +
                "  \"customer\": {\n" +
                "    \"firstName\": \"JHON\",\n" +
                "    \"lastName\": \"DOE\",\n" +
                "    \"address\": null, // This key has a null value\n" +
                "    \"salary\": 99,\n" +
                "    \"type\": \"BUSINESS\"\n" +
                "  }\n" +
                "}";

        // Parse the JSON string to create a JsonObject
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();



        when(expressionRepositoryMock.findById(1L)).thenReturn(java.util.Optional.of(expression));
        assertTrue(expressionService.evaluateExpression(1L, jsonObject), "Failed to evaluate expression");
    }
    @Test
    void testEvaluateExpressionExpectFalse() throws Exception {
        ExpressionRepository expressionRepositoryMock = Mockito.mock(ExpressionRepository.class);


        EvaluationService evaluationService = new EvaluationServiceImpl();
        ExpressionService expressionService = new ExpressionServiceImpl(expressionRepositoryMock, evaluationService);
        Expression expression = new Expression("customer.lastName == \"DOE\" AND !true");
        String jsonString = "{\n" +
                "  \"customer\": {\n" +
                "    \"firstName\": \"JHON\",\n" +
                "    \"lastName\": \"DOE\",\n" +
                "    \"address\": null, // This key has a null value\n" +
                "    \"salary\": 99,\n" +
                "    \"type\": \"BUSINESS\"\n" +
                "  }\n" +
                "}";

        // Parse the JSON string to create a JsonObject
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();



        when(expressionRepositoryMock.findById(1L)).thenReturn(java.util.Optional.of(expression));
        assertFalse(expressionService.evaluateExpression(1L, jsonObject), "Failed to evaluate expression");
    }
    @Test
    void testEvaluateExpressionTryMath() throws Exception {
        ExpressionRepository expressionRepositoryMock = Mockito.mock(ExpressionRepository.class);


        EvaluationService evaluationService = new EvaluationServiceImpl();
        ExpressionService expressionService = new ExpressionServiceImpl(expressionRepositoryMock, evaluationService);
        Expression expression = new Expression("2+customer.salary >= 100");
        String jsonString = "{\n" +
                "  \"customer\": {\n" +
                "    \"firstName\": \"JHON\",\n" +
                "    \"lastName\": \"DOE\",\n" +
                "    \"address\": null, // This key has a null value\n" +
                "    \"salary\": 99,\n" +
                "    \"type\": \"BUSINESS\"\n" +
                "  }\n" +
                "}";

        // Parse the JSON string to create a JsonObject
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();



        when(expressionRepositoryMock.findById(1L)).thenReturn(java.util.Optional.of(expression));
        assertFalse(expressionService.evaluateExpression(1L, jsonObject), "Failed to evaluate expression");
    }
    @Test
    void testEvaluateExpressionTryCharacters() throws Exception {
        ExpressionRepository expressionRepositoryMock = Mockito.mock(ExpressionRepository.class);


        EvaluationService evaluationService = new EvaluationServiceImpl();
        ExpressionService expressionService = new ExpressionServiceImpl(expressionRepositoryMock, evaluationService);
        Expression expression = new Expression("character.firstCharacter == 'a'");
        String jsonString = "{\n" +
                "  \"character\": {\n" +
                "    \"firstCharacter\": 'a',\n" +
                "    \"lastName\": \"DOE\",\n" +
                "    \"address\": null, // This key has a null value\n" +
                "    \"salary\": 99,\n" +
                "    \"type\": \"BUSINESS\"\n" +
                "  }\n" +
                "}";

        // Parse the JSON string to create a JsonObject
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();



        when(expressionRepositoryMock.findById(1L)).thenReturn(java.util.Optional.of(expression));
        assertTrue(expressionService.evaluateExpression(1L, jsonObject), "Failed to evaluate expression");
    }
    @Test
    void testEvaluateExpressionTryIncorrectSytax() throws Exception {
        ExpressionRepository expressionRepositoryMock = Mockito.mock(ExpressionRepository.class);
        EvaluationService evaluationService = new EvaluationServiceImpl();
        ExpressionService expressionService = new ExpressionServiceImpl(expressionRepositoryMock, evaluationService);
        Expression expression = new Expression("character.firstCharacter == 'a' ||");
        String jsonString = "{\n" +
                "  \"character\": {\n" +
                "    \"firstCharacter\": 'a',\n" +
                "    \"lastName\": \"DOE\",\n" +
                "    \"address\": null, // This key has a null value\n" +
                "    \"salary\": 99,\n" +
                "    \"type\": \"BUSINESS\"\n" +
                "  }\n" +
                "}";

        // Parse the JSON string to create a JsonObject
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();



        when(expressionRepositoryMock.findById(1L)).thenReturn(java.util.Optional.of(expression));
        assertThrows(Exception.class, () -> {
            expressionService.evaluateExpression(1L, jsonObject);
        });
    }

}
