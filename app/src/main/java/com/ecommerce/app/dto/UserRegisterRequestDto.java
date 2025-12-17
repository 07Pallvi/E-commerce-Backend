package com.ecommerce.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequestDto {

    private String mobile;
    private String email;
    private String password;
    private String fullName;
    private AddressDto address;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {

        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String pincode;
    }

}
