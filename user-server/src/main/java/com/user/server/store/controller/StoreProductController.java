package com.user.server.store.controller;

import com.common.config.api.apidto.APIDataResponse;
import com.common.config.api.exception.ForbiddenAccessException;
import com.user.config.security.PrincipalDetails;
import com.user.server.product.dto.RequestProductDto;
import com.user.server.product.dto.ResponseProductDto;
import com.user.server.store.dto.ProductUpdateRequest;
import com.user.server.store.service.StoreProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/products")
@RequiredArgsConstructor
@Slf4j
public class StoreProductController {

    private final StoreProductService storeProductService;

    @PostMapping("/register")
    public APIDataResponse<String> registerProduct(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody RequestProductDto product) {

        String productUid = storeProductService.registerProduct(product, principalDetails.getUser());

        return APIDataResponse.of(productUid);
    }

    @GetMapping("/all")
    public APIDataResponse<Page<ResponseProductDto>> getAllProducts(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return APIDataResponse.of(storeProductService.getAllProducts(principalDetails.getUser(), pageable));
    }

    @GetMapping("/{productId}")
    public APIDataResponse<ResponseProductDto> getProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        if (!principal.getStoreId().equals(storeId)) {
            throw new ForbiddenAccessException("다른 상점의 상품은 조회할 수 없습니다.");
        }

        return APIDataResponse.of(storeProductService.getProduct(productId));
    }

    @PutMapping("/{productId}")
    public APIDataResponse<?> updateProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody ProductUpdateRequest request
    ) {
        if (!principal.getStoreId().equals(storeId)) {
            throw new ForbiddenAccessException("다른 상점의 상품은 수정할 수 없습니다.");
        }

        storeProductService.update(storeId, productId, request);
        return APIDataResponse.success();
    }
}
