package com.jaeyeonling.lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Whitespace 테스트")
class WhitespaceTest {

    @ParameterizedTest
    @ValueSource(chars = {' ', '\t', '\n', '\r'})
    @DisplayName("공백 문자는 유효하다")
    void testValidWhitespace(final char c) {
        // when
        Whitespace ws = new Whitespace(c);

        // then
        assertThat(ws.value()).isEqualTo(c);
    }

    @ParameterizedTest
    @ValueSource(chars = {'a', '1', '!'})
    @DisplayName("공백이 아닌 문자는 예외를 발생시킨다")
    void testInvalidWhitespace(final char c) {
        assertThatThrownBy(() -> new Whitespace(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효한 공백 문자가 아닙니다");
    }

    @Test
    @DisplayName("isWhitespace 정적 메서드를 테스트한다")
    void testIsWhitespaceMethod() {
        assertThat(Whitespace.isWhitespace(' ')).isTrue();
        assertThat(Whitespace.isWhitespace('\t')).isTrue();
        assertThat(Whitespace.isWhitespace('\n')).isTrue();
        assertThat(Whitespace.isWhitespace('\r')).isTrue();
        assertThat(Whitespace.isWhitespace('a')).isFalse();
    }

    @Test
    @DisplayName("isNotWhitespace 정적 메서드를 테스트한다")
    void testIsNotWhitespaceMethod() {
        assertThat(Whitespace.isNotWhitespace('a')).isTrue();
        assertThat(Whitespace.isNotWhitespace(' ')).isFalse();
    }
}
