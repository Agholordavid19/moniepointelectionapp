package org.mackson.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequest {
    private String governorEmail;
    private String deputyGovernorEmail;
    private String speakerEmail;
    private String deputySpeakerEmail;
}
