package com.jaeyeonling.parser.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.ColumnReference;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.exception.SyntaxException;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.parser.TokenStream;

/**
 * 식별자 파싱을 담당하는 클래스
 * - 컬럼 참조 처리
 * - 테이블.컬럼 형식 지원
 */
public final class IdentifierParser {

    private final TokenStream tokenStream;

    public IdentifierParser(final TokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    /**
     * 식별자(컬럼 참조)를 파싱합니다.
     * 식별자가 아닌 경우 null을 반환합니다.
     */
    public Expression parseIdentifier() {
        if (!isIdentifier()) {
            return null;
        }

        final Token identToken = tokenStream.consume(
                TokenType.IDENTIFIER,
                "식별자가 필요합니다"
        );

        if (!tokenStream.advanceIfMatch(TokenType.DOT)) {
            return ColumnReference.of(
                    null,
                    identToken.value(),
                    new SourceLocation(identToken)
            );
        }

        if (!tokenStream.check(TokenType.IDENTIFIER)) {
            throw new SyntaxException(
                    "테이블명 뒤에 점(.)이 오면 컬럼명이 와야 합니다.\n" +
                            "예시: users.name, orders.id",
                    tokenStream.peek()
            );
        }

        final String tableName = identToken.value();
        final Token columnToken = tokenStream.consume(
                TokenType.IDENTIFIER,
                "컬럼명이 필요합니다"
        );
        final String columnName = columnToken.value();

        return ColumnReference.of(
                tableName,
                columnName,
                new SourceLocation(identToken)
        );
    }

    /**
     * 현재 토큰이 식별자인지 확인합니다.
     */
    public boolean isIdentifier() {
        if (tokenStream.isAtEnd()) {
            return false;
        }
        return tokenStream.check(TokenType.IDENTIFIER);
    }
}
