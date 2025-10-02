package com.moduda.delivery.catalog.domain.repository;

import com.moduda.delivery.catalog.domain.entity.Menu;
import com.moduda.delivery.catalog.domain.entity.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
  List<Menu> findAllByStore(Store store);
  Boolean existsByIdAndStore(Long menuId, Store store);

}
