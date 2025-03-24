package com.user.server.product.controller;

import com.common.config.api.apidto.APIDataResponse;
import com.user.server.product.dto.ProductResponseDto;
import com.user.server.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/featured")
    public APIDataResponse<List<ProductResponseDto>> getFeaturedProducts() {
        List<ProductResponseDto> featuredProducts = productService.getFeaturedProducts();

        return APIDataResponse.of(featuredProducts);
    }

    @GetMapping("/latest")
    public APIDataResponse<List<ProductResponseDto>> getLatestProducts() {
        List<ProductResponseDto> latestProducts = productService.getLatestProducts();

        return APIDataResponse.of(latestProducts);
    }

    @GetMapping("/discounted")
    public APIDataResponse<List<ProductResponseDto>> getDiscountedProducts() {

        List<ProductResponseDto> discountedProducts = productService.getDiscountedProducts();


        return APIDataResponse.of(discountedProducts);
    }

    @GetMapping
    public List<ProductResponseDto> getProductsByTag(@RequestParam(required = false) String tag) {
        if (tag != null) {
            return productService.getProductsByTag(tag);
        }
        return productService.getAllProducts();
    }

    @GetMapping("/brands/{brandId}/products")
    public List<ProductResponseDto> getProductsByBrand(@PathVariable Long brandId) {
        return productService.getProductsByBrand(brandId);
    }
}
