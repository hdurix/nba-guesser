package com.github.hdurix.nbaguesser.domain;

import com.github.hdurix.nbaguesser.error.domain.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Randomizer {
  public int[] tenOutOf(int max) {
    Assert.field("max", max).min(10);

    return shuffle(IntStream.range(0, max)).stream().limit(10).mapToInt(Integer::intValue).toArray();
  }

  public int oneOf(int[] numbers) {
    Assert.notNull("numbers", numbers);
    Assert.notEmpty("numbers", Arrays.stream(numbers).boxed().toList());

    return shuffle(Arrays.stream(numbers)).get(0);
  }

  private static List<Integer> shuffle(IntStream numbers) {
    List<Integer> allNumbers = new ArrayList<>(numbers.boxed().toList());
    Collections.shuffle(allNumbers);
    return allNumbers;
  }
}
