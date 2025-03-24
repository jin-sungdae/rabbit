package com.user.server.brand.controller;

import com.common.config.api.apidto.APIDataResponse;
import com.user.server.brand.entity.ProductBrand;
import com.user.server.brand.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService productBrandService;

    @GetMapping
    public APIDataResponse<List<ProductBrand>> getBrands() {

        List<ProductBrand> brands = productBrandService.getAllBrands();

        return APIDataResponse.of(brands);
    }
}
