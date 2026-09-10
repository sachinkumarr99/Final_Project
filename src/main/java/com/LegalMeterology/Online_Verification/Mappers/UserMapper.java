package com.LegalMeterology.Online_Verification.Mappers;

import org.springframework.stereotype.Component;

import com.LegalMeterology.Online_Verification.Dto.UserDto;
import com.LegalMeterology.Online_Verification.Entities.User;

@Component
public class UserMapper {
    
    
    // DTO -> Entity
    public User toEntity(UserDto dto) {

        if (dto == null) {
            return null;
        }

        return User.builder()
                .email(dto.getEmail())
                .mobile(dto.getMobile())
                .password(dto.getPassword())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .build();
    }


    // Entity -> DTO
    public UserDto toDto(User user) {

        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .build();
    }
}
