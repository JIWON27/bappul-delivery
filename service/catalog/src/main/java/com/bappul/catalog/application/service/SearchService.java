package com.bappul.catalog.application.service;

import com.bappul.catalog.domain.entity.Store;
import com.bappul.catalog.domain.repository.StoreSearchViewRepository;
import com.bappul.catalog.domain.repository.StoreSearchViewRepository.StoreSearchResult;
import com.bappul.catalog.web.v1.request.store.StoreSearchCondition;
import com.bappul.catalog.web.v1.response.search.StoreSearchResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import response.CursorResponse;

@Service
@RequiredArgsConstructor
public class SearchService {

  private final StoreSearchViewRepository storeSearchViewRepository;
  private final StoreImageService storeImageService;

  @Transactional(readOnly = true)
  public CursorResponse<StoreSearchResponse> search(StoreSearchCondition condition) {
    StoreSearchResult storeSearchResult = storeSearchViewRepository.findStoresWithCondition(condition);

    List<Store> stores = storeSearchResult.content();
    List<StoreSearchResponse> responses = new ArrayList<>();
    for (Store store : stores) {
      String imageUrl = storeImageService.getStoreImageUrl(store.getId());
      StoreSearchResponse response = StoreSearchResponse.from(store, imageUrl);
      responses.add(response);
    }

    return CursorResponse.of(responses, storeSearchResult.nextCursor(), responses.size(), storeSearchResult.hasNext());
  }
}
