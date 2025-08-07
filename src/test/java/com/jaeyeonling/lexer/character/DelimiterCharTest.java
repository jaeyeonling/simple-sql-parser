package com.jaeyeonling.lexer.character;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DelimiterChar 테스트")
class DelimiterCharTest {

    @ParameterizedTest
    @ValueSource(chars = {'(', ')', ',', '.'})
    @DisplayName("유효한 구분자 문자이다")
    void testValidDelimiterChar(final char c) {
        // when
        final DelimiterChar dc = new DelimiterChar(c);

        // then
        assertThat(dc.value()).isEqualTo(c);
    }

    @ParameterizedTest
    @ValueSource(chars = {'a', '1', '!', ' '})
    @DisplayName("유효하지 않은 구분자 문자는 예외를 발생시킨다")
    void testInvalidDelimiterChar(final char c) {
        assertThatThrownBy(() -> new DelimiterChar(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효한 구분자가 아닙니다");
    }

    @Test
    @DisplayName("isDelimiterChar 정적 메서드를 테스트한다")
    void testIsDelimiterCharMethod() {
        assertThat(DelimiterChar.isDelimiterChar('(')).isTrue();
        assertThat(DelimiterChar.isDelimiterChar(',')).isTrue();
        assertThat(DelimiterChar.isDelimiterChar('a')).isFalse();
    }

    @Test
    @DisplayName("isNotDelimiterChar 정적 메서드를 테스트한다")
    void testIsNotDelimiterCharMethod() {
        assertThat(DelimiterChar.isNotDelimiterChar('a')).isTrue();
        assertThat(DelimiterChar.isNotDelimiterChar('(')).isFalse();
    }
}
