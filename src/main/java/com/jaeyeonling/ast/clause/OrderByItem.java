package com.jaeyeonling.ast.clause;

import com.jaeyeonling.ast.expression.Expression;

/**
 * ORDER BY 항목
 */
public record OrderByItem(
        Expression expression,
        OrderDirection direction
) {
}
