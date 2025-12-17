package com.ecommerce.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.app.entity.Address;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    
}
