package org.mackson.model.dtos.contestant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContestantRequest {
    private String party;
    private String position;
}
