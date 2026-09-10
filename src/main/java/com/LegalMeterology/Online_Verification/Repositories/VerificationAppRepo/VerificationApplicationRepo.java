package com.LegalMeterology.Online_Verification.Repositories.VerificationAppRepo;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.LegalMeterology.Online_Verification.Entities.VerificationApplication;

public interface VerificationApplicationRepo extends MongoRepository<VerificationApplication,ObjectId>{
    
     List<VerificationApplication> findAllByUserId(String userId);

     boolean existsByInstrumentNumber(String instrumentNumber);

     boolean existsByApplicationNumber(String applicationNumber);

     Optional<VerificationApplication> findByApplicationNumber(String applicationNumber);
     Optional<VerificationApplication> findByInstrumentNumber(String instrumentNumber);
     
}
