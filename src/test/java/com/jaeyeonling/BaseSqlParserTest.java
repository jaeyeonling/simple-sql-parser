package com.jaeyeonling;

import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.exception.SqlParseException;
import com.jaeyeonling.parser.SqlParser;
import com.jaeyeonling.visitor.SqlToStringVisitor;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * SQL 파서 테스트의 기본 클래스.
 * 공통 유틸리티 메서드를 제공합니다.
 */
public abstract class BaseSqlParserTest {

    protected SqlToStringVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new SqlToStringVisitor();
    }

    /**
     * SQL을 파싱하고 검증합니다.
     */
    protected SelectStatement parseAndValidate(final String sql) {
        final SelectStatement stmt = new SqlParser(sql).parse();
        assertThat(stmt).isNotNull();
        return stmt;
    }

    /**
     * SQL을 파싱하고 재구성한 결과가 원본과 동일한지 검증합니다.
     */
    protected SelectStatement parseAndAssertReconstructed(final String sql) {
        final SelectStatement stmt = parseAndValidate(sql);
        final String reconstructed = stmt.accept(visitor);
        assertThat(reconstructed).isEqualTo(sql);
        return stmt;
    }

    /**
     * SQL을 파싱하고 재구성한 결과가 예상값과 동일한지 검증합니다.
     */
    protected SelectStatement parseAndAssertReconstructed(
            final String sql,
            final String expected
    ) {
        SelectStatement stmt = parseAndValidate(sql);
        String reconstructed = stmt.accept(visitor);
        assertThat(reconstructed).isEqualTo(expected);
        return stmt;
    }

    /**
     * SQL 파싱 예외가 발생하는지 검증합니다.
     * SqlParseException 또는 그 하위 클래스(LexicalException, SyntaxException)가 발생해야 합니다.
     */
    protected void assertParseException(
            final String sql,
            final String expectedMessage
    ) {
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining(expectedMessage);
    }

    /**
     * SQL 파싱 예외가 발생하는지만 검증합니다 (메시지 검증 없음).
     */
    protected void assertParseException(final String sql) {
        assertThatThrownBy(() -> new SqlParser(sql).parse())
                .isInstanceOf(SqlParseException.class);
    }

    /**
     * SQL 파싱이 성공하는지만 검증합니다.
     */
    protected void assertParseSuccess(final String sql) {
        assertThat(new SqlParser(sql).parse()).isNotNull();
    }
}
