package com.jaeyeonling.parser;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.clause.SelectClause;
import com.jaeyeonling.ast.expression.AllColumns;
import com.jaeyeonling.ast.expression.ColumnReference;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.ExpressionSelectItem;
import com.jaeyeonling.ast.expression.SelectItem;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SELECT 절 파싱을 담당하는 클래스.
 */
public class SelectClauseParser {

    private final TokenStream tokenStream;
    private final ExpressionParser expressionParser;

    public SelectClauseParser(final TokenStream tokenStream) {
        this.tokenStream = tokenStream;
        this.expressionParser = new ExpressionParser(tokenStream);
    }

    /**
     * SELECT 절을 파싱합니다.
     */
    public SelectClause parse() {
        final Token selectToken = tokenStream.consume(TokenType.SELECT,
                "SQL 쿼리는 SELECT로 시작해야 합니다.\n" +
                        "예시: SELECT * FROM 테이블명");
        final boolean distinct = tokenStream.advanceIfMatch(TokenType.DISTINCT);
        final List<SelectItem> selectItems = parseSelectList();

        return new SelectClause(distinct, selectItems, new SourceLocation(selectToken));
    }

    /**
     * SELECT 리스트를 파싱합니다.
     */
    private List<SelectItem> parseSelectList() {
        final List<SelectItem> items = new ArrayList<>();

        do {
            items.add(parseSelectItem());
        } while (tokenStream.advanceIfMatch(TokenType.COMMA));

        return items;
    }

    /**
     * 개별 SELECT 아이템을 파싱합니다.
     */
    private SelectItem parseSelectItem() {
        // * (전체 컬럼)
        if (tokenStream.advanceIfMatch(TokenType.STAR)) {
            final Token star = tokenStream.previous();
            return new AllColumns(new SourceLocation(star));
        }

        // 표현식 (컬럼 참조 포함)
        final Expression expr = expressionParser.parseExpression();

        // 별칭 처리
        Optional<String> alias = Optional.empty();
        if (tokenStream.advanceIfMatch(TokenType.AS)) {
            Token aliasToken = tokenStream.consume(TokenType.IDENTIFIER,
                    "AS 키워드 다음에는 컬럼 별칭을 지정해야 합니다.\n" +
                            "예시: SELECT name AS 이름, age AS 나이 FROM users");
            alias = Optional.of(aliasToken.value());
        } else if (tokenStream.check(TokenType.IDENTIFIER) &&
                !isReservedKeyword(tokenStream.peek())) {
            // AS 없이 별칭이 올 수 있음
            alias = Optional.of(tokenStream.advance().value());
        }

        // 컬럼 참조인 경우
        if (expr instanceof ColumnReference colRef) {
            // 기존 컬럼 참조에 별칭을 추가한 새로운 인스턴스 생성
            return alias.map(formattedContent -> new ColumnReference(
                    colRef.tableName().orElse(null),
                    colRef.columnName(),
                    formattedContent,
                    colRef.location()
            )).orElse(colRef);
        }

        // 일반 표현식인 경우
        return new ExpressionSelectItem(expr, alias.orElse(null), expr.location());
    }

    /**
     * 예약어인지 확인합니다.
     */
    private boolean isReservedKeyword(final Token token) {
        return tokenStream.isKeyword(token);
    }
}
