package com.ecommerce.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerRegisterRequestDto {

    private String mobile;
    private String password;
    private String fullName;
    private String email;
    private String companyName;
    private String companyContactNo;
    private String companyEmailId;
    private String companyAddress;
    private String sellerCode;
    private String city;
    private String state;
    private String pincode;
    
}
