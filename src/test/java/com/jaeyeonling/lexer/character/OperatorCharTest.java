package com.jaeyeonling.lexer.character;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OperatorChar 테스트")
class OperatorCharTest {

    @ParameterizedTest
    @ValueSource(chars = {'=', '!', '<', '>', '+', '-', '*', '/'})
    @DisplayName("유효한 연산자 문자이다")
    void testValidOperatorChar(final char c) {
        // when
        final OperatorChar oc = new OperatorChar(c);

        // then
        assertThat(oc.value()).isEqualTo(c);
    }

    @ParameterizedTest
    @ValueSource(chars = {'a', '1', '@', '&'})
    @DisplayName("유효하지 않은 연산자 문자는 예외를 발생시킨다")
    void testInvalidOperatorChar(final char c) {
        assertThatThrownBy(() -> new OperatorChar(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효한 연산자 문자가 아닙니다");
    }

    @Test
    @DisplayName("isOperatorChar 정적 메서드를 테스트한다")
    void testIsOperatorCharMethod() {
        assertThat(OperatorChar.isOperatorChar('=')).isTrue();
        assertThat(OperatorChar.isOperatorChar('+')).isTrue();
        assertThat(OperatorChar.isOperatorChar('a')).isFalse();
    }

    @Test
    @DisplayName("isNotOperatorChar 정적 메서드를 테스트한다")
    void testIsNotOperatorCharMethod() {
        assertThat(OperatorChar.isNotOperatorChar('a')).isTrue();
        assertThat(OperatorChar.isNotOperatorChar('=')).isFalse();
    }
}
