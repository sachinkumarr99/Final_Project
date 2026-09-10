package com.LegalMeterology.Online_Verification.Repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.LegalMeterology.Online_Verification.Entities.Counter;

@Repository
public interface CounterRepo extends MongoRepository<Counter,ObjectId>{

} 
