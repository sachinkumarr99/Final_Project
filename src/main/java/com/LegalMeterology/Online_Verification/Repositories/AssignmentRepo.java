package com.LegalMeterology.Online_Verification.Repositories;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LegalMeterology.Online_Verification.Entities.Assignment;
import com.LegalMeterology.Online_Verification.Enums.ApplicationStatus;

@Repository
public interface AssignmentRepo extends MongoRepository<Assignment,ObjectId>{

    Optional<Assignment> findByApplicationNumber(String applicationNumber);

    List<Assignment> findAllByAssignedToId(String assignedToId);

    List<Assignment> findAllByAssignedToIdAndStatus(
            String assignedToId,
            ApplicationStatus status
    );

    boolean existsByApplicationNumberAndAssignedById(String applicationNumber,String assignedById);

}
