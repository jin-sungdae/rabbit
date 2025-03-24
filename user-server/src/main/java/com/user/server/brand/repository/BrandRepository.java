package com.user.server.brand.repository;


import com.user.server.brand.entity.ProductBrand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<ProductBrand, Long> {
}