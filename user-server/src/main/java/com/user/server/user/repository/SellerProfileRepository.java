package com.user.server.user.repository;

import com.user.server.user.entity.SellerProfile;
import com.user.server.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
}
