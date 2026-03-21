package com.github.hdurix.nbaguesser.domain;

import com.github.hdurix.nbaguesser.error.domain.Assert;
import java.time.LocalDate;
import java.util.List;

public final class QuizResult {

  private final String pollId;
  private final LocalDate date;
  private final List<String> options;
  private final int correctOptionIndex;
  private final String correctAnswer;
  private final long messageId;

  private QuizResult(QuizResultBuilder builder) {
    Assert.notBlank("pollId", builder.pollId);
    Assert.notNull("date", builder.date);
    Assert.notEmpty("options", builder.options);
    Assert.notBlank("correctAnswer", builder.correctAnswer);
    this.pollId = builder.pollId;
    this.date = builder.date;
    this.options = builder.options;
    this.correctOptionIndex = builder.correctOptionIndex;
    this.correctAnswer = builder.correctAnswer;
    this.messageId = builder.messageId;
  }

  public static QuizResultBuilder builder() {
    return new QuizResultBuilder();
  }

  public String pollId() {
    return pollId;
  }

  public LocalDate date() {
    return date;
  }

  public List<String> options() {
    return options;
  }

  public int correctOptionIndex() {
    return correctOptionIndex;
  }

  public String correctAnswer() {
    return correctAnswer;
  }

  public long messageId() {
    return messageId;
  }

  public static final class QuizResultBuilder {

    private String pollId;
    private LocalDate date;
    private List<String> options;
    private int correctOptionIndex;
    private String correctAnswer;
    private long messageId;

    public QuizResultBuilder pollId(String pollId) {
      this.pollId = pollId;
      return this;
    }

    public QuizResultBuilder date(LocalDate date) {
      this.date = date;
      return this;
    }

    public QuizResultBuilder options(List<String> options) {
      this.options = options;
      return this;
    }

    public QuizResultBuilder correctOptionIndex(int correctOptionIndex) {
      this.correctOptionIndex = correctOptionIndex;
      return this;
    }

    public QuizResultBuilder correctAnswer(String correctAnswer) {
      this.correctAnswer = correctAnswer;
      return this;
    }

    public QuizResultBuilder messageId(long messageId) {
      this.messageId = messageId;
      return this;
    }

    public QuizResult build() {
      return new QuizResult(this);
    }
  }
}
