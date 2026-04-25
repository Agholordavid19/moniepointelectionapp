package org.mackson.model.dtos.contestant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mackson.model.data.ElectionPosition;
import org.mackson.model.data.PolitcalParties;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ContestantVoterResponse {
    private String party;
    private String post;
    private int voters;
}
