package org.mackson.model.dtos.signup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitizenRequest {
    private String email;
    private String name;
    private int year;
    private String password;
    private String phoneNumber;
    private String localGovernmentArea;
}
