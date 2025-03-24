package com.user.server.series.service;


import com.user.server.product.dto.ProductResponseDto;
import com.user.server.product.entity.Product;
import com.user.server.series.entity.ProductSeries;
import com.user.server.series.entity.ProductSeriesMapping;

import com.user.server.series.repository.SeriesMappingRepository;
import com.user.server.series.repository.SeriesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SeriesServiceTest {

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private SeriesMappingRepository mappingRepository;

    @InjectMocks
    private SeriesService seriesService;

    public SeriesServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("특정 시리즈 ID로 제품들을 조회하면 ProductResponseDto 리스트를 반환해야 한다.")
    void getProductsBySeriesId_ShouldReturnProductDtos() {
        Long seriesId = 1L;
        Product product = Product.builder().id(1L).defaultName("ProductA").productCode("P001").productType("GENERAL").build();
        ProductSeriesMapping mapping = ProductSeriesMapping.builder().product(product).build();

        when(mappingRepository.findBySeriesId(seriesId)).thenReturn(List.of(mapping));

        List<ProductResponseDto> result = seriesService.getProductsBySeries(seriesId);
        assertEquals(1, result.size());
        assertEquals("ProductA", result.get(0).getDefaultName());
    }
}