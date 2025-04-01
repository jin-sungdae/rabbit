package com.user.server.store.controller;

import com.common.config.api.constant.ErrorCode;
import com.common.config.api.controller.APIExceptionHandler;
import com.common.config.api.exception.ProductNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.server.brand.entity.ProductBrand;
import com.user.server.file.dto.FileDto;
import com.user.server.product.controller.ProductController;
import com.user.server.product.dto.ResponseProductDto;
import com.user.server.product.service.ProductService;
import com.user.server.store.service.StoreProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(APIExceptionHandler.class)
class StoreProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreProductService storeProductService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     *  /api/v1/store/{storeId}/products/register GET 요청에 대한 테스트
     * @throws Exception
     */


    /**
     *  /api/v1/store/{storeId}/products/all GET 요청에 대한 테스트
     * @throws Exception
     */




    /**
     *  /api/v1/store/{storeId}/products/{productId} GET 요청에 대한 테스트
     * @throws Exception
     */
    @Test
    @DisplayName("존재하는 productId 에 맞춰서 존재하는 상품을 조회")
    void test_putProduct() throws Exception {
        // given
        ResponseProductDto responseProductDto = ResponseProductDto.builder()
                .productId(1L)
                .defaultName("테스트 상품")
                .productCode("10000-122")
                .productType("PRODUCT")
                .isFeatured(true)
                .productBrand(ProductBrand.builder().brandName("테스트 브랜드").build())
                .isActive(true)
                .mainImage(FileDto.builder().id(1L).fileName("test.jpg").fileUrl("http://test.com/test.jpg").build())
                .build();

        given(storeProductService.getProductByProductId(1L)).willReturn(responseProductDto);

        // when & then
        mockMvc.perform(get("/api/v1/products/1")
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(1L))
                .andExpect(jsonPath("$.data.defaultName").value("테스트 상품"))
                .andExpect(jsonPath("$.data.productCode").value("10000-122"))
                .andExpect(jsonPath("$.data.productType").value("PRODUCT"))
                .andExpect(jsonPath("$.data.isFeatured").value(true))
                .andExpect(jsonPath("$.data.productBrand.brandName").value("테스트 브랜드"))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.mainImage.id").value(1L))
                .andExpect(jsonPath("$.data.mainImage.fileName").value("test.jpg"))
                .andExpect(jsonPath("$.data.mainImage.fileUrl").value("http://test.com/test.jpg"));
    }

    @Test
    @DisplayName("[에러 테스트] 상품이 존재는 하지만 비활성화 상태")
    void test_putProduct_Inactive() throws Exception {
        // given
        ResponseProductDto responseProductDto = ResponseProductDto.builder()
                .productId(1L)
                .defaultName("테스트 상품")
                .productCode("10000-122")
                .productType("PRODUCT")
                .isFeatured(true)
                .productBrand(ProductBrand.builder().brandName("테스트 브랜드").build())
                .isActive(false)
                .mainImage(FileDto.builder().id(1L).fileName("test.jpg").fileUrl("http://test.com/test.jpg").build())
                .build();

        given(storeProductService.getProductByProductId(1L)).willReturn(responseProductDto);

        // when & then
        mockMvc.perform(get("/api/v1/products/1")
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.PRODUCT_INACTIVE.getCode()));
    }

    @Test
    @DisplayName("[에러 테스트] PrincipalDetails이 존재하지 않을때 401권한 에러 발생")
    void test_putProduct_Unauthorized() throws Exception {
        // given
        ResponseProductDto responseProductDto = ResponseProductDto.builder()
                .productId(1L)
                .defaultName("테스트 상품")
                .productCode("10000-122")
                .productType("PRODUCT")
                .isFeatured(true)
                .productBrand(ProductBrand.builder().brandName("테스트 브랜드").build())
                .isActive(true)
                .mainImage(FileDto.builder().id(1L).fileName("test.jpg").fileUrl("http://test.com/test.jpg").build())
                .build();

        given(storeProductService.getProductByProductId(1L)).willReturn(responseProductDto);

        // when & then
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[에러 테스트] 존재하지 않는 productId 에 맞춰서 상품을 조회하면 404 응답")
    void test_putProduct_NotFound() throws Exception {
        // given
        given(storeProductService.getProductByProductId(999L))
                .willThrow(new ProductNotFoundException());

        // when & then
        mockMvc.perform(get("/api/v1/stores/{storeId}/products/{productId}", 1L, 999L)
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isNotFound());
    }
}