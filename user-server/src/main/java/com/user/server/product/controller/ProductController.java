package com.user.server.product.controller;

import com.common.config.api.apidto.APIDataResponse;
import com.user.config.security.PrincipalDetails;
import com.user.server.product.dto.RequestProductDto;
import com.user.server.product.dto.ResponseProductDto;
import com.user.server.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/register")
    public APIDataResponse<String> registerProduct(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody RequestProductDto product) {

        String productUid = productService.registerProduct(product, principalDetails.getUser());

        return APIDataResponse.of(productUid);
    }

    @GetMapping("/all")
    public APIDataResponse<Page<ResponseProductDto>> getAllProducts(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return APIDataResponse.of(productService.getAllProducts(principalDetails.getUser(), pageable));
    }




    @GetMapping("/featured")
    public APIDataResponse<List<ResponseProductDto>> getFeaturedProducts() {
        List<ResponseProductDto> featuredProducts = productService.getFeaturedProducts();

        return APIDataResponse.of(featuredProducts);
    }

    @GetMapping("/latest")
    public APIDataResponse<List<ResponseProductDto>> getLatestProducts() {
        List<ResponseProductDto> latestProducts = productService.getLatestProducts();

        return APIDataResponse.of(latestProducts);
    }

    @GetMapping("/discounted")
    public APIDataResponse<List<ResponseProductDto>> getDiscountedProducts() {

        List<ResponseProductDto> discountedProducts = productService.getDiscountedProducts();


        return APIDataResponse.of(discountedProducts);
    }

    @GetMapping
    public List<ResponseProductDto> getProductsByTag(@RequestParam(required = false) String tag) {
        if (tag != null) {
            return productService.getProductsByTag(tag);
        }

        return null;
    }

    @GetMapping("/brands/{brandId}/products")
    public List<ResponseProductDto> getProductsByBrand(@PathVariable Long brandId) {
        return productService.getProductsByBrand(brandId);
    }
}
