package com.user.server.series.controller;

import com.common.config.api.apidto.APIDataResponse;
import com.user.server.product.dto.ProductResponseDto;
import com.user.server.series.dto.SeriesDto;
import com.user.server.series.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    @GetMapping
    public List<SeriesDto> getAllSeries() {
        return seriesService.getAllSeries();
    }

    @GetMapping("/{seriesId}/products")
    public APIDataResponse<List<ProductResponseDto>> getProductsBySeries(@PathVariable Long seriesId) {

        List<ProductResponseDto> products = seriesService.getProductsBySeries(seriesId);

        return APIDataResponse.of(products);
    }
}