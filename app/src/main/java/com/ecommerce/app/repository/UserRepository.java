package com.ecommerce.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.app.entity.User;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    
}
