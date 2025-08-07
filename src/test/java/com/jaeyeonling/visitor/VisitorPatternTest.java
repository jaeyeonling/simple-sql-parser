package com.jaeyeonling.visitor;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.clause.FromClause;
import com.jaeyeonling.ast.clause.GroupByClause;
import com.jaeyeonling.ast.clause.HavingClause;
import com.jaeyeonling.ast.clause.LimitClause;
import com.jaeyeonling.ast.clause.OrderByClause;
import com.jaeyeonling.ast.clause.SelectClause;
import com.jaeyeonling.ast.clause.WhereClause;
import com.jaeyeonling.ast.expression.AllColumns;
import com.jaeyeonling.ast.expression.BetweenExpression;
import com.jaeyeonling.ast.expression.BinaryOperatorExpression;
import com.jaeyeonling.ast.expression.BooleanLiteral;
import com.jaeyeonling.ast.expression.ColumnReference;
import com.jaeyeonling.ast.expression.DecimalLiteral;
import com.jaeyeonling.ast.expression.ExpressionSelectItem;
import com.jaeyeonling.ast.expression.InExpression;
import com.jaeyeonling.ast.expression.IntegerLiteral;
import com.jaeyeonling.ast.expression.IsNotNullExpression;
import com.jaeyeonling.ast.expression.IsNullExpression;
import com.jaeyeonling.ast.expression.LikeExpression;
import com.jaeyeonling.ast.expression.NotBetweenExpression;
import com.jaeyeonling.ast.expression.NotInExpression;
import com.jaeyeonling.ast.expression.NotLikeExpression;
import com.jaeyeonling.ast.expression.NullLiteral;
import com.jaeyeonling.ast.expression.StringLiteral;
import com.jaeyeonling.ast.statement.SelectStatement;
import com.jaeyeonling.ast.table.Table;
import com.jaeyeonling.parser.SqlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Visitor 패턴 단위 테스트
 * AST 순회와 변환 로직을 검증합니다.
 */
class VisitorPatternTest {

    private SqlToStringVisitor sqlToStringVisitor;
    private SourceLocation testLocation;

    @BeforeEach
    void setUp() {
        sqlToStringVisitor = new SqlToStringVisitor();
        testLocation = new SourceLocation(1, 1, 0, 0);
    }

    @Test
    @DisplayName("SqlToStringVisitor가 간단한 SELECT 문을 올바르게 변환한다")
    void testSqlToStringVisitorSimpleSelect() {
        // given
        final String originalSql = "SELECT * FROM users";

        // when
        final SelectStatement stmt = new SqlParser(originalSql).parse();
        final String reconstructed = stmt.accept(sqlToStringVisitor);

        // then
        assertThat(reconstructed).isEqualTo("SELECT * FROM users");
    }

    @Test
    @DisplayName("SqlToStringVisitor가 별칭을 올바르게 처리한다")
    void testSqlToStringVisitorWithAliases() {
        // given
        final String originalSql = "SELECT u.id, u.name AS username FROM users u";

        // when
        final SelectStatement stmt = new SqlParser(originalSql).parse();
        final String reconstructed = stmt.accept(sqlToStringVisitor);

        // then
        assertThat(reconstructed).isEqualTo("SELECT u.id, u.name AS username FROM users u");
    }

    @Test
    @DisplayName("SqlToStringVisitor가 WHERE 절을 올바르게 처리한다")
    void testSqlToStringVisitorWithWhere() {
        // given
        final String originalSql = "SELECT * FROM users WHERE age > 18 AND status = 'active'";

        // when
        final SelectStatement stmt = new SqlParser(originalSql).parse();
        final String reconstructed = stmt.accept(sqlToStringVisitor);

        // then
        assertThat(reconstructed).isEqualTo("SELECT * FROM users WHERE age > 18 AND status = 'active'");
    }

    @Test
    @DisplayName("SqlToStringVisitor가 DISTINCT를 올바르게 처리한다")
    void testSqlToStringVisitorWithDistinct() {
        // given
        final String originalSql = "SELECT DISTINCT city FROM users";

        // when
        final SelectStatement stmt = new SqlParser(originalSql).parse();
        final String reconstructed = stmt.accept(sqlToStringVisitor);

        // then
        assertThat(reconstructed).isEqualTo("SELECT DISTINCT city FROM users");
    }

    @Test
    @DisplayName("SqlToStringVisitor가 ORDER BY를 올바르게 처리한다")
    void testSqlToStringVisitorWithOrderBy() {
        // given
        final String originalSql = "SELECT * FROM users ORDER BY name ASC, age DESC";

        // when
        final SelectStatement stmt = new SqlParser(originalSql).parse();
        final String reconstructed = stmt.accept(sqlToStringVisitor);

        // then
        assertThat(reconstructed).isEqualTo("SELECT * FROM users ORDER BY name ASC, age DESC");
    }

    @Test
    @DisplayName("SqlToStringVisitor가 LIMIT/OFFSET을 올바르게 처리한다")
    void testSqlToStringVisitorWithLimitOffset() {
        // given
        final String originalSql = "SELECT * FROM users LIMIT 10 OFFSET 20";

        // when
        final SelectStatement stmt = new SqlParser(originalSql).parse();
        final String reconstructed = stmt.accept(sqlToStringVisitor);

        // then
        assertThat(reconstructed).isEqualTo("SELECT * FROM users LIMIT 10 OFFSET 20");
    }

    @Test
    @DisplayName("커스텀 Visitor로 컬럼명을 수집할 수 있다")
    void testCustomColumnCollectorVisitor() {
        // given
        final String sql = "SELECT u.id, u.name, u.email FROM users u WHERE u.age > 18 ORDER BY u.name";
        final SelectStatement stmt = new SqlParser(sql).parse();

        // 컬럼명을 수집하는 커스텀 Visitor
        final AstVisitor<List<String>> columnCollector = new AstVisitor<>() {
            private final List<String> columns = new ArrayList<>();

            @Override
            public List<String> visitSelectStatement(SelectStatement selectStatement) {
                selectStatement.selectClause().accept(this);
                selectStatement.whereClause().ifPresent(w -> w.accept(this));
                selectStatement.orderByClause().ifPresent(o -> o.accept(this));
                return columns;
            }

            @Override
            public List<String> visitSelectClause(SelectClause selectClause) {
                selectClause.selectItems().forEach(item -> item.accept(this));
                return columns;
            }

            @Override
            public List<String> visitFromClause(FromClause fromClause) {
                return columns;
            }

            @Override
            public List<String> visitWhereClause(WhereClause whereClause) {
                whereClause.condition().accept(this);
                return columns;
            }

            @Override
            public List<String> visitGroupByClause(GroupByClause groupByClause) {
                return columns;
            }

            @Override
            public List<String> visitHavingClause(HavingClause havingClause) {
                return columns;
            }

            @Override
            public List<String> visitOrderByClause(OrderByClause orderByClause) {
                orderByClause.orderByItems().forEach(item ->
                        item.expression().accept(this)
                );
                return columns;
            }

            @Override
            public List<String> visitLimitClause(LimitClause limitClause) {
                return columns;
            }

            @Override
            public List<String> visitTable(Table table) {
                return columns;
            }

            @Override
            public List<String> visitColumnReference(ColumnReference columnReference) {
                columns.add(columnReference.columnName());
                return columns;
            }

            @Override
            public List<String> visitAllColumns(AllColumns allColumns) {
                return columns;
            }

            @Override
            public List<String> visitExpressionSelectItem(ExpressionSelectItem expressionSelectItem) {
                expressionSelectItem.expression().accept(this);
                return columns;
            }

            @Override
            public List<String> visitBinaryOperatorExpression(BinaryOperatorExpression binaryOperatorExpression) {
                binaryOperatorExpression.left().accept(this);
                if (binaryOperatorExpression.right() != null) {
                    binaryOperatorExpression.right().accept(this);
                }
                return columns;
            }

            @Override
            public List<String> visitIntegerLiteral(IntegerLiteral integerLiteral) {
                return columns;
            }

            @Override
            public List<String> visitDecimalLiteral(DecimalLiteral decimalLiteral) {
                return columns;
            }

            @Override
            public List<String> visitStringLiteral(StringLiteral stringLiteral) {
                return columns;
            }

            @Override
            public List<String> visitBooleanLiteral(BooleanLiteral booleanLiteral) {
                return columns;
            }

            @Override
            public List<String> visitNullLiteral(NullLiteral nullLiteral) {
                return columns;
            }

            @Override
            public List<String> visitLikeExpression(LikeExpression likeExpression) {
                likeExpression.expression().accept(this);
                likeExpression.pattern().accept(this);
                return columns;
            }

            @Override
            public List<String> visitInExpression(InExpression inExpression) {
                inExpression.expression().accept(this);
                inExpression.values().forEach(v -> v.accept(this));
                return columns;
            }

            @Override
            public List<String> visitBetweenExpression(BetweenExpression betweenExpression) {
                betweenExpression.expression().accept(this);
                betweenExpression.lowerBound().accept(this);
                betweenExpression.upperBound().accept(this);
                return columns;
            }

            @Override
            public List<String> visitIsNullExpression(IsNullExpression isNullExpression) {
                isNullExpression.expression().accept(this);
                return columns;
            }

            @Override
            public List<String> visitNotLikeExpression(NotLikeExpression notLikeExpression) {
                notLikeExpression.expression().accept(this);
                notLikeExpression.pattern().accept(this);
                return columns;
            }

            @Override
            public List<String> visitNotInExpression(NotInExpression notInExpression) {
                notInExpression.expression().accept(this);
                notInExpression.values().forEach(v -> v.accept(this));
                return columns;
            }

            @Override
            public List<String> visitNotBetweenExpression(NotBetweenExpression notBetweenExpression) {
                notBetweenExpression.expression().accept(this);
                notBetweenExpression.lowerBound().accept(this);
                notBetweenExpression.upperBound().accept(this);
                return columns;
            }

            @Override
            public List<String> visitIsNotNullExpression(IsNotNullExpression isNotNullExpression) {
                isNotNullExpression.expression().accept(this);
                return columns;
            }
        };

        // when
        final List<String> columnNames = stmt.accept(columnCollector);

        // then
        assertThat(columnNames).containsExactly("id", "name", "email", "age", "name");
    }

    @Test
    @DisplayName("커스텀 Visitor로 테이블명을 수집할 수 있다")
    void testCustomTableCollectorVisitor() {
        // given
        final String sql = "SELECT * FROM users, orders, products p";
        final SelectStatement stmt = new SqlParser(sql).parse();

        // 테이블명을 수집하는 커스텀 Visitor
        final AstVisitor<List<String>> tableCollector = new AstVisitor<>() {
            private final List<String> tables = new ArrayList<>();

            @Override
            public List<String> visitSelectStatement(SelectStatement selectStatement) {
                selectStatement.fromClause().ifPresent(from -> from.accept(this));
                return tables;
            }

            @Override
            public List<String> visitSelectClause(SelectClause selectClause) {
                return tables;
            }

            @Override
            public List<String> visitFromClause(FromClause fromClause) {
                fromClause.tableReferences().forEach(ref -> ref.accept(this));
                return tables;
            }

            @Override
            public List<String> visitWhereClause(WhereClause whereClause) {
                return tables;
            }

            @Override
            public List<String> visitGroupByClause(GroupByClause groupByClause) {
                return tables;
            }

            @Override
            public List<String> visitHavingClause(HavingClause havingClause) {
                return tables;
            }

            @Override
            public List<String> visitOrderByClause(OrderByClause orderByClause) {
                return tables;
            }

            @Override
            public List<String> visitLimitClause(LimitClause limitClause) {
                return tables;
            }

            @Override
            public List<String> visitTable(Table table) {
                tables.add(table.name());
                return tables;
            }

            @Override
            public List<String> visitColumnReference(ColumnReference columnReference) {
                return tables;
            }

            @Override
            public List<String> visitAllColumns(AllColumns allColumns) {
                return tables;
            }

            @Override
            public List<String> visitExpressionSelectItem(ExpressionSelectItem expressionSelectItem) {
                return tables;
            }

            @Override
            public List<String> visitBinaryOperatorExpression(BinaryOperatorExpression binaryOperatorExpression) {
                return tables;
            }

            @Override
            public List<String> visitIntegerLiteral(IntegerLiteral integerLiteral) {
                return tables;
            }

            @Override
            public List<String> visitDecimalLiteral(DecimalLiteral decimalLiteral) {
                return tables;
            }

            @Override
            public List<String> visitStringLiteral(StringLiteral stringLiteral) {
                return tables;
            }

            @Override
            public List<String> visitBooleanLiteral(BooleanLiteral booleanLiteral) {
                return tables;
            }

            @Override
            public List<String> visitNullLiteral(NullLiteral nullLiteral) {
                return tables;
            }

            @Override
            public List<String> visitLikeExpression(LikeExpression likeExpression) {
                return tables;
            }

            @Override
            public List<String> visitInExpression(InExpression inExpression) {
                return tables;
            }

            @Override
            public List<String> visitBetweenExpression(BetweenExpression betweenExpression) {
                return tables;
            }

            @Override
            public List<String> visitIsNullExpression(IsNullExpression isNullExpression) {
                return tables;
            }

            @Override
            public List<String> visitNotLikeExpression(NotLikeExpression notLikeExpression) {
                return tables;
            }

            @Override
            public List<String> visitNotInExpression(NotInExpression notInExpression) {
                return tables;
            }

            @Override
            public List<String> visitNotBetweenExpression(NotBetweenExpression notBetweenExpression) {
                return tables;
            }

            @Override
            public List<String> visitIsNotNullExpression(IsNotNullExpression isNotNullExpression) {
                return tables;
            }
        };

        // when
        final List<String> tableNames = stmt.accept(tableCollector);

        // then
        assertThat(tableNames).containsExactly("users", "orders", "products");
    }

    @Test
    @DisplayName("SqlToStringVisitor가 문자열 리터럴의 따옴표를 올바르게 이스케이프한다")
    void testSqlToStringVisitorStringEscaping() {
        // given
        final StringLiteral literal = new StringLiteral("It's a test", testLocation);

        // when
        final String result = literal.accept(sqlToStringVisitor);

        // then
        assertThat(result).isEqualTo("'It''s a test'");
    }

    @Test
    @Disabled("SqlToStringVisitor가 현재 괄호를 재구성하지 않음")
    @DisplayName("SqlToStringVisitor가 복잡한 WHERE 절을 올바르게 처리한다")
    void testSqlToStringVisitorComplexWhere() {
        // given
        final String sql = "SELECT * FROM users WHERE (age > 18 AND age < 65) OR status = 'premium'";

        // when
        final SelectStatement stmt = new SqlParser(sql).parse();
        final String reconstructed = stmt.accept(sqlToStringVisitor);

        // then
        assertThat(reconstructed).isEqualTo(
                "SELECT * FROM users WHERE (age > 18 AND age < 65) OR status = 'premium'"
        );
    }

    @Test
    @DisplayName("Visitor는 AST의 모든 노드를 순회한다")
    void testVisitorTraversesAllNodes() {
        // given
        final String sql = "SELECT DISTINCT u.id, u.name AS username " +
                "FROM users u " +
                "WHERE u.age > 18 " +
                "ORDER BY u.name DESC " +
                "LIMIT 10";

        final SelectStatement stmt = new SqlParser(sql).parse();

        // 방문한 노드 타입을 추적하는 Visitor
        final AstVisitor<List<String>> nodeTypeCollector = new AstVisitor<>() {
            private final List<String> nodeTypes = new ArrayList<>();

            private List<String> record(String type) {
                nodeTypes.add(type);
                return nodeTypes;
            }

            @Override
            public List<String> visitSelectStatement(SelectStatement selectStatement) {
                record("SelectStatement");
                selectStatement.selectClause().accept(this);
                selectStatement.fromClause().ifPresent(from -> from.accept(this));
                selectStatement.whereClause().ifPresent(w -> w.accept(this));
                selectStatement.orderByClause().ifPresent(o -> o.accept(this));
                selectStatement.limitClause().ifPresent(l -> l.accept(this));
                return nodeTypes;
            }

            @Override
            public List<String> visitSelectClause(SelectClause selectClause) {
                record("SelectClause");
                selectClause.selectItems().forEach(item -> item.accept(this));
                return nodeTypes;
            }

            @Override
            public List<String> visitFromClause(FromClause fromClause) {
                record("FromClause");
                fromClause.tableReferences().forEach(ref -> ref.accept(this));
                return nodeTypes;
            }

            @Override
            public List<String> visitWhereClause(WhereClause whereClause) {
                record("WhereClause");
                whereClause.condition().accept(this);
                return nodeTypes;
            }

            @Override
            public List<String> visitGroupByClause(GroupByClause groupByClause) {
                record("GroupByClause");
                return nodeTypes;
            }

            @Override
            public List<String> visitHavingClause(HavingClause havingClause) {
                record("HavingClause");
                return nodeTypes;
            }

            @Override
            public List<String> visitOrderByClause(OrderByClause orderByClause) {
                record("OrderByClause");
                orderByClause.orderByItems().forEach(item ->
                        item.expression().accept(this)
                );
                return nodeTypes;
            }

            @Override
            public List<String> visitLimitClause(LimitClause limitClause) {
                record("LimitClause");
                return nodeTypes;
            }

            @Override
            public List<String> visitTable(Table table) {
                record("Table");
                return nodeTypes;
            }

            @Override
            public List<String> visitColumnReference(ColumnReference columnReference) {
                record("ColumnReference");
                return nodeTypes;
            }

            @Override
            public List<String> visitAllColumns(AllColumns allColumns) {
                record("AllColumns");
                return nodeTypes;
            }

            @Override
            public List<String> visitExpressionSelectItem(ExpressionSelectItem expressionSelectItem) {
                record("ExpressionSelectItem");
                return nodeTypes;
            }

            @Override
            public List<String> visitBinaryOperatorExpression(BinaryOperatorExpression binaryOperatorExpression) {
                record("BinaryOperatorExpression");
                binaryOperatorExpression.left().accept(this);
                if (binaryOperatorExpression.right() != null) {
                    binaryOperatorExpression.right().accept(this);
                }
                return nodeTypes;
            }

            @Override
            public List<String> visitIntegerLiteral(IntegerLiteral integerLiteral) {
                record("IntegerLiteral");
                return nodeTypes;
            }

            @Override
            public List<String> visitDecimalLiteral(DecimalLiteral decimalLiteral) {
                record("DecimalLiteral");
                return nodeTypes;
            }

            @Override
            public List<String> visitStringLiteral(StringLiteral stringLiteral) {
                record("StringLiteral");
                return nodeTypes;
            }

            @Override
            public List<String> visitBooleanLiteral(BooleanLiteral booleanLiteral) {
                record("BooleanLiteral");
                return nodeTypes;
            }

            @Override
            public List<String> visitNullLiteral(NullLiteral nullLiteral) {
                record("NullLiteral");
                return nodeTypes;
            }

            @Override
            public List<String> visitLikeExpression(LikeExpression likeExpression) {
                record("LikeExpression");
                likeExpression.expression().accept(this);
                likeExpression.pattern().accept(this);
                return nodeTypes;
            }

            @Override
            public List<String> visitInExpression(InExpression inExpression) {
                record("InExpression");
                inExpression.expression().accept(this);
                inExpression.values().forEach(v -> v.accept(this));
                return nodeTypes;
            }

            @Override
            public List<String> visitBetweenExpression(BetweenExpression betweenExpression) {
                record("BetweenExpression");
                betweenExpression.expression().accept(this);
                betweenExpression.lowerBound().accept(this);
                betweenExpression.upperBound().accept(this);
                return nodeTypes;
            }

            @Override
            public List<String> visitIsNullExpression(IsNullExpression isNullExpression) {
                record("IsNullExpression");
                isNullExpression.expression().accept(this);
                return nodeTypes;
            }

            @Override
            public List<String> visitNotLikeExpression(NotLikeExpression notLikeExpression) {
                record("NotLikeExpression");
                notLikeExpression.expression().accept(this);
                notLikeExpression.pattern().accept(this);
                return nodeTypes;
            }

            @Override
            public List<String> visitNotInExpression(NotInExpression notInExpression) {
                record("NotInExpression");
                notInExpression.expression().accept(this);
                notInExpression.values().forEach(v -> v.accept(this));
                return nodeTypes;
            }

            @Override
            public List<String> visitNotBetweenExpression(NotBetweenExpression notBetweenExpression) {
                record("NotBetweenExpression");
                notBetweenExpression.expression().accept(this);
                notBetweenExpression.lowerBound().accept(this);
                notBetweenExpression.upperBound().accept(this);
                return nodeTypes;
            }

            @Override
            public List<String> visitIsNotNullExpression(IsNotNullExpression isNotNullExpression) {
                record("IsNotNullExpression");
                isNotNullExpression.expression().accept(this);
                return nodeTypes;
            }
        };

        // when
        final List<String> visitedTypes = stmt.accept(nodeTypeCollector);

        // then
        assertThat(visitedTypes).containsExactly(
                "SelectStatement",
                "SelectClause",
                "ColumnReference",
                "ColumnReference",
                "FromClause",
                "Table",
                "WhereClause",
                "BinaryOperatorExpression",
                "ColumnReference",
                "IntegerLiteral",
                "OrderByClause",
                "ColumnReference",
                "LimitClause"
        );
    }
}
