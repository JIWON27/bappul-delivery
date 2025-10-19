package com.bappul.order.adapter;

import com.bappul.order.adapter.request.PricingInternalRequest;
import com.bappul.order.adapter.response.PricingInternalResponse;
import com.bappul.order.port.CatalogQuotePort;
import com.bappul.order.web.v1.request.OrderItemRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogQuoteAdapter implements CatalogQuotePort {

  private final CatalogClient catalogClient;

  @Override
  public PricingInternalResponse getQuote(Long storeId, List<OrderItemRequest> items) {
    PricingInternalRequest request = PricingInternalRequest.builder()
        .storeId(storeId)
        .items(items)
        .build();
    return catalogClient.calculate(request);
  }
}
