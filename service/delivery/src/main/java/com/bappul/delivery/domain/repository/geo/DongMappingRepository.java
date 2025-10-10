package com.bappul.delivery.domain.repository.geo;

import com.bappul.delivery.domain.entity.geo.DongMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DongMappingRepository extends JpaRepository<DongMapping, Long> {

}
