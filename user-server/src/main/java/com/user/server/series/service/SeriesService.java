package com.user.server.series.service;

import com.user.server.product.dto.ProductResponseDto;
import com.user.server.series.dto.SeriesDto;

import com.user.server.series.entity.ProductSeriesMapping;
import com.user.server.series.repository.SeriesMappingRepository;
import com.user.server.series.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository productSeriesRepository;
    private final SeriesMappingRepository mappingRepository;

    public List<SeriesDto> getAllSeries() {
        return productSeriesRepository.findAll().stream()
                .map(SeriesDto::from)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductsBySeries(Long seriesId) {
        List<ProductSeriesMapping> mappings = mappingRepository.findBySeriesId(seriesId);

        return mappings.stream()
                .map(ProductSeriesMapping::getProduct)
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }
}
