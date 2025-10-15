package com.bappul.pomotion.application.event.consumer;

@FunctionalInterface
public interface ThrowingExceptionConsumer<T> {
  void accept(T t) throws Exception;
}
