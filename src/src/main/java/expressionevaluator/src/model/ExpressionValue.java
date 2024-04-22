package expressionevaluator.src.model;

import java.util.Objects;

public abstract class ExpressionValue {
    private String value;
    public ExpressionValue(String operator) {
        this.value = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }





    @Override
    public String toString(){
        return value;
    }
}
