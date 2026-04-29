package org.mackson.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WinnerResponse {
    private String position;
    private String name;
    private String party;
    private Integer votes;
}
