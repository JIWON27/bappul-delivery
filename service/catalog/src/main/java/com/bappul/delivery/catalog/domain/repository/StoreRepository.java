package com.bappul.delivery.catalog.domain.repository;

import com.bappul.delivery.catalog.domain.entity.Category;
import com.bappul.delivery.catalog.domain.entity.Store;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
  boolean existsByCategory(Category category);
  List<Store> findAllByIdIn(List<Long> storeIds);
  Optional<Store> findByIdAndUserId(Long storeId, Long userId);

}
