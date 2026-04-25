package org.mackson.model.dtos.citizendto.signup;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CitizenResponse {
    private String status;
    private String email;
}
