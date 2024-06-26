package expressionevaluator.src.entities.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
@Entity
public class Expression {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String expression;
    public Expression() {

    }
    public Expression(String expression) {
        this.expression = expression;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }


}
