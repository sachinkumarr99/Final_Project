package com.LegalMeterology.Online_Verification.Repositories;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LegalMeterology.Online_Verification.Entities.User;
import com.LegalMeterology.Online_Verification.Enums.Role;

@Repository
public interface UserRepo extends MongoRepository<User,ObjectId>{
    
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(Role role);
} 
