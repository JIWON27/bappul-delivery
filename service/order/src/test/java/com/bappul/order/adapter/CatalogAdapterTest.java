package com.bappul.order.adapter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bappul.order.adapter.response.StoreLocationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CatalogAdapterTest {

  @Autowired
  CatalogAdapter catalogAdapter;

  @MockBean
  CatalogClient catalogClient;

  @Test
  @DisplayName("첫번째 호출은 HTTP 호출되고 두번째부터는 캐시에서 읽는다")
  void shouldUseCacheAfterFirstCall() {
    /* given */
    Long storeId = 1L;
    StoreLocationResponse resp = new StoreLocationResponse(
        "도로명", "상세주소", "우편번호",
        "지번", "행정동", "법정동",
        127.0310, 37.5000);

    /* when */
    when(catalogClient.getLocation(storeId)).thenReturn(resp);

    StoreLocationResponse firstCall = catalogAdapter.getLocation(storeId);
    StoreLocationResponse secondCall = catalogAdapter.getLocation(storeId);
    StoreLocationResponse thirdCall = catalogAdapter.getLocation(storeId);

    /* then */
    assertThat(firstCall).isSameAs(secondCall).isSameAs(thirdCall);
    verify(catalogClient, times(1)).getLocation(storeId);
  }

  @Test
  @DisplayName("TTL 만료 시 캐시가 비워지고 재조회 시 HTTP 호출된다")
  void shouldCallHttpAgainAfterTtlExpires() throws InterruptedException {
    /* given */
    Long storeId = 2L;
    StoreLocationResponse resp = new StoreLocationResponse(
        "도로명", "상세주소", "우편번호",
        "지번", "행정동", "법정동",
        127.0310, 37.5000);

    /* when */
    when(catalogClient.getLocation(storeId)).thenReturn(resp);
    StoreLocationResponse firstCall = catalogAdapter.getLocation(storeId);
    StoreLocationResponse secondCall = catalogAdapter.getLocation(storeId);
    Thread.sleep(4000);
    StoreLocationResponse thirdCall = catalogAdapter.getLocation(storeId);

    /* then */
    verify(catalogClient, times(2)).getLocation(storeId);
  }
}
