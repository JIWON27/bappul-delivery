package com.bappul.delivery.catalog.domain.repository;

import com.bappul.delivery.catalog.domain.entity.Menu;
import com.bappul.delivery.catalog.domain.entity.MenuOptionGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuOptionGroupRepository extends JpaRepository<MenuOptionGroup, Long> {
  List<MenuOptionGroup> findByMenu(Menu menu);
}
