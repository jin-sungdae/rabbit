package com.user.server.store.controller;

import com.common.config.api.constant.ErrorCode;
import com.common.config.api.controller.APIExceptionHandler;
import com.common.config.api.exception.ProductNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.config.security.PrincipalDetails;
import com.user.server.brand.entity.ProductBrand;
import com.user.server.file.dto.FileDto;
import com.user.server.product.controller.ProductController;
import com.user.server.product.dto.RequestProductDto;
import com.user.server.product.dto.ResponseProductDto;
import com.user.server.product.service.ProductService;
import com.user.server.store.service.StoreProductService;
import com.user.server.user.entity.Role;
import com.user.server.user.entity.User;
import com.user.test.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreProductController.class)
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
    @Test
    @DisplayName("[정상 테스트] 상품 등록 요청")
    @WithMockCustomUser(sellerId = 1L, role = Role.SELLER)
    void test_registerProduct() throws Exception {

        // given
        RequestProductDto requestProductDto = RequestProductDto.builder()
                .defaultName("테스트 상품")
                .productCode("10000-122")
                .productType("PRODUCT")
                .isFeatured(true)
                .productBrand(ProductBrand.builder().brandName("테스트 브랜드").build())
                .isActive(true)
                .build();

        given(storeProductService.registerProduct(any(RequestProductDto.class), any(User.class)))
                .willReturn("test-uid");

        // when & then
        mockMvc.perform(post("/api/v1/stores/{storeId}/products/register", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("test-uid"));
    }

    @Test
    @DisplayName("[에러 테스트] 인증 없이 상품 등록 요청 시 401")
    void test_registerProduct_unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/stores/{storeId}/products/register", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockCustomUser(role = Role.USER)
    @Test
    @DisplayName("[에러 테스트] USER가 상품 등록 요청 시 403")
    void test_registerProduct_forbiddenByRole() throws Exception {
        mockMvc.perform(post("/api/v1/stores/{storeId}/products/register", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[에러 테스트] storeId가 음수일 경우 400 BadRequest")
    @WithMockCustomUser(sellerId = 1L)
    void test_invalidStoreId() throws Exception {
        mockMvc.perform(post("/api/v1/stores/{storeId}/products/register", -1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }


    /**
     *  /api/v1/store/{storeId}/products/all GET 요청에 대한 테스트
     * @throws Exception
     */
    @WithMockCustomUser(sellerId = 1L, role = Role.SELLER)
    @Test
    @DisplayName("[정상 테스트] 판매자 본인의 상품 리스트 페이징 조회")
    void test_getAllProducts_success() throws Exception {
        // given
        List<ResponseProductDto> products = List.of(
                ResponseProductDto.builder().productId(1L).defaultName("상품1").build(),
                ResponseProductDto.builder().productId(2L).defaultName("상품2").build()
        );

        Page<ResponseProductDto> page = new PageImpl<>(products, PageRequest.of(0, 20), 2);

        given(storeProductService.getAllProducts(any(User.class), any(Pageable.class)))
                .willReturn(page);

        // when & then
        mockMvc.perform(get("/api/v1/stores/{storeId}/products/all", 1L)
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].productId").value(1L))
                .andExpect(jsonPath("$.data.content[1].defaultName").value("상품2"))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    @DisplayName("[에러 테스트] 인증 없이 전체 상품 요청 시 401")
    void test_getAllProducts_unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/stores/1/products/all"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockCustomUser(role = Role.USER, sellerId = 1L)
    @Test
    @DisplayName("[에러 테스트] USER가 상품 목록 API 접근 시 403")
    void test_getAllProducts_forbidden_role() throws Exception {
        mockMvc.perform(get("/api/v1/stores/1/products/all"))
                .andExpect(status().isForbidden());
    }

    @WithMockCustomUser(sellerId = 1L)
    @Test
    @DisplayName("[에러 테스트] 다른 상점 ID로 조회 시 403")
    void test_getAllProducts_invalidStoreId() throws Exception {
        mockMvc.perform(get("/api/v1/stores/2/products/all"))
                .andExpect(status().isForbidden());
    }


    /**
     *  /api/v1/store/{storeId}/products/{productId} GET 요청에 대한 테스트
     * @throws Exception
     */

    @Test
    @DisplayName("[정상 테스트] 존재하는 storeId에 존재하는 productId 에 맞춰서 존재하는 상품을 조회")
    @WithMockCustomUser(sellerId = 1L, role = Role.SELLER)
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
        mockMvc.perform(get("/api/v1/stores/{storeId}/products/{productId}", 1L, 1L))
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
    @DisplayName("[에러 테스트] 상품이 존재는 하지만 다른 상점의 아이디로 조회")
    @WithMockCustomUser(sellerId = 2L, role = Role.SELLER)
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
        mockMvc.perform(get("/api/v1/stores/{storeId}/products/{productId}", 1L, 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value("403"));
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
    @WithMockCustomUser(sellerId = 1L, role = Role.SELLER)
    void test_putProduct_NotFound() throws Exception {
        // given
        given(storeProductService.getProductByProductId(999L))
                .willThrow(new ProductNotFoundException());

        // when & then
        mockMvc.perform(get("/api/v1/stores/{storeId}/products/{productId}", 1L, 999L))
                .andExpect(status().isNotFound());
    }

    @WithMockCustomUser(sellerId = -1L)
    @Test
    @DisplayName("[에러 테스트] SELLER 프로필이 존재하지 않을 경우 403")
    void test_productAccessWithoutSellerProfile() throws Exception {
        mockMvc.perform(get("/api/v1/stores/{storeId}/products/{productId}", 1L, 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[에러 테스트] storeId가 음수일 경우 400 BadRequest")
    @WithMockCustomUser(sellerId = 1L)
    void test_invalidStoreIdAndProductId() throws Exception {
        mockMvc.perform(get("/api/v1/stores/{storeId}/products/{productId}", -1, -1))
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(sellerId = 1L, role = Role.USER)
    @Test
    @DisplayName("[에러 테스트] SELLER 전용 API에 USER가 접근 시도하면 403")
    void test_buyerAccessingSellerProduct() throws Exception {
        mockMvc.perform(get("/api/v1/stores/{storeId}/products/{productId}", 1L, 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("[에러 테스트] 잘못된 productId 형식 (문자열)일 때 400")
    @WithMockCustomUser(sellerId = 1L, role = Role.SELLER)
    void test_invalidProductIdType() throws Exception {
        mockMvc.perform(get("/api/v1/stores/1/products/abc"))
                .andExpect(status().isBadRequest());
    }
}