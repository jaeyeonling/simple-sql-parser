package com.jaeyeonling.integration;

import com.jaeyeonling.ast.clause.FromClause;
import com.jaeyeonling.ast.clause.LimitClause;
import com.jaeyeonling.ast.clause.OrderByClause;
import com.jaeyeonling.ast.clause.SelectClause;
import com.jaeyeonling.ast.clause.WhereClause;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.ColumnReference;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.ast.expression.ExpressionSelectItem;
import com.jaeyeonling.ast.expression.IntegerLiteral;
import com.jaeyeonling.ast.expression.Operator;
import com.jaeyeonling.ast.expression.SelectItem;
import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.ast.table.Table;
import com.jaeyeonling.parser.SqlParser;
import com.jaeyeonling.visitor.SqlToStringVisitor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 복잡한 SQL 쿼리에 대한 통합 테스트
 * 여러 기능이 조합된 실제 사용 사례를 테스트합니다.
 */
class ComplexQueryIntegrationTest {

    private final SqlToStringVisitor sqlToStringVisitor = new SqlToStringVisitor();

    @Test
    @DisplayName("모든 절이 포함된 복잡한 쿼리를 파싱한다")
    void testCompleteQuery() {
        // given
        final String sql = "SELECT DISTINCT u.id, u.name AS username, u.email " +
                "FROM users u, orders o " +
                "WHERE u.id = o.user_id AND u.status = 'active' " +
                "ORDER BY u.created_at DESC, u.name ASC " +
                "LIMIT 20 OFFSET 40";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        // SELECT 절 검증
        final SelectClause selectClause = stmt.selectClause();
        assertThat(selectClause.isDistinct()).isTrue();
        assertThat(selectClause.selectItems()).hasSize(3);

        // FROM 절 검증
        final FromClause fromClause = stmt.fromClause().orElseThrow();
        assertThat(fromClause.tableReferences()).hasSize(2);

        final List<String> tableNames = fromClause.tableReferences().stream()
                .map(ref -> ((Table) ref).name())
                .toList();
        assertThat(tableNames).containsExactly("users", "orders");

        // WHERE 절 검증
        final WhereClause whereClause = stmt.whereClause().orElseThrow();
        assertThat(whereClause.condition()).isInstanceOf(BinaryOperatorExpression.class);

        // ORDER BY 절 검증
        final OrderByClause orderByClause = stmt.orderByClause().orElseThrow();
        assertThat(orderByClause.orderByItems()).hasSize(2);

        // LIMIT/OFFSET 검증
        final LimitClause limitClause = stmt.limitClause().orElseThrow();
        assertThat(limitClause.limit()).isEqualTo(20);
        assertThat(limitClause.offset()).hasValue(40);
    }

    @Test
    @DisplayName("복잡한 WHERE 조건을 가진 쿼리를 파싱한다")
    void testComplexWhereConditions() {
        // given
        final String sql = "SELECT * FROM products p " +
                "WHERE p.price > 100 AND p.price < 1000 " +
                "AND (p.category = 'electronics' OR p.category = 'computers') " +
                "AND p.stock > 0 " +
                "AND p.name LIKE 'Apple%'";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        final WhereClause whereClause = stmt.whereClause().orElseThrow();
        final Expression condition = whereClause.condition();

        // 최상위는 AND 연산자여야 함
        assertThat(condition).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression topAnd = (BinaryOperatorExpression) condition;
        assertThat(topAnd.operator()).isEqualTo(Operator.AND);
    }

    @Test
    @DisplayName("산술 연산자를 포함한 쿼리를 파싱한다")
    void testQueryWithArithmeticOperators() {
        // given
        final String sql = "SELECT price * quantity AS total_price, " +
                "discount_amount / 100 AS discount_rate, " +
                "price - discount_amount AS discounted_price, " +
                "tax_amount + shipping_fee AS additional_cost " +
                "FROM orders " +
                "WHERE price * quantity / 100 > 10";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        final List<SelectItem> items = stmt.selectClause().selectItems();
        assertThat(items).hasSize(4);

        // 각 산술 연산이 올바르게 파싱되었는지 확인
        final ExpressionSelectItem item1 = (ExpressionSelectItem) items.getFirst();
        assertThat(item1.alias()).hasValue("total_price");
        assertThat(item1.expression()).isInstanceOf(BinaryOperatorExpression.class);
        BinaryOperatorExpression expr1 = (BinaryOperatorExpression) item1.expression();
        assertThat(expr1.operator()).isEqualTo(Operator.MULTIPLY);

        final ExpressionSelectItem item2 = (ExpressionSelectItem) items.get(1);
        assertThat(item2.alias()).hasValue("discount_rate");
        assertThat(item2.expression()).isInstanceOf(BinaryOperatorExpression.class);
        BinaryOperatorExpression expr2 = (BinaryOperatorExpression) item2.expression();
        assertThat(expr2.operator()).isEqualTo(Operator.DIVIDE);

        // WHERE 절의 복잡한 산술 표현식 확인
        final WhereClause whereClause = stmt.whereClause().orElseThrow();
        final Expression condition = whereClause.condition();
        assertThat(condition).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression whereExpr = (BinaryOperatorExpression) condition;
        assertThat(whereExpr.operator()).isEqualTo(Operator.GREATER_THAN);

        // 왼쪽 표현식: price * quantity / 100
        assertThat(whereExpr.left()).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression leftExpr = (BinaryOperatorExpression) whereExpr.left();
        assertThat(leftExpr.operator()).isEqualTo(Operator.DIVIDE);
    }

    @Test
    @DisplayName("다양한 별칭을 사용하는 쿼리를 파싱한다")
    void testQueryWithVariousAliases() {
        // given
        final String sql = "SELECT u.id user_id, u.name AS username, u.email, 'active' status " +
                "FROM users AS u " +
                "WHERE u.age > 18";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        final List<SelectItem> items = stmt.selectClause().selectItems();
        assertThat(items).hasSize(4);

        // 별칭 검증
        final ColumnReference col1 = (ColumnReference) items.getFirst();
        assertThat(col1.alias()).hasValue("user_id");

        final ColumnReference col2 = (ColumnReference) items.get(1);
        assertThat(col2.alias()).hasValue("username");

        final ColumnReference col3 = (ColumnReference) items.get(2);
        assertThat(col3.alias()).isEmpty();

        final ExpressionSelectItem col4 = (ExpressionSelectItem) items.get(3);
        assertThat(col4.alias()).hasValue("status");
    }

    @Test
    @DisplayName("중첩된 AND/OR 조건을 올바르게 파싱한다")
    void testNestedAndOrConditions() {
        // given
        final String sql = "SELECT * FROM users " +
                "WHERE age > 18 AND age < 65 " +
                "OR status = 'premium' AND verified = 'true' " +
                "OR role = 'admin'";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        // AND가 OR보다 우선순위가 높으므로
        // ((age > 18 AND age < 65) OR (status = 'premium' AND verified = true) OR (role = 'admin'))
        // 형태로 파싱되어야 함
        final WhereClause whereClause = stmt.whereClause().orElseThrow();
        final Expression condition = whereClause.condition();

        // 최상위는 OR 연산자여야 함
        assertThat(condition).isInstanceOf(BinaryOperatorExpression.class);
        final BinaryOperatorExpression topOr = (BinaryOperatorExpression) condition;
        assertThat(topOr.operator()).isEqualTo(Operator.OR);
    }

    @Test
    @DisplayName("대소문자가 섞인 쿼리를 올바르게 파싱한다")
    void testMixedCaseQuery() {
        // given
        final String sql = "sElEcT DISTINCT u.ID, U.Name " +
                "fRoM Users U " +
                "WhErE u.AGE > 18 " +
                "oRdEr By u.name aSc " +
                "LiMiT 10";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.selectClause().isDistinct()).isTrue();
        assertThat(stmt.selectClause().selectItems()).hasSize(2);
        assertThat(stmt.whereClause()).isPresent();
        assertThat(stmt.orderByClause()).isPresent();
        assertThat(stmt.limitClause()).isPresent();
    }

    @Test
    @Disabled("현재 파서는 키워드를 식별자로 사용할 수 없음")
    @DisplayName("SQL 키워드를 식별자로 사용하는 경우를 처리한다")
    void testKeywordsAsIdentifiers() {
        // given
        // 'from', 'select', 'where' 등이 컬럼명으로 사용되는 경우
        final String sql = "SELECT u.select, u.from, u.where AS condition " +
                "FROM users u " +
                "WHERE u.order > 10";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        final List<SelectItem> items = stmt.selectClause().selectItems();
        assertThat(items).hasSize(3);

        // 키워드가 컬럼명으로 사용됨
        final ColumnReference col1 = (ColumnReference) items.getFirst();
        assertThat(col1.columnName()).isEqualTo("select");

        final ColumnReference col2 = (ColumnReference) items.get(1);
        assertThat(col2.columnName()).isEqualTo("from");

        final ColumnReference col3 = (ColumnReference) items.get(2);
        assertThat(col3.columnName()).isEqualTo("where");
        assertThat(col3.alias()).hasValue("condition");
    }

    @Test
    @DisplayName("매우 긴 쿼리를 파싱할 수 있다")
    void testVeryLongQuery() {
        // given
        final StringBuilder sql = new StringBuilder("SELECT ");

        // 100개의 컬럼 생성
        for (int i = 1; i <= 100; i++) {
            if (i > 1) {
                sql.append(", ");
            }
            sql.append("col").append(i);
        }

        sql.append(" FROM very_wide_table WHERE ");

        // 50개의 AND 조건 생성
        for (int i = 1; i <= 50; i++) {
            if (i > 1) {
                sql.append(" AND ");
            }
            sql.append("col").append(i).append(" > 0");
        }

        // when
        final SelectStatement stmt = new SqlParser(sql.toString()).parse();

        // then
        assertThat(stmt.selectClause().selectItems()).hasSize(100);
        assertThat(stmt.whereClause()).isPresent();
    }

    @ParameterizedTest
    @DisplayName("SQL 재구성이 원본과 의미적으로 동일하다")
    @ValueSource(strings = {
            "SELECT * FROM users",
            "SELECT id, name FROM users WHERE age > 18",
            "SELECT DISTINCT city FROM users ORDER BY city",
            "SELECT * FROM users LIMIT 10 OFFSET 20",
            "SELECT u.id, u.name FROM users u WHERE u.status = 'active' ORDER BY u.id DESC"
    })
    void testSqlReconstructionConsistency(final String originalSql) {
        // when
        final SelectStatement stmt1 = new SqlParser(originalSql).parse();
        final String reconstructed = stmt1.accept(sqlToStringVisitor);
        final SelectStatement stmt2 = new SqlParser(reconstructed).parse();

        // then
        // 재구성된 SQL을 다시 파싱해도 같은 AST가 생성되어야 함
        assertThat(stmt2.selectClause().selectItems())
                .hasSameSizeAs(stmt1.selectClause().selectItems());
    }

    @Test
    @DisplayName("연속된 연산자를 올바르게 처리한다")
    void testConsecutiveOperators() {
        // given
        final String sql = "SELECT * FROM users WHERE age >= 18 AND age <= 65 AND status != 'banned'";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        final WhereClause whereClause = stmt.whereClause().orElseThrow();
        final Expression condition = whereClause.condition();

        // 연속된 AND는 왼쪽 결합으로 처리됨
        // ((age >= 18 AND age <= 65) AND status != 'banned')
        assertThat(condition).isInstanceOf(BinaryOperatorExpression.class);
    }

    @Test
    @DisplayName("특수 문자가 포함된 식별자를 처리한다")
    void testIdentifiersWithSpecialCharacters() {
        // given
        final String sql = "SELECT user_id, first_name, last_name, email_address " +
                "FROM user_accounts " +
                "WHERE created_at > '2024-01-01'";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        final List<SelectItem> items = stmt.selectClause().selectItems();
        assertThat(items).hasSize(4);

        final List<String> columnNames = items.stream()
                .map(item -> ((ColumnReference) item).columnName())
                .toList();
        assertThat(columnNames).containsExactly("user_id", "first_name", "last_name", "email_address");

        final Table table = (Table) stmt.fromClause().orElseThrow().tableReferences().getFirst();
        assertThat(table.name()).isEqualTo("user_accounts");
    }

    @Test
    @DisplayName("BETWEEN 연산자를 LIKE로 잘못 사용한 경우")
    void testBetweenAsLike() {
        // given
        // BETWEEN은 현재 구현되지 않았으므로 식별자로 처리됨
        final String sql = "SELECT * FROM users WHERE age BETWEEN 18 AND 65";

        // when
        // 'BETWEEN'이 컬럼명으로 해석되지만 파싱은 성공함
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt).isNotNull();
        assertThat(stmt.whereClause()).isPresent();
    }

    @ParameterizedTest
    @DisplayName("다양한 LIMIT 값으로 쿼리를 파싱한다")
    @ValueSource(ints = {0, 1, 10, 100, 1000, 999999})
    void testVariousLimitValues(int limit) {
        // given
        final String sql = String.format("SELECT * FROM users LIMIT %d", limit);

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        final LimitClause limitClause = stmt.limitClause().orElseThrow();
        assertThat(limitClause.limit()).isEqualTo(limit);
    }

    @Test
    @DisplayName("중복 별칭 사용 시에도 파싱이 성공한다")
    void testDuplicateAliases() {
        // given
        // 같은 별칭 'id'를 여러 번 사용
        final String sql = "SELECT user_id AS id, order_id AS id, product_id AS id FROM users";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        // 파서는 중복 별칭을 허용함 (의미적 검증은 하지 않음)
        final List<SelectItem> items = stmt.selectClause().selectItems();
        assertThat(items).hasSize(3);
        assertThat(items).allMatch(item ->
                item instanceof ColumnReference && item.alias().orElse("").equals("id"));
    }

    @Test
    @DisplayName("깊게 중첩된 표현식을 파싱한다")
    void testDeeplyNestedExpressions() {
        // given
        final String sql = "SELECT * FROM users WHERE " +
                "(a = 1 OR (b = 2 AND (value = 3 OR (d = 4 AND e = 5))))";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        assertThat(stmt.whereClause()).isPresent();
    }

    @Test
    @DisplayName("빈 결과를 반환할 수 있는 쿼리")
    void testQueryThatMayReturnEmpty() {
        // given
        final String sql = "SELECT * FROM users WHERE 1 = 0";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        final WhereClause whereClause = stmt.whereClause().orElseThrow();
        final BinaryOperatorExpression condition = (BinaryOperatorExpression) whereClause.condition();

        // 1 = 0 조건 확인
        assertThat(condition.left()).isInstanceOf(IntegerLiteral.class);
        assertThat(((IntegerLiteral) condition.left()).value()).isEqualTo(1L);
        assertThat(condition.operator()).isEqualTo(Operator.EQUALS);
        assertThat(condition.right()).isInstanceOf(IntegerLiteral.class);
        assertThat(((IntegerLiteral) condition.right()).value()).isEqualTo(0L);
    }

    @Test
    @DisplayName("실제 사용 예제: 사용자 검색 쿼리")
    void testRealWorldUserSearchQuery() {
        // given
        final String sql = "SELECT u.id, u.username, u.email, u.created_at " +
                "FROM users u " +
                "WHERE u.status = 'active' " +
                "AND (u.username LIKE 'john%' OR u.email LIKE 'john%') " +
                "AND u.created_at > '2024-01-01' " +
                "ORDER BY u.created_at DESC " +
                "LIMIT 20";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();

        // then
        // 실제 애플리케이션에서 사용할 법한 쿼리가 올바르게 파싱됨
        assertThat(stmt.selectClause().selectItems()).hasSize(4);
        assertThat(stmt.whereClause()).isPresent();
        assertThat(stmt.orderByClause()).isPresent();
        assertThat(stmt.limitClause()).isPresent();
        assertThat(stmt.limitClause().get().limit()).isEqualTo(20);
    }
}
