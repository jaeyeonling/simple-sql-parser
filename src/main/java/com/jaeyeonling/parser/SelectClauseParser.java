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

        // 별칭 파싱 (AS 키워드 있거나 없거나)
        final Optional<String> alias = tokenStream.parseOptionalAlias(
                "AS 키워드 다음에는 컬럼 별칭을 지정해야 합니다.\n" +
                        "예시: SELECT name AS 이름, age AS 나이 FROM users"
        );

        if (expr instanceof ColumnReference colRef) {
            return alias.map(a -> new ColumnReference(
                    colRef.tableName().orElse(null),
                    colRef.columnName(),
                    a,
                    colRef.location()
            )).orElse(colRef);
        }

        return new ExpressionSelectItem(expr, alias.orElse(null), expr.location());
    }
}
