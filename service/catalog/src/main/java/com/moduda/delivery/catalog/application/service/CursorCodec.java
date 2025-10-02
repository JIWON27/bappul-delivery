package com.moduda.delivery.catalog.application.service;

import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.CURSOR_INTERNAL_ERROR;
import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.CURSOR_INVALID_FORMAT;
import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.CURSOR_PARAM_REQUIRED;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import exception.ServiceException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CursorCodec {
  private final ObjectMapper objectMapper;

  private static final String TYPE = "type";
  private static final String TYPE_LATEST = "latest";
  private static final String TYPE_MIN_ORDER = "min_order_price";

  private static final String STORE_CREATED_AT_MS = "store_created_at_ms";
  private static final String MIN_ORDER_PRICE = "min_order_price";
  private static final String STORE_ID = "store_id";

  private String encodeObjectNode(ObjectNode node) {
    try {
      String json = objectMapper.writeValueAsString(node);
      return Base64.getUrlEncoder().withoutPadding()
          .encodeToString(json.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      throw new ServiceException(CURSOR_INTERNAL_ERROR);
    }
  }

  private JsonNode decodeNodeFromCursor(String cursor) {
    try {
      String json = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
      return objectMapper.readTree(json);
    } catch (Exception e) {
      throw new ServiceException(CURSOR_INVALID_FORMAT);
    }
  }

  public String encodeLatest(LocalDateTime createdAtUtc, Long storeId) {
    if (Objects.isNull(createdAtUtc) || Objects.isNull(storeId)) {
      throw new ServiceException(CURSOR_PARAM_REQUIRED);
    }
    long epochMs = createdAtUtc.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    ObjectNode node = objectMapper.createObjectNode();
    node.put(TYPE, TYPE_LATEST);
    node.put(STORE_CREATED_AT_MS, epochMs);
    node.put(STORE_ID, storeId);

    return encodeObjectNode(node);
  }

  public Cursor<LocalDateTime> decodeLatest(String cursor) {
    if (cursor == null || cursor.isEmpty()) {
      return new Cursor<>(null, null);
    }
    JsonNode node = decodeNodeFromCursor(cursor);

    long createdAtMs = node.get(STORE_CREATED_AT_MS).asLong();
    Long storeId = node.get(STORE_ID).asLong();

    LocalDateTime createAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAtMs), ZoneId.of("UTC"));

    return new Cursor<>(createAt, storeId);
  }

  public String encodeMinOrderPrice(BigDecimal minOrderPrice, Long storeId) {
    if (Objects.isNull(minOrderPrice) || Objects.isNull(storeId)) {
      throw new ServiceException(CURSOR_PARAM_REQUIRED);
    }

    String monOrderPriceString = toDecimalString(minOrderPrice);

    ObjectNode node = objectMapper.createObjectNode();
    node.put(TYPE, TYPE_MIN_ORDER);
    node.put(MIN_ORDER_PRICE, monOrderPriceString);
    node.put(STORE_ID, storeId);

    return encodeObjectNode(node);
  }

  public Cursor<BigDecimal> decodeMinOrderPrice(String cursor) {
    if (cursor == null || cursor.isEmpty()) {
      return new Cursor<>(null, null);
    }
    JsonNode node = decodeNodeFromCursor(cursor);

    String minOrderPriceString = node.get(MIN_ORDER_PRICE).asText();
    Long storeId = node.get(STORE_ID).asLong();

    BigDecimal minOrderPrice = toDecimal(minOrderPriceString);

    return new Cursor<>(minOrderPrice, storeId);
  }

  private static String toDecimalString(BigDecimal v) {
    return v == null ? null : v.toPlainString();
  }

  private static BigDecimal toDecimal(String minOrderPrice) {
    return minOrderPrice == null ? null : new BigDecimal(minOrderPrice);
  }

  public record Cursor<T>(T sortKey, Long storeId) {}

}
