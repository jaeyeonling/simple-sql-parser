package com.jaeyeonling.parser.expression.operators;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.InExpression;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;
import com.jaeyeonling.parser.expression.ExpressionProvider;
import com.jaeyeonling.parser.expression.OperatorParser;
import com.jaeyeonling.parser.expression.OperatorPriority;

import java.util.ArrayList;
import java.util.List;

/**
 * IN/NOT IN 연산자 파싱을 담당합니다.
 * 예: status IN ('active', 'pending'), id NOT IN (1, 2, 3)
 */
public final class InOperatorParser implements OperatorParser {

    private final ExpressionProvider expressionProvider;

    public InOperatorParser(final ExpressionProvider expressionProvider) {
        this.expressionProvider = expressionProvider;
    }

    @Override
    public boolean canParse(final TokenStream tokenStream) {
        return tokenStream.check(TokenType.IN);
    }

    @Override
    public Expression parse(
            final TokenStream tokenStream,
            final Expression left
    ) {
        tokenStream.consume(TokenType.IN,
                "IN 키워드가 필요합니다");

        tokenStream.consume(TokenType.LPAREN,
                "IN 연산자 뒤에는 '('가 와야 합니다.\n" +
                        "예시: status IN ('active', 'pending')");

        // 값 목록 파싱
        final List<Expression> values = parseValueList(tokenStream);

        tokenStream.consume(TokenType.RPAREN,
                "IN 절의 값 목록은 ')'로 닫아야 합니다.");

        final SourceLocation location = left.location();

        return new InExpression(left, values, location);
    }

    private List<Expression> parseValueList(final TokenStream tokenStream) {
        List<Expression> values = new ArrayList<>();

        do {
            // 쉼표로 구분된 추가 값들
            values.add(expressionProvider.parseAdditiveExpression());
        } while (tokenStream.advanceIfMatch(TokenType.COMMA));

        return values;
    }

    @Override
    public OperatorPriority priority() {
        return OperatorPriority.SPECIAL;
    }
}
