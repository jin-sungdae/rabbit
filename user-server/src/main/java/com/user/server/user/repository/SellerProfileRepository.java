package com.user.server.user.repository;

import com.user.server.product.entity.Product;
import com.user.server.user.entity.SellerProfile;
import com.user.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {

    Optional<SellerProfile> findByUserUid(String uid);


}
