package com.example.MyProject1.Repository;

import com.example.MyProject1.entity.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface JournalEntryRepository extends MongoRepository<JournalEntry, Object> {

}
