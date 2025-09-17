package com.example.MyProject1.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;


import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "journal_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry {

    @Id
    private ObjectId id;

    private String title;
    private String content;

    private LocalDateTime date;
}
