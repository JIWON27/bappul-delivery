package com.moduda.delivery.catalog.domain.repository;

import com.moduda.delivery.catalog.domain.entity.MenuOptionGroup;
import com.moduda.delivery.catalog.domain.entity.MenuOptionValue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuOptionValueRepository extends JpaRepository<MenuOptionValue, Long> {
  List<MenuOptionValue> findAllByMenuOptionGroup(MenuOptionGroup menuOptionGroup);

}
