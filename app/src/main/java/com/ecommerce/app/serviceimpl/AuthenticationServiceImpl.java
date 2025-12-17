package com.ecommerce.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.ecommerce.app.dto.RefreshTokenRequestDto;
import com.ecommerce.app.dto.SellerRegisterRequestDto;
import com.ecommerce.app.dto.UserRegisterRequestDto;
import com.ecommerce.app.entity.Address;
import com.ecommerce.app.entity.Role;
import com.ecommerce.app.entity.Seller;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.enums.RoleName;
import com.ecommerce.app.exception.EcomException;
import com.ecommerce.app.repository.AddressRepository;
import com.ecommerce.app.repository.RoleRepository;
import com.ecommerce.app.repository.SellerRepository;
import com.ecommerce.app.repository.UserRepository;
import com.ecommerce.app.service.AuthenticationService;
import com.ecommerce.app.service.JwtService;
import com.ecommerce.app.utils.ApplicationConstants;
import com.ecommerce.app.utils.CommonUtils;
import com.ecommerce.app.utils.ErrorInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = LogManager.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final SellerRepository sellerRepository;
    

    @Override
    public JSONObject registerUser(UserRegisterRequestDto requestDto) throws EcomException {

        validateRegisterUserRequest(requestDto);       
        
        Role role = roleRepository.findByName(RoleName.CUSTOMER).orElseThrow(() -> 
              new EcomException("Customer role not found", ErrorInfo.ROLE_NOT_FOUND.getErrorCode()));

        User user = createUser(requestDto, role);

        Address address = new Address();
        address.setAddressLine1(requestDto.getAddress().getAddressLine1());
        address.setAddressLine2(requestDto.getAddress().getAddressLine2());
        address.setCity(requestDto.getAddress().getCity());
        address.setState(requestDto.getAddress().getState());
        address.setPincode(requestDto.getAddress().getPincode());
        address.setUser(user);
        addressRepository.save(address);

        user.setAddressUpdated(true);
        userRepository.save(user);

        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("message", "User registered successfully");
        response.put("action", "Login to continue");
        return CommonUtils.createFinalJsonResponse(response);
    }

    private User createUser(UserRegisterRequestDto requestDto, Role role) throws EcomException {

        Optional<User> optionalUser = userRepository.findByMobile(requestDto.getMobile());
        Optional<User> optionalUserWithEmail = userRepository.findByEmail(requestDto.getEmail());

        User user = null;
        if(!optionalUser.isPresent()) {
            if(optionalUserWithEmail.isPresent()) {
                throw new EcomException("User with this email already exists", ErrorInfo.USER_ALREADY_EXISTS.getErrorCode());
            }
            User newUser = new User();
            newUser.setMobile(requestDto.getMobile());
            newUser.setPassword(requestDto.getPassword());     // store password using Bcrypt
            newUser.setFullName(requestDto.getFullName());
            newUser.setActive(true);
            newUser.setRole(role);
            if(requestDto.getEmail() != null && !requestDto.getEmail().isBlank()) {
                newUser.setEmail(requestDto.getEmail());
            }
            user = userRepository.save(newUser);
        }else{
            user = optionalUser.get();
            if(!user.getId().equals(optionalUserWithEmail.get().getId())) {
                throw new EcomException("User with this email already exists", ErrorInfo.USER_ALREADY_EXISTS.getErrorCode());
            }
            if(user.isActive()){
                throw new EcomException("Already registered as" + user.getRole().getName().name(), ErrorInfo.USER_ALREADY_EXISTS.getErrorCode());
            }else{
                LOGGER.info("User already exists, updating user details");
                user.setActive(true);
                user.setPassword(requestDto.getPassword());
                user.setFullName(requestDto.getFullName());
                user.setEmail(requestDto.getEmail());
                user.setMobile(requestDto.getMobile());
                user.setRole(role);
                if(requestDto.getEmail() != null && !requestDto.getEmail().isBlank()) {
                    user.setEmail(requestDto.getEmail());
                }
                user = userRepository.save(user);
            }
        }
        return user;
    }

    private void validateRegisterUserRequest(UserRegisterRequestDto requestDto) throws EcomException {

        if(requestDto == null) {
            throw new EcomException(ErrorInfo.INVALID_REQUEST.getErrorText(), ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
        CommonUtils.validateMandatoryField(requestDto.getMobile(), "Mobile number");
        CommonUtils.validateMandatoryField(requestDto.getPassword(), "Password");
        CommonUtils.validateMandatoryField(requestDto.getFullName(), "Full name");
        CommonUtils.validatemobile(requestDto.getMobile(), "Mobile number");
        CommonUtils.validatePassword(requestDto.getPassword());
        CommonUtils.validateName(requestDto.getFullName());
        // non mandatory fields validations
        if(requestDto.getEmail() != null && !requestDto.getEmail().isBlank()) {
            CommonUtils.validateEmail(requestDto.getEmail(), "Email");
        }
        if(requestDto.getAddress() != null) {
            CommonUtils.validateMandatoryField(requestDto.getAddress().getAddressLine1(), "Address line 1");
            CommonUtils.validateMandatoryField(requestDto.getAddress().getCity(), "City");
            CommonUtils.validateMandatoryField(requestDto.getAddress().getState(), "State");
            CommonUtils.validateMandatoryField(requestDto.getAddress().getPincode(), "Pincode");
            CommonUtils.validatePincode(requestDto.getAddress().getPincode(), "Pincode");
        }
    }

    @Override
    public JSONObject registerSeller(SellerRegisterRequestDto requestDto) throws EcomException {

        validateSellerRequest(requestDto);

        Role role = roleRepository.findByName(RoleName.SELLER).orElseThrow(() -> 
              new EcomException("Seller role not found", ErrorInfo.ROLE_NOT_FOUND.getErrorCode()));

        UserRegisterRequestDto requestDtoForUser = UserRegisterRequestDto.builder()
            .mobile(requestDto.getMobile()).password(requestDto.getPassword())
            .fullName(requestDto.getFullName()).email(requestDto.getEmail()).build();

        User user = createUser(requestDtoForUser, role);

        Seller seller = new Seller();
        seller.setUser(user);
        seller.setCompanyName(requestDto.getCompanyName());
        seller.setCompanyContactNo(requestDto.getCompanyContactNo());
        seller.setCompanyEmailId(requestDto.getCompanyEmailId());
        seller.setCompanyAddress(requestDto.getCompanyAddress());
        seller.setSellerCode(requestDto.getSellerCode());
        seller.setCity(requestDto.getCity());
        seller.setState(requestDto.getState());
        seller.setPincode(requestDto.getPincode());
        sellerRepository.save(seller);

        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("message", "Seller registered successfully");
        response.put("action", "Login to continue");
        return CommonUtils.createFinalJsonResponse(response);
    }

    private void validateSellerRequest(SellerRegisterRequestDto requestDto) throws EcomException {
        
        if(requestDto == null) {
            throw new EcomException(ErrorInfo.INVALID_REQUEST.getErrorText(), ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
        CommonUtils.validateMandatoryField(requestDto.getMobile(), "Mobile number");
        CommonUtils.validateMandatoryField(requestDto.getPassword(), "Password");
        CommonUtils.validateMandatoryField(requestDto.getFullName(), "Full name");
        CommonUtils.validateMandatoryField(requestDto.getCompanyName(), "Company name");
        CommonUtils.validateMandatoryField(requestDto.getCompanyContactNo(), "Company contact number");
        CommonUtils.validateMandatoryField(requestDto.getCompanyEmailId(), "Company email ID");
        CommonUtils.validateMandatoryField(requestDto.getCompanyAddress(), "Company address");
        CommonUtils.validateMandatoryField(requestDto.getSellerCode(), "Seller code");
        CommonUtils.validateMandatoryField(requestDto.getCity(), "City");
        CommonUtils.validateMandatoryField(requestDto.getState(), "State");
        CommonUtils.validateMandatoryField(requestDto.getPincode(), "Pincode");
        if(requestDto.getEmail() != null && !requestDto.getEmail().isBlank()) {
            CommonUtils.validateEmail(requestDto.getEmail(), "Email");
        }
        CommonUtils.validatemobile(requestDto.getMobile(), "Mobile number");
        CommonUtils.validatePassword(requestDto.getPassword());
        CommonUtils.validateName(requestDto.getFullName());
        CommonUtils.validateString(requestDto.getCompanyName(), "Company name", 60);
        CommonUtils.validatemobile(requestDto.getCompanyContactNo(), "Company contact number");
        CommonUtils.validateEmail(requestDto.getEmail(), "Company email ID");
        CommonUtils.validateString(requestDto.getCompanyAddress(), "Company address", 300);
        CommonUtils.validateString(requestDto.getCity(), "City", 50);
        CommonUtils.validateString(requestDto.getState(), "State", 50);
        CommonUtils.validatePincode(requestDto.getPincode(), "Pincode");
        CommonUtils.validatePincode(requestDto.getSellerCode(), "Seller code");   
    }

    @Override
    public JSONObject authenticate(String mobile, String password) throws EcomException {

        CommonUtils.validateMandatoryField(mobile, "Mobile number");
        CommonUtils.validateMandatoryField(password, "Password");
        CommonUtils.validatemobile(mobile, "Mobile number");

        User user = userRepository.findByMobileAndIsActiveTrue(mobile).orElseThrow(() -> 
              new EcomException(ErrorInfo.USER_NOT_FOUND.getErrorText(), ErrorInfo.USER_NOT_FOUND.getErrorCode()));

        // For now, simple password comparison (INSECURE - replace with BCrypt in production)     ---- ??
        if (!user.getPassword().equals(password)) {
            throw new EcomException(ErrorInfo.INCORRECT_PASSWORD.getErrorText(), ErrorInfo.INCORRECT_PASSWORD.getErrorCode());
        }
        user.setLastLoggedIn(LocalDateTime.now());
        userRepository.save(user);

        String roleName = user.getRole().getName().name();
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getMobile(), roleName);
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getMobile());
        LOGGER.info("User authenticated successfully: {}", mobile);

        JSONObject response = new JSONObject();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("userId", user.getId());
        response.put("mobile", user.getMobile());
        response.put("role", roleName);
        return CommonUtils.createFinalJsonResponse(response);
    }
    
    // Refresh access token using refresh token
    public JSONObject refreshToken(RefreshTokenRequestDto requestDto) throws EcomException {

        if(requestDto == null) {
            throw new EcomException(ErrorInfo.INVALID_REQUEST.getErrorText(), ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
        String refreshToken = requestDto.getRefreshToken();
        CommonUtils.validateMandatoryField(refreshToken, "Refresh token");

        if (!jwtService.validateToken(refreshToken, jwtService.getRefreshSignKey())) {
            throw new EcomException("Invalid refresh token", ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
        String userId = jwtService.extractUserId(refreshToken, jwtService.getRefreshSignKey());
        String mobile = jwtService.extractPhoneNumber(refreshToken, jwtService.getRefreshSignKey());

        if (userId == null || mobile == null) {
            throw new EcomException("Invalid refresh token claims", ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
        User user = userRepository.findByIdAndIsActiveTrue(UUID.fromString(userId)).orElseThrow(() -> 
              new EcomException(ErrorInfo.USER_NOT_FOUND.getErrorText(), ErrorInfo.USER_NOT_FOUND.getErrorCode()));

        // Generate new access token
        String roleName = user.getRole().getName().name();
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getMobile(), roleName);
        LOGGER.info("Access token refreshed for user: {}", mobile);

        JSONObject response = new JSONObject();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("userId", user.getId());
        return CommonUtils.createFinalJsonResponse(response);
    }

    @Override
    public JSONObject validateToken(HttpHeaders headers) throws EcomException {

        String token = headers.getFirst(ApplicationConstants.AUTHORIZATION);
        if (token == null || !token.startsWith(ApplicationConstants.BEARER) || token.length() <= 7) {
            throw new EcomException("Invalid token", ErrorInfo.INVALID_REQUEST.getErrorCode());
        }
        token = token.substring(7);
        String mobile = jwtService.extractPhoneNumber(token, jwtService.getAccessSignKey());
        String role = jwtService.extractRole(token, jwtService.getAccessSignKey());

        Optional<User> optionalUser = userRepository.findByMobile(mobile);
        if(optionalUser.isPresent()) {

            User user = optionalUser.get();
            if(!user.getRole().getName().name().equals(role)) {
                throw new EcomException(ErrorInfo.TOKEN_DOES_NOT_BELONG_TO_USER.getErrorText(), 
                        ErrorInfo.TOKEN_DOES_NOT_BELONG_TO_USER.getErrorCode());
            }
            if(!user.isActive()) {
                throw new EcomException(ErrorInfo.USER_NOT_ACTIVE.getErrorText(), ErrorInfo.USER_NOT_ACTIVE.getErrorCode());
            }
            JSONObject response = new JSONObject();
            response.put("userId", user.getId());
            response.put("mobile", user.getMobile());
            response.put("role", role);
            return CommonUtils.createFinalJsonResponse(response);
        }else{
            CommonUtils.logException(ErrorInfo.TOKEN_DOES_NOT_BELONG_TO_USER.getErrorText(), "validateAuthToken", 
                            "UserServiceImpl", null);
            throw new EcomException(ErrorInfo.TOKEN_DOES_NOT_BELONG_TO_USER.getErrorText(), ErrorInfo.TOKEN_DOES_NOT_BELONG_TO_USER.getErrorCode());
        }
    }

}