package com.github.hdurix.nbaguesser.domain;

import com.github.hdurix.nbaguesser.error.domain.MissingMandatoryValueException;
import com.github.hdurix.nbaguesser.error.domain.NumberValueTooLowException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RandomizerUnitTest {
  @ParameterizedTest
  @ValueSource(ints = {2, 5, 8} )
  void shouldNotRandomizeLessThanTen(int number) {
    assertThatThrownBy(() -> new Randomizer().tenOutOf(number))
      .isInstanceOf(NumberValueTooLowException.class)
      .hasMessageContaining("max")
      .hasMessageContaining("at least 10")
      .hasMessageContaining("was " + number);
  }

  @ParameterizedTest
  @ValueSource(ints = {10, 20, 30} )
  void shouldRandomize(int numbers) {
    int[] randomNumbers = new Randomizer().tenOutOf(numbers);

    assertThat(randomNumbers)
      .hasSize(10)
      .isNotEqualTo(new Randomizer().tenOutOf(numbers));
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldNotGetOneOfEmptyCollection(List<Integer> collection) {
    assertThatThrownBy(() -> new Randomizer().oneOf(collection))
      .isInstanceOf(MissingMandatoryValueException.class)
      .hasMessageContaining("collection");
  }

  @Test
  void shouldGetRandomElement() {
    Collection<Integer> numbers = List.of(0, 1,2,3,4,5);

    int randomNumber = new Randomizer().oneOf(numbers);

    assertThat(numbers).contains(randomNumber);
  }
}
