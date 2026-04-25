package org.mackson.model.dtos.citizendto.updatecitizen;

import lombok.*;
import org.mackson.model.data.LocalGovernmentArea;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CitizenUpdatedResponse {
    private String email;
    private String phoneNumber;
    private String localGovernment;
    private int year;
    private String name;
    private String votingId;
}
