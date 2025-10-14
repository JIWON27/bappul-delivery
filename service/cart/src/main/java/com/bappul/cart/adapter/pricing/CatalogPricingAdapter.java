package com.bappul.cart.adapter.pricing;

import com.bappul.cart.adapter.pricing.request.PricingInternalRequest;
import com.bappul.cart.adapter.pricing.response.PricingInternalResponse;
import com.bappul.cart.port.PricingPort;
import com.bappul.cart.web.v1.request.CartItemRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogPricingAdapter implements PricingPort {

  private final CatalogClient catalogClient;

  @Override
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 3000))
  public PricingInternalResponse getQuote(Long storeId, List<CartItemRequest> items) {
    PricingInternalRequest internalRequest = PricingInternalRequest.builder()
        .storeId(storeId)
        .items(items)
        .build();

     return catalogClient.calculate(internalRequest);
  }
}
