package org.mackson.model.dtos.citizendto.updatecitizen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mackson.model.data.LocalGovernmentArea;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CitizenUpdateValidated {
    private String phoneNumber;
    private LocalGovernmentArea localGovernment;
    private int year;
    private String name;
}
