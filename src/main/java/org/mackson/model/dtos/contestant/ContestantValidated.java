package org.mackson.model.dtos.contestant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mackson.model.data.ElectionPosition;
import org.mackson.model.data.PolitcalParties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContestantValidated {
    private PolitcalParties party;
    private ElectionPosition position;
}
