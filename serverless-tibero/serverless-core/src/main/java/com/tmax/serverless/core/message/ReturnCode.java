package com.tmax.serverless.core.message;

public enum ReturnCode {
  SUCCESS(0),
  FAIL(-1);

  final int value;

  ReturnCode(int value) {
    this.value = value;
  }
}

