package com.jaeyeonling.parser.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.AllColumns;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.FunctionCall;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * 함수 호출 표현식 파싱을 담당하는 클래스
 * - COUNT(*), SUM(column), AVG(expression) 등 처리
 * - 표준 집계 함수 지원
 */
public final class FunctionParser {

    private final TokenStream tokenStream;
    private final ExpressionProvider expressionProvider;

    public FunctionParser(
            final TokenStream tokenStream,
            final ExpressionProvider expressionProvider
    ) {
        this.tokenStream = tokenStream;
        this.expressionProvider = expressionProvider;
    }

    /**
     * 함수 호출이 가능한지 확인합니다.
     * IDENTIFIER 다음에 LPAREN이 오는 패턴을 확인합니다.
     */
    public boolean canParseFunction() {
        if (!tokenStream.check(TokenType.IDENTIFIER)) {
            return false;
        }

        final Token nextToken = tokenStream.peekAt(1);
        return nextToken != null && nextToken.type() == TokenType.LPAREN;
    }

    /**
     * 함수 호출을 파싱합니다.
     * 전제조건: canParseFunction()이 true를 반환한 경우
     *
     * @return 파싱된 FunctionCall 표현식
     */
    public Expression parseFunction() {
        final Token functionToken = tokenStream.consume(TokenType.IDENTIFIER, "함수 이름이 필요합니다");
        final String functionName = functionToken.value();

        tokenStream.consume(TokenType.LPAREN,
                "함수 호출에는 괄호가 필요합니다.\n예시: COUNT(*), SUM(amount)");

        final List<Expression> arguments = parseFunctionArguments();

        final Token closeParen = tokenStream.consume(TokenType.RPAREN,
                "함수 호출은 닫는 괄호로 끝나야 합니다.\n예시: COUNT(*), SUM(amount)");

        return new FunctionCall(
                functionName.toUpperCase(),
                arguments,
                new SourceLocation(functionToken).merge(new SourceLocation(closeParen))
        );
    }

    /**
     * 함수 인자들을 파싱합니다.
     * COUNT(*)의 특별 처리와 일반 표현식 인자를 모두 처리합니다.
     */
    private List<Expression> parseFunctionArguments() {
        // 빈 괄호인 경우
        if (tokenStream.check(TokenType.RPAREN)) {
            return emptyList();
        }

        // COUNT(*)의 특별 처리
        if (tokenStream.check(TokenType.STAR)) {
            final Token star = tokenStream.advance();
            final SourceLocation starLocation = new SourceLocation(star);
            final Expression allColumns = new AllColumns(starLocation);
            return List.of(allColumns);
        }

        final List<Expression> arguments = new ArrayList<>();
        // 일반 표현식 파싱
        do {
            // ExpressionProvider를 통해 재귀적으로 표현식 파싱
            arguments.add(expressionProvider.parseExpression());
        } while (tokenStream.advanceIfMatch(TokenType.COMMA));

        return arguments;
    }
}
