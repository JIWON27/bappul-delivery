package com.bappul.catalog.application.service;

import com.bappul.catalog.domain.repository.StoreSearchViewRepository;
import com.bappul.catalog.domain.repository.StoreSearchViewRepository.StoreSearchResult;
import com.bappul.catalog.web.v1.request.store.StoreSearchCondition;
import com.bappul.catalog.web.v1.response.search.StoreSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import response.CursorResponse;

@Service
@RequiredArgsConstructor
public class SearchService {

  private final StoreSearchViewRepository storeSearchViewRepository;

  @Transactional(readOnly = true)
  public CursorResponse<StoreSearchResponse> search(StoreSearchCondition condition) {
    StoreSearchResult storeSearchResult = storeSearchViewRepository.findStoresWithCondition(condition);

    List<StoreSearchResponse> responses = storeSearchResult.content().stream()
        .map(StoreSearchResponse::from)
        .toList();

    return CursorResponse.of(responses, storeSearchResult.nextCursor(), responses.size(), storeSearchResult.hasNext());
  }
}
