package com.bappul.delivery.domain.repository.geo;

import com.bappul.delivery.domain.entity.geo.LegalDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalDongRepository extends JpaRepository<LegalDong, Long> {

}
