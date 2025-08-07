package com.jaeyeonling.ast.table;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 단일 테이블을 나타내는 AST 노드.
 */
public final class Table implements TableReference {

    private final String name;

    @Nullable
    private final String alias;
    private final SourceLocation location;

    public Table(
            final String name,
            @Nullable final String alias,
            final SourceLocation location
    ) {
        this.name = name;
        this.alias = alias;
        this.location = location;
    }

    public Table(
            final String name,
            final SourceLocation location
    ) {
        this(name, null, location);
    }

    @Override
    public Optional<String> alias() {
        return Optional.ofNullable(alias);
    }

    @Override
    public TableReferenceType tableReferenceType() {
        return TableReferenceType.TABLE;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitTable(this);
    }

    @Override
    public SourceLocation location() {
        return location;
    }

    public String name() {
        return name;
    }
}
