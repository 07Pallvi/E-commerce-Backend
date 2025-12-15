package com.ecommerce.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.JoinColumn;
import java.util.UUID;

@Entity
@Table(name = "sellers")
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seller extends AuditModel {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seller_id", updatable = false, nullable = false)
    private UUID sellerId;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "company_contact_no", nullable = false, length = 10)
    private String companyContactNo;
    
    @Column(name = "company_email_id", nullable = false, length = 100)
    private String companyEmailId;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "pan_number", nullable = false, length = 10)
    private String panNumber;
    
    @Column(name = "pincode", nullable = false, length = 6)
    private String pincode;

    @Column(name = "seller_code", nullable = false, length = 10)
    private String sellerCode;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean isActive;
    
    @Column(name = "rating", nullable = false, precision = 2, scale = 1)
    private double rating;
    
    
}
