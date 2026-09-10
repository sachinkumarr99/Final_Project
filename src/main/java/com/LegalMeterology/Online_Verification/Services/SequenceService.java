package com.LegalMeterology.Online_Verification.Services;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.LegalMeterology.Online_Verification.Entities.Counter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SequenceService {

    private final MongoTemplate mongoTemplate;

    public long getNextSequence(String sequenceName) {

        Query query = new Query(
                Criteria.where("_id").is(sequenceName)
        );

        Update update = new Update()
                .inc("sequence", 1);

        Counter counter = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options()
                        .upsert(true)
                        .returnNew(true),
                Counter.class
        );

        return counter.getSequence();
    }
}