package com.user.server.tag.service;

import com.user.server.product.dto.ProductResponseDto;

import com.user.server.product.entity.Product;
import com.user.server.product.respository.ProductRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TagServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private TagService tagService;

    public TagServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("특정 태그로 제품들을 조회하면 ProductResponseDto 리스트를 반환해야 한다.")
    void getProductsByTag_ShouldReturnDtos() {
        String tag = "eco";

        Product product = Product.builder()
                .id(1L)
                .defaultName("Eco Product")
                .productCode("P001")
                .productType("GENERAL")
                .brand(null)
                .images(Collections.emptyList())
                .variants(Collections.emptyList())
                .build();

        when(productRepository.findAllByTagName(tag)).thenReturn(List.of(product));

        List<ProductResponseDto> result = tagService.getProductsByTag(tag);
        assertEquals(1, result.size());
        assertEquals("Eco Product", result.get(0).getDefaultName());
    }
}