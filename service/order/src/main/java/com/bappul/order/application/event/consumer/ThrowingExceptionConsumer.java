package com.bappul.order.application.event.consumer;

@FunctionalInterface
public interface ThrowingExceptionConsumer<T> {
  void accept(T t) throws Exception;
}
