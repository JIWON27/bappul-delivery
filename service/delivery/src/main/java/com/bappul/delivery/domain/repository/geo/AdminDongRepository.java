package com.bappul.delivery.domain.repository.geo;

import com.bappul.delivery.domain.entity.geo.AdminDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminDongRepository extends JpaRepository<AdminDong, Long> {


  @Query(value = """
    SELECT admin_code
    FROM admin_dong
    WHERE ST_Contains(
            polygon,
            ST_SRID(POINT(:lng, :lat), 4326)
          )
    LIMIT 1
    """, nativeQuery = true)
  String findAdminCodeByGPS(@Param("lng") double lng, @Param("lat") double lat);

}
