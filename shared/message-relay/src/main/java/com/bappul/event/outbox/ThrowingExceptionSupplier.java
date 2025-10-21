package com.bappul.event.outbox;

@FunctionalInterface
public interface ThrowingExceptionSupplier<T> {
  T get() throws Exception;
}
