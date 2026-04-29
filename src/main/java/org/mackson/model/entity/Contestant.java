package org.mackson.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mackson.model.data.ElectionPosition;
import org.mackson.model.data.PolitcalParties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contestant {
    @Id
    private String id;
    private String name;
    private PolitcalParties party;
    private ElectionPosition position;
    private Integer numberOfVotes;
    private boolean isContestant;
    private String email;
    private Instant reisteredAt;
    private Instant updatedAt;
}
