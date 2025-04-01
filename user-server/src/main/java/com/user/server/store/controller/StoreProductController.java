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

import java.security.InvalidParameterException;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/products")
@RequiredArgsConstructor
@Slf4j
public class StoreProductController {

    private final StoreProductService storeProductService;

    @PostMapping("/register")
    public APIDataResponse<String> registerProduct(
            @PathVariable Long storeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody RequestProductDto product
    ) {

        if (storeId <= 0) {
            throw new InvalidParameterException("storeId는 양수여야 합니다.");
        }

        if(!principalDetails.getUser().getRole().toString().equals("SELLER")) {
            throw new ForbiddenAccessException("판매자만 상품을 등록할 수 있습니다.");
        }

        if (!principalDetails.getUser().getSellerProfile().getId().equals(storeId)) {
            throw new ForbiddenAccessException("다른 상점에서 상품을 등록할 수 없습니다.");
        }

        String productUid = storeProductService.registerProduct(product, principalDetails.getUser());

        return APIDataResponse.of(productUid);
    }

    @GetMapping("/all")
    public APIDataResponse<Page<ResponseProductDto>> getAllProducts(
            @PathVariable Long storeId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        if (!principalDetails.getUser().getRole().toString().equals("SELLER")) {
            throw new ForbiddenAccessException("판매자만 접근할 수 있습니다.");
        }

        if (!principalDetails.getUser().getSellerProfile().getId().equals(storeId)) {
            throw new ForbiddenAccessException("본인의 상점만 조회할 수 있습니다.");
        }

        return APIDataResponse.of(storeProductService.getAllProducts(principalDetails.getUser(), pageable));
    }

    @GetMapping("/{productId}")
    public APIDataResponse<ResponseProductDto> getProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        if (storeId <= 0 || productId <= 0) {
            throw new InvalidParameterException("storeId와 productId는 양수여야 합니다.");
        }

        if (!principal.getUser().getSellerProfile().getId().equals(storeId)) {
            throw new ForbiddenAccessException("다른 상점의 상품은 조회할 수 없습니다.");
        }

        if (!principal.getUser().getRole().toString().equals("SELLER")) {
            throw new ForbiddenAccessException("판매자만 조회할 수 있습니다.");
        }

        return APIDataResponse.of(storeProductService.getProductByProductId(productId));
    }

    @PutMapping("/{productId}")
    public APIDataResponse<String> updateProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody ProductUpdateRequest request
    ) {
        if (!principal.getUser().getSellerProfile().getId().equals(storeId)) {
            throw new ForbiddenAccessException("다른 상점의 상품은 수정할 수 없습니다.");
        }

        storeProductService.update(storeId, productId, request);
        return APIDataResponse.of(Boolean.toString(true));
    }
}
