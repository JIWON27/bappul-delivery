package com.bappul.order.adapter;

import com.bappul.order.adapter.request.PricingInternalRequest;
import com.bappul.order.adapter.response.PricingInternalResponse;
import com.bappul.order.adapter.response.StoreLocationResponse;
import com.bappul.order.port.CatalogPort;
import com.bappul.order.web.v1.request.OrderItemRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogAdapter implements CatalogPort {

  private final CatalogClient catalogClient;

  @Override
  public PricingInternalResponse getQuote(Long storeId, List<OrderItemRequest> items) {
    PricingInternalRequest request = PricingInternalRequest.builder()
        .storeId(storeId)
        .items(items)
        .build();
    return catalogClient.calculate(request);
  }

  @Override
  @Cacheable(value = "storeLocation", key = "#storeId")
  public StoreLocationResponse getLocation(Long storeId) {
    return catalogClient.getLocation(storeId);
  }
}
