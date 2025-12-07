package com.v1.walksafeai_backend.utility;

import lombok.Data;
import lombok.Getter;

@Data
public class ScoringWeights {

    private final double lightingWeight;
    private final double policeWeight;
    private final double facilityWeight;
    private final double incidentWeight;
    private final double crowdProxyWeight;

    public ScoringWeights(boolean night) {
        // Simple transparent heuristic
        this.lightingWeight = night ? 0.40 : 0.25;
        this.policeWeight = 0.20;
        this.facilityWeight = 0.15;
        this.incidentWeight = 0.25;
        this.crowdProxyWeight = night ? 0.00 : 0.15; // keep simple for MVP
    }
}

