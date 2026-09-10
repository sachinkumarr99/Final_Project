package com.LegalMeterology.Online_Verification.Repositories.InstrumentRepo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LegalMeterology.Online_Verification.Entities.InstrumentDocument;
import com.LegalMeterology.Online_Verification.Enums.Machine.DocumentCategory;

@Repository
public interface InstrumentDocumentRepo extends MongoRepository<InstrumentDocument, ObjectId> {

    boolean existsByInstrumentNumberAndCategory(
        String instrumentNumber,
        DocumentCategory category
    );

    List<InstrumentDocument> findAllByInstrumentNumber(String instrumentNumber);
}
