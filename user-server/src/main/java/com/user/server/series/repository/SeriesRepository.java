package com.user.server.series.repository;


import com.user.server.series.entity.ProductSeries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesRepository extends JpaRepository<ProductSeries, Long> {
}