package com.user.server.user.controller;


import com.common.config.api.apidto.APIDataResponse;

import com.user.config.security.PrincipalDetails;
import com.user.server.user.dto.RequestSellerProfile;
import com.user.server.user.dto.ResponseSellerProfile;
import com.user.server.user.dto.ResponseUserProfile;
import com.user.server.user.service.SellerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seller")
@RequiredArgsConstructor
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;

    @PostMapping("/apply")
    public APIDataResponse<String> applySeller(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody RequestSellerProfile dto
    ) {

        sellerProfileService.applySeller(principal.getUser().getUid(), dto);

        return APIDataResponse.of(Boolean.toString(true));
    }

    @PutMapping("/profile")
    public APIDataResponse<String> updateSellerProfile(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody RequestSellerProfile dto
    ) {

        sellerProfileService.updateSellerProfile(principal.getUser().getUid(), dto);

        return APIDataResponse.of(Boolean.toString(true));
    }

    @GetMapping("/profile")
    public APIDataResponse<ResponseSellerProfile> getSellerProfile(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {

        ResponseSellerProfile responseSellerProfile = sellerProfileService.getSellerProfile(principal.getUser().getUid());


        return APIDataResponse.of(responseSellerProfile);
    }

}
