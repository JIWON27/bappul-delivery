package com.bappul.catalog.domain.repository;

import com.bappul.catalog.domain.entity.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {

}
