package org.mackson.model.dtos.citizendto.updatecitizen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitizenUpdateRequest {
    private String phoneNumber;
    private String localGovernment;
    private int year;
    private String name;
}
