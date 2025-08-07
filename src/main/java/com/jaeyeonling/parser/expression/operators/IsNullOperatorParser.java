package com.jaeyeonling.parser.expression.operators;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.IsNullExpression;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;
import com.jaeyeonling.parser.expression.OperatorParser;
import com.jaeyeonling.parser.expression.OperatorPriority;

/**
 * IS NULL 연산자 파싱을 담당합니다.
 * 예: email IS NULL
 */
public final class IsNullOperatorParser implements OperatorParser {

    @Override
    public boolean canParse(final TokenStream tokenStream) {
        if (!tokenStream.check(TokenType.IS)) {
            return false;
        }

        final Token nextToken = tokenStream.peekAt(1);
        // IS 다음이 NULL이면 IS NULL, NOT이면 IS NOT NULL이므로 false
        return nextToken != null && nextToken.type() == TokenType.NULL;
    }

    @Override
    public Expression parse(
            final TokenStream tokenStream,
            final Expression left
    ) {
        tokenStream.consume(TokenType.IS, "IS 키워드가 필요합니다");
        tokenStream.consume(TokenType.NULL,
                "IS 뒤에는 NULL이 와야 합니다.\n" +
                        "예시: email IS NULL");

        final SourceLocation location = left.location();
        return new IsNullExpression(left, location);
    }

    @Override
    public OperatorPriority priority() {
        return OperatorPriority.IS_NULL;
    }
}
