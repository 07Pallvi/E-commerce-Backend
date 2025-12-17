package com.ecommerce.app.entity;

import com.ecommerce.app.enums.Permission;
import com.ecommerce.app.enums.RoleName;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "roles")
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role extends AuditModel  {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
	@Enumerated(EnumType.STRING)
	private RoleName name;

    @Column(name = "permissions", columnDefinition = "TEXT")
    @Convert(converter = PermissionConverter.class)
    private List<Permission> permissions = new ArrayList<>();

    @Column(name = "is_active", columnDefinition = "boolean default false")
	private boolean isActive;

    @Column(name = "has_all_permissions", columnDefinition = "boolean default false")
	private boolean hasAllPermissions;
    
    
    @Converter
    public static class PermissionConverter implements AttributeConverter<List<Permission>, String> {
        
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<Permission> attribute) {
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (Exception e) {
                return "[]";
            }
        }

        @Override
        public List<Permission> convertToEntityAttribute(String dbData) {
            try {
                if (dbData == null || dbData.isEmpty()) {
                    return new ArrayList<>();
                }
                return objectMapper.readValue(dbData, new TypeReference<List<Permission>>() {});
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
    }
    
}