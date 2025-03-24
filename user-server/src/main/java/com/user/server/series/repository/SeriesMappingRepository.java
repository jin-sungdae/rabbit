package com.user.server.series.repository;


import com.user.server.series.entity.ProductSeries;
import com.user.server.series.entity.ProductSeriesMapping;
import com.user.server.series.entity.id.ProductSeriesMappingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeriesMappingRepository extends JpaRepository<ProductSeriesMapping, ProductSeriesMappingId> {
    List<ProductSeriesMapping> findBySeriesIdOrderBySortOrder(Long seriesId);

    List<ProductSeriesMapping> findBySeriesId(Long seriesId);
}