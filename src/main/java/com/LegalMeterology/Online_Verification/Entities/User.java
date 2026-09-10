package com.LegalMeterology.Online_Verification.Entities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.LegalMeterology.Online_Verification.Enums.Role;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@Builder
public class User {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @Indexed(unique = true)
    private String email;

    private String mobile;

    private String password;

    @Field("first_name")
    private String firstName;

    private String address;

    @Field("last_name")
    private String lastName;

    private String city;
    private String state;
    private boolean isOrganization;

    private Role role;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

}
