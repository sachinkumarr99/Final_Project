package com.LegalMeterology.Online_Verification.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document("counters")
@Data
public class Counter {

    @Id
    private String id;

    private Long sequence;
}