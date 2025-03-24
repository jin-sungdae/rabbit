package com.user.server.brand.service;

import com.user.server.product.dto.ProductResponseDto;
import com.user.server.product.entity.Product;
import com.user.server.product.respository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BrandServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BrandService brandService;

    public BrandServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("특정 브랜드 ID로 제품들을 조회하면 ProductResponseDto 리스트를 반환해야 한다.")
    void getProductsByBrand_ShouldReturnDtoList() {
        Long brandId = 1L;
        Product product = Product.builder().id(1L).defaultName("BrandX Product").build();
        when(productRepository.findByBrandId(brandId)).thenReturn(List.of(product));

        List<ProductResponseDto> result = brandService.getProductsByBrand(brandId);
        assertEquals(1, result.size());
        assertEquals("BrandX Product", result.get(0).getDefaultName());
    }
}