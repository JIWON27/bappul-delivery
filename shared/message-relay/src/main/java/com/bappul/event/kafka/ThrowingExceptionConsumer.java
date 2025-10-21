package com.bappul.event.kafka;

@FunctionalInterface
public interface ThrowingExceptionConsumer<T> {
  void accept(T t) throws Exception;
}
