package com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationStandard;
import com.LegalMeterology.Online_Verification.Enums.Machine.InstrumentType;

@Repository
public interface VerificationStandardRepo
        extends MongoRepository<VerificationStandard, ObjectId> {

    Optional<VerificationStandard> findByInstrumentType(
            InstrumentType instrumentType
    );

     boolean existsByInstrumentType(InstrumentType instrumentType);
}