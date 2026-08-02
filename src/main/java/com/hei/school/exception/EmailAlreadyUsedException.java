package com.hei.school.exception;

public class EmailAlreadyUsedException extends RuntimeException {
  public EmailAlreadyUsedException(String message) {
    super(message);
  }
}
