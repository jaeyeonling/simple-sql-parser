package com.jaeyeonling.lang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Alpha 테스트")
class AlphaTest {

    @ParameterizedTest
    @ValueSource(chars = {'a', 'b', 'z', 'A', 'B', 'Z'})
    @DisplayName("a-z, A-Z는 유효한 알파벳이다")
    void testValidAlpha(final char c) {
        // when
        Alpha alpha = new Alpha(c);

        // then
        assertThat(alpha.value()).isEqualTo(c);
    }

    @ParameterizedTest
    @ValueSource(chars = {'1', '!', ' ', '@', '가'})
    @DisplayName("알파벳이 아닌 문자는 예외를 발생시킨다")
    void testInvalidAlpha(final char c) {
        assertThatThrownBy(() -> new Alpha(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효한 알파벳이 아닙니다");
    }

    @Test
    @DisplayName("대문자 확인 메서드를 테스트한다")
    void testIsUpperCase() {
        assertThat(new Alpha('A').isUpperCase()).isTrue();
        assertThat(new Alpha('a').isUpperCase()).isFalse();
    }

    @Test
    @DisplayName("소문자 확인 메서드를 테스트한다")
    void testIsLowerCase() {
        assertThat(new Alpha('a').isLowerCase()).isTrue();
        assertThat(new Alpha('A').isLowerCase()).isFalse();
    }


    @Test
    @DisplayName("isAlpha 정적 메서드를 테스트한다")
    void testIsAlphaMethod() {
        assertThat(Alpha.isAlpha('a')).isTrue();
        assertThat(Alpha.isAlpha('Z')).isTrue();
        assertThat(Alpha.isAlpha('5')).isFalse();
    }

    @Test
    @DisplayName("isNotAlpha 정적 메서드를 테스트한다")
    void testIsNotAlphaMethod() {
        assertThat(Alpha.isNotAlpha('5')).isTrue();
        assertThat(Alpha.isNotAlpha('a')).isFalse();
    }
}
