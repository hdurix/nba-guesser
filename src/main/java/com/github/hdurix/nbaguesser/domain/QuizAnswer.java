package com.github.hdurix.nbaguesser.domain;

import com.github.hdurix.nbaguesser.error.domain.Assert;
import java.time.Instant;

public final class QuizAnswer {

  private final String pollId;
  private final long userId;
  private final String userName;
  private final int optionIndex;
  private final Instant timestamp;

  private QuizAnswer(QuizAnswerBuilder builder) {
    Assert.notBlank("pollId", builder.pollId);
    Assert.notBlank("userName", builder.userName);
    Assert.notNull("timestamp", builder.timestamp);
    this.pollId = builder.pollId;
    this.userId = builder.userId;
    this.userName = builder.userName;
    this.optionIndex = builder.optionIndex;
    this.timestamp = builder.timestamp;
  }

  public static QuizAnswerBuilder builder() {
    return new QuizAnswerBuilder();
  }

  public String pollId() {
    return pollId;
  }

  public long userId() {
    return userId;
  }

  public String userName() {
    return userName;
  }

  public int optionIndex() {
    return optionIndex;
  }

  public Instant timestamp() {
    return timestamp;
  }

  public static final class QuizAnswerBuilder {

    private String pollId;
    private long userId;
    private String userName;
    private int optionIndex;
    private Instant timestamp;

    public QuizAnswerBuilder pollId(String pollId) {
      this.pollId = pollId;
      return this;
    }

    public QuizAnswerBuilder userId(long userId) {
      this.userId = userId;
      return this;
    }

    public QuizAnswerBuilder userName(String userName) {
      this.userName = userName;
      return this;
    }

    public QuizAnswerBuilder optionIndex(int optionIndex) {
      this.optionIndex = optionIndex;
      return this;
    }

    public QuizAnswerBuilder timestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public QuizAnswer build() {
      return new QuizAnswer(this);
    }
  }
}
