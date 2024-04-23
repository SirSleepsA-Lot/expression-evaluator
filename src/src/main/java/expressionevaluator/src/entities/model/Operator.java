package expressionevaluator.src.entities.model;

import java.util.Objects;

public class Operator extends ExpressionValue {
    public Operator(String value) {
        super(value);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof String that)) return false;
        return this.getValue().equals(that);
    }
    @Override
    public int hashCode() {
        return Objects.hash(this.getValue());
    }
}
