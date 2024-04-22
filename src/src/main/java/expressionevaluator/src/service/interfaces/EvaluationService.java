package expressionevaluator.src.service.interfaces;

import expressionevaluator.src.model.Operator;

public interface EvaluationService {
    public <T, U> Object resolve(T operand1, U operand2, Operator operator) throws Exception;
}
