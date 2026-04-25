package org.mackson.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mackson.model.data.LocalGovernmentArea;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Citizen {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String name;
    private Integer yearOfBirth;
    private String password;
    @Indexed(unique = true)
    private String phoneNumber;
    private boolean isCitizen;
    private boolean isLoggedIn;
    @Indexed(unique = true)
    private String votingId;
    private LocalGovernmentArea localGovernmentArea;
    private boolean hasVoted;
    @CreatedDate
    private Instant createdAt;
    private Instant voterIdRegisteredAt;
    private Instant voterIdExpiringAt;
    @LastModifiedDate
    private Instant updatedAt;
}
