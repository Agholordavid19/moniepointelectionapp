package org.mackson.model.dtos.citizendto.updatecitizen;

import lombok.*;
import org.mackson.model.data.LocalGovernmentArea;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitizenRequestValidated {
    private String email;
    private String name;
    private int year;
    private String password;
    private String phoneNumber;
    private LocalGovernmentArea localGovernmentArea;
}
