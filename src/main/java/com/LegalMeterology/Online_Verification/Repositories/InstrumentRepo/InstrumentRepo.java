package com.LegalMeterology.Online_Verification.Repositories.InstrumentRepo;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LegalMeterology.Online_Verification.Entities.Instrument;

@Repository
public interface InstrumentRepo extends MongoRepository<Instrument,ObjectId>{
    
    boolean existsByManufacturerAndSerialNumber(String manufacturer,String serialNumber);

    boolean existsByInstrumentNumber(String instrumentNumber);
    
    List<Instrument> findAllByOwnerId(String ownerId);

    Optional<Instrument> findByInstrumentNumber(String instrumentNumber);

}
