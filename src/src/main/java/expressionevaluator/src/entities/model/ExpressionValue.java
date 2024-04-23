package expressionevaluator.src.entities.model;

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
