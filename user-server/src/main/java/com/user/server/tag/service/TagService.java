package com.user.server.tag.service;

import com.user.server.product.dto.ResponseProductDto;
import com.user.server.product.entity.Product;
import com.user.server.product.respository.ProductRepository;
import com.user.server.tag.dto.PopularTagResponse;
import com.user.server.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final ProductRepository productRepository;

    public List<Product> findProductsByTagName(String tagName) {
        return productRepository.findAllByTagName(tagName);
    }

    public List<PopularTagResponse> getPopularTags(int limit) {
        return tagRepository.findPopularTags(PageRequest.of(0, limit)).stream()
                .map(r -> new PopularTagResponse((String) r[0], ((Long) r[1]).intValue()))
                .collect(Collectors.toList());
    }

    public List<ResponseProductDto> getProductsByTag(String tagName) {
        return findProductsByTagName(tagName).stream()
                .map(ResponseProductDto::from)
                .collect(Collectors.toList());
    }


}
