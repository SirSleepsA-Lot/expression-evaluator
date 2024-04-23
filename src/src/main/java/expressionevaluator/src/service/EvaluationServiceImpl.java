package expressionevaluator.src.service;

import expressionevaluator.src.entities.model.Operator;
import expressionevaluator.src.service.interfaces.EvaluationService;
import org.springframework.stereotype.Service;

@Service
public class EvaluationServiceImpl implements EvaluationService {
    public <T, U> Object resolve(T operand1, U operand2, Operator operator) throws Exception {
        return switch (operator.getValue()) {
            case "||" -> {
                if (operand1 instanceof Boolean && operand2 instanceof Boolean) {
                    yield or((Boolean) operand1, (Boolean) operand2);
                }
                throw new Exception("operands for or action should be boolean");
            }
            case "&&" -> {
                if (operand1 instanceof Boolean && operand2 instanceof Boolean) {
                    yield and((Boolean) operand1, (Boolean) operand2);
                }
                throw new Exception("operands for and action should be boolean");
            }
            case "==" -> equals(operand1, operand2);
            case "!=" -> notEquals(operand1, operand2);
            case "<" -> lesserThan(operand1, operand2);
            case ">" -> greaterThan(operand1, operand2);
            case "<=" -> lesserThanOrEquals(operand1, operand2);
            case ">=" -> greaterThanOrEquals(operand1, operand2);
            case "+" -> {
                if (operand1 instanceof Number && operand2 instanceof Number) {
                    yield add((Number) operand1, (Number) operand2);
                }
                throw new Exception("operands for mod action should be numbers");
            }
            case "-" -> {
                if (operand1 instanceof Number && operand2 instanceof Number) {
                    yield sub((Number) operand1, (Number) operand2);
                }
                throw new Exception("operands for mod action should be numbers");
            }
            case "*" -> {
                if (operand1 instanceof Number && operand2 instanceof Number) {
                    yield mul((Number) operand1, (Number) operand2);
                }
                throw new Exception("operands for mod action should be numbers");
            }
            case "/" -> {
                if (operand1 instanceof Number && operand2 instanceof Number) {
                    if (((Number) operand2).floatValue() == 0)
                        throw new Exception("2nd operand cannot be 0 when dividing");
                    yield div((Number) operand1, (Number) operand2);
                }
                throw new Exception("operands for div action should be numbers");
            }
            case "%" -> {
                if (operand1 instanceof Number && operand2 instanceof Number) {
                    yield mod((Number) operand1, (Number) operand2);
                }
                throw new Exception("operands for mod action should be numbers");
            }
            default -> false;
        };
    }

    private <T extends Number, U extends Number> Number mod(T operand1, U operand2){
        return operand1.floatValue() % operand2.floatValue();
    }
    private <T extends Number, U extends Number> Number div(T operand1, U operand2){
        return operand1.floatValue() / operand2.floatValue();
    }
    private <T extends Number, U extends Number> Number mul(T operand1, U operand2){
        return operand1.floatValue() * operand2.floatValue();
    }
    private <T extends Number, U extends Number> Number add(T operand1, U operand2){
        return operand1.floatValue() + operand2.floatValue();
    }
    private <T extends Number, U extends Number> Number sub(T operand1, U operand2){
        return operand1.floatValue() - operand2.floatValue();
    }

    private <T, U> boolean equals(T operand1, U operand2){
        if(operand1 == null && operand2 == null) return true;
        assert operand1 != null;
        if(operand1.getClass().equals(operand2.getClass())|| (operand2 instanceof Number && operand1 instanceof Number)){
            return operand1.equals(operand2);
        }
        return false;
    }
    private <T, U> boolean notEquals(T operand1, U operand2){
       return !equals(operand1, operand2);
    }
    private <T, U> boolean greaterThan(T operand1, U operand2){
        if(operand1 == null && operand2 == null) return false;
        assert operand1 != null;
        if((operand2 instanceof Number && operand1 instanceof Number)){
            return ((Number) operand1).floatValue() > ((Number) operand2).floatValue();
        }
        else if((operand2 instanceof Character && operand1 instanceof Character)){
            return ((Character) operand1) > ((Character) operand2);
        }
        return false;
    }
    private <T, U> boolean lesserThanOrEquals(T operand1, U operand2){
        if(operand1 == null && operand2 == null) return false;
        return !greaterThan(operand1, operand2);
    }
    private <T, U> boolean lesserThan(T operand1, U operand2){
        if(operand1 == null && operand2 == null) return false;
        assert operand1 != null;
        if((operand2 instanceof Number && operand1 instanceof Number)){
            return ((Number) operand1).floatValue() < ((Number) operand2).floatValue();
        }
        else if((operand2 instanceof Character && operand1 instanceof Character)){
            return ((Character) operand1) < ((Character) operand2);
        }
        return false;
    }
    private <T, U> boolean greaterThanOrEquals(T operand1, U operand2){
        if(operand1 == null && operand2 == null) return false;
        return !greaterThan(operand1, operand2);
    }

    private boolean and(boolean operand1, boolean operand2){
        return operand1 && operand2;
    }
    private boolean or(boolean operand1, boolean operand2){
        return operand1 || operand2;
    }
}
