package com.bappul.delivery.catalog.domain.repository;

import com.bappul.delivery.catalog.domain.entity.Menu;
import com.bappul.delivery.catalog.domain.entity.MenuOptionGroup;
import com.bappul.delivery.catalog.domain.entity.MenuOptionValue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuOptionValueRepository extends JpaRepository<MenuOptionValue, Long> {
  @Query("""
    select v
    from MenuOptionValue v
    join fetch v.menuOptionGroup g
    where g.menu = :menu
  """)
  List<MenuOptionValue> findAllByMenuWithGroup(@Param("menu") Menu menu);
  List<MenuOptionValue> findAllByMenuOptionGroup(MenuOptionGroup menuOptionGroup);

}
