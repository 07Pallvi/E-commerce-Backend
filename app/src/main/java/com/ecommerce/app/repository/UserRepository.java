package com.ecommerce.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.app.entity.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByMobileNumberAndIsActiveTrue(String mobileNumber);
    
    Optional<User> findByEmailAndIsActiveTrue(String email);

    Optional<User> findByIdAndIsActiveTrue(UUID id);
}
