package com.LegalMeterology.Online_Verification.Repositories;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LegalMeterology.Online_Verification.Entities.Certificate;

@Repository
public interface CertificateRepo extends MongoRepository<Certificate,ObjectId>{
    
    Optional<Certificate> findByApplicationNumber(String applicationNumber);
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
    Optional<Certificate> findByVerificationToken(String token);
    Optional<Certificate> findByInstrumentNumber(String instrumentNumber);
}
