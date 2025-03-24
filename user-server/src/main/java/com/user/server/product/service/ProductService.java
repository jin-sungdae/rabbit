package com.user.server.product.service;

import com.user.server.product.dto.ProductResponseDto;
import com.user.server.product.respository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponseDto> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrue()
                .stream().map(ProductResponseDto::from).collect(Collectors.toList());
    }

    public List<ProductResponseDto> getLatestProducts() {
        return productRepository.findTop10ByOrderByCreatedAtDesc()
                .stream().map(ProductResponseDto::from).collect(Collectors.toList());
    }

    public List<ProductResponseDto> getDiscountedProducts() {
        return productRepository.findDiscountedProducts()
                .stream().map(ProductResponseDto::from).collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductsByTag(String tag) {
        return productRepository.findByTag(tag)
                .stream().map(ProductResponseDto::from).collect(Collectors.toList());
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream().map(ProductResponseDto::from).collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductsByBrand(Long brandId) {
        return productRepository.findByBrandId(brandId).stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }
}
