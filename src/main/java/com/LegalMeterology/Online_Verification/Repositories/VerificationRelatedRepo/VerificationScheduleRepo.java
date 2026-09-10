package com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationSchedule;

@Repository
public interface VerificationScheduleRepo
        extends MongoRepository<VerificationSchedule, ObjectId> {

    Optional<VerificationSchedule> findByApplicationNumber(
            String applicationNumber
    );
}