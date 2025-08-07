package com.jaeyeonling.ast;

import com.jaeyeonling.lexer.Token;

/**
 * 소스 코드에서의 위치 정보를 나타내는 불변 클래스.
 * 에러 메시지나 디버깅에 유용한 위치 정보를 제공합니다.
 */
public record SourceLocation(
        int line,
        int column,
        int startIndex,
        int endIndex
) {

    public static final SourceLocation UNKNOWN = new SourceLocation(-1, -1, -1, -1);

    /**
     * 토큰으로부터 SourceLocation을 생성합니다.
     */
    public SourceLocation(final Token token) {
        this(
                token.line(),
                token.column(),
                token.startIndex(),
                token.endIndex()
        );
    }

    /**
     * 두 위치를 합쳐서 새로운 위치를 생성합니다.
     * 시작 위치는 더 앞선 위치를, 끝 위치는 더 뒤의 위치를 사용합니다.
     *
     * @param other 합칠 다른 위치
     * @return 합쳐진 새로운 위치
     */
    public SourceLocation merge(final SourceLocation other) {
        if (this == UNKNOWN) return other;
        if (other == UNKNOWN) return this;

        final int newStartIndex = Math.min(this.startIndex, other.startIndex);
        final int newEndIndex = Math.max(this.endIndex, other.endIndex);
        final int newLine = this.startIndex <= other.startIndex ? this.line : other.line;
        final int newColumn = this.startIndex <= other.startIndex ? this.column : other.column;

        return new SourceLocation(newLine, newColumn, newStartIndex, newEndIndex);
    }

    @Override
    public String toString() {
        if (this == UNKNOWN) {
            return "Unknown location";
        }
        return String.format("Line %d, Column %d", line, column);
    }
}
