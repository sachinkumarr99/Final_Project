package com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.Verification;

@Repository
public interface VerificationRepo
        extends MongoRepository<Verification, ObjectId> {

    List<Verification> findAllByLmoId(String lmoId);

    Optional<Verification> findByApplicationNumber(
            String applicationNumber
    );

    List<Verification> findAllByInstrumentNumber(
            String instrumentNumber
    );
}