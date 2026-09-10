package com.LegalMeterology.Online_Verification.Dto;

import java.time.LocalDateTime;

import com.LegalMeterology.Online_Verification.Enums.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private String id;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
    private String email;

    @NotBlank(message = "Mobile Number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid mobile number")
    private String mobile;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$", message = "Password must be at least 8 characters and contain uppercase, lowercase, number and special character")
    private String password;

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z]{2,10}$", message = "First name must contain only letters and be 2 to 10 characters long")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z]{2,20}$", message = "Last name must contain only letters and be 2 to 20 characters long")
    private String lastName;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    private String address;

    @NotBlank(message = "city is required")
    @Pattern(regexp = "^[a-zA-Z]{2,15}$", message = "city name should be alphabatically")
    private String city;

    @NotBlank(message = "state is required")
    @Pattern(regexp = "^[a-zA-Z]+( +[a-zA-Z]+)*$", message = "state name should be alphabatically")
    private String state;

    @NotNull(message = "Is Organization or Not")
    private boolean isOrganization;


}
