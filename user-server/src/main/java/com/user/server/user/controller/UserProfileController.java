package com.user.server.user.controller;

import com.common.config.api.apidto.APIDataResponse;
import com.user.config.security.PrincipalDetails;
import com.user.server.user.dto.RequestUserProfile;
import com.user.server.user.dto.ResponseUserProfile;
import com.user.server.user.service.UserProfileService;
import com.user.server.user.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("")
    public APIDataResponse<ResponseUserProfile> getUserProfile(
            @AuthenticationPrincipal PrincipalDetails principal
    ) {

        ResponseUserProfile responseUserProfile = userProfileService.getUserProfile(principal.getUser().getUid());

        return APIDataResponse.of(responseUserProfile);
    }

    @PutMapping("")
    @RateLimiter(name = "userProfileUpdate", fallbackMethod = "rateLimitFallback")
    public APIDataResponse<String> updateUserProfile(
            @AuthenticationPrincipal PrincipalDetails principal,
            @RequestBody RequestUserProfile requestUserProfile
    ) {

        userProfileService.updateUserProfile(principal.getUser().getUid(), requestUserProfile);

        return APIDataResponse.of(Boolean.toString(true));
    }

    public APIDataResponse<String> rateLimitFallback(Exception e) {
        return APIDataResponse.of("false", "요청이 너무 많습니다. 잠시 후 다시 시도하세요.");
    }

}
