package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 일반 표현식을 SELECT 항목으로 래핑하는 클래스.
 * 함수 호출, 리터럴 등 컬럼이 아닌 표현식을 SELECT 절에서 사용할 때 사용됩니다.
 */
public final class ExpressionSelectItem implements SelectItem {

    private final Expression expression;

    @Nullable
    private final String alias;
    private final SourceLocation location;

    public ExpressionSelectItem(
            final Expression expression,
            @Nullable final String alias,
            final SourceLocation location
    ) {
        this.expression = expression;
        this.alias = alias;
        this.location = location;
    }

    @Override
    public Optional<String> alias() {
        return Optional.ofNullable(alias);
    }

    @Override
    public SelectItemType selectItemType() {
        return SelectItemType.EXPRESSION;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitExpressionSelectItem(this);
    }

    @Override
    public SourceLocation location() {
        return location;
    }

    public Expression expression() {
        return expression;
    }
}
