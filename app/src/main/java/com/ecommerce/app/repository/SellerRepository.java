package com.ecommerce.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.app.entity.Seller;
import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {
    
}
