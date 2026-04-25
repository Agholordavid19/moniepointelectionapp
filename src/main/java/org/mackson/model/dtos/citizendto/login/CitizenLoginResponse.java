package org.mackson.model.dtos.citizendto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CitizenLoginResponse {
    private String email;
    private boolean isLoggedIn;
}
