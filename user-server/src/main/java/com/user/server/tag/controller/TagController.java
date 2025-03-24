package com.user.server.tag.controller;

import com.common.config.api.apidto.APIDataResponse;
import com.user.server.tag.dto.PopularTagResponse;
import com.user.server.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/popular")
    public APIDataResponse<List<PopularTagResponse>> getPopularTags(@RequestParam(defaultValue = "10") int limit) {

        List<PopularTagResponse> popularTags = tagService.getPopularTags(limit);

        return APIDataResponse.of(popularTags);
    }
}
