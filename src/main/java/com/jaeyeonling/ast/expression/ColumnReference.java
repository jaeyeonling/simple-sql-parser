package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 컬럼 참조를 나타내는 AST 노드.
 * 예: column, table.column
 */
public final class ColumnReference implements Expression, SelectItem {

    @Nullable
    private final String tableName;
    private final String columnName;

    @Nullable
    private final String alias;
    private final SourceLocation location;

    public ColumnReference(
            @Nullable final String tableName,
            final String columnName,
            @Nullable final String alias,
            final SourceLocation location
    ) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.alias = alias;
        this.location = location;
    }

    public ColumnReference(
            final String columnName,
            final SourceLocation location
    ) {
        this(null, columnName, null, location);
    }

    public static ColumnReference of(
            final String tableName,
            final String columnName,
            final SourceLocation location
    ) {
        return new ColumnReference(tableName, columnName, null, location);
    }

    public static ColumnReference withAlias(
            final String columnName,
            final String alias,
            final SourceLocation location
    ) {
        return new ColumnReference(null, columnName, alias, location);
    }

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.COLUMN;
    }

    @Override
    public SelectItemType selectItemType() {
        return SelectItemType.COLUMN;
    }

    @Override
    public Optional<String> alias() {
        return Optional.ofNullable(alias);
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitColumnReference(this);
    }

    @Override
    public SourceLocation location() {
        return location;
    }

    public Optional<String> tableName() {
        return Optional.ofNullable(tableName);
    }

    public String columnName() {
        return columnName;
    }
}
