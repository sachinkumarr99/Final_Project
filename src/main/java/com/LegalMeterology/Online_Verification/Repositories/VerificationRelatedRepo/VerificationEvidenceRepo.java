package com.LegalMeterology.Online_Verification.Repositories.VerificationRelatedRepo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LegalMeterology.Online_Verification.Entities.VerificationRelated.VerificationEvidence;

@Repository
public interface VerificationEvidenceRepo
        extends MongoRepository<VerificationEvidence, ObjectId> {

    List<VerificationEvidence> findAllByVerificationId(
            String verificationId
    );

    List<VerificationEvidence> findAllByApplicationNumber(
            String applicationNumber
    );

}