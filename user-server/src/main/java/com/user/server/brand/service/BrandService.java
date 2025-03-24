package com.user.server.brand.service;

import com.user.server.brand.entity.ProductBrand;
import com.user.server.brand.repository.BrandRepository;
import com.user.server.product.dto.ProductResponseDto;
import com.user.server.product.entity.Product;
import com.user.server.product.respository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository productBrandRepository;
    private final ProductRepository productRepository;

    public List<ProductResponseDto> getProductsByBrand(Long brandId) {
        List<Product> products = productRepository.findByBrandId(brandId);
        return products.stream()
                .map(ProductResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<ProductBrand> getAllBrands() {
        return productBrandRepository.findAll();
    }
}
