package org.mackson.model.dtos.contestant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mackson.model.data.ElectionPosition;
import org.mackson.model.data.PolitcalParties;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContestantResponse {
    private String party;
    private String position;
    private Instant reisteredAt;
}
