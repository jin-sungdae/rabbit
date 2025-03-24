package com.user.server.product.respository;

import com.user.server.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsFeaturedTrue();

    List<Product> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT p FROM Product p JOIN p.variants v JOIN v.prices price WHERE price.salePrice < price.basePrice")
    List<Product> findDiscountedProducts();

    @Query("SELECT p FROM Product p JOIN p.tagMappings tm JOIN tm.tag t WHERE t.tagName = :tag")
    List<Product> findByTag(@Param("tag") String tag);

    @Query("SELECT p FROM Product p JOIN p.tagMappings tm JOIN tm.tag t WHERE t.tagName = :tagName")
    List<Product> findAllByTagName(@Param("tagName") String tagName);

    List<Product> findByBrandId(Long brandId);
}
