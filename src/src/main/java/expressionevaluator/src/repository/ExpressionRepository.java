package expressionevaluator.src.repository;

import expressionevaluator.src.entities.model.Expression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpressionRepository extends JpaRepository<Expression, Long> {
}
