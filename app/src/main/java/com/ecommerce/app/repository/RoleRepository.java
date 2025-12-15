package com.ecommerce.app.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.app.entity.Role;
import com.ecommerce.app.enums.RoleName;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);

    Optional<Role> findByNameAndIsActiveTrue(RoleName name);
    
}
