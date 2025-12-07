package com.v1.walksafeai_backend.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NormalizationUtil {

    public double clamp01(double v) {
        if (v < 0) return 0;
        if (v > 1) return 1;
        return v;
    }

    /**
     * Normalize with a soft cap.
     * Example: if you expect 0..50 lights in sample area,
     * counts > 50 won't keep boosting score linearly.
     */
    public double cappedRatio(double value, double cap) {
        if (cap <= 0) return 0;
        return clamp01(value / cap);
    }

    public double invert(double score01) {
        return clamp01(1.0 - score01);
    }
}

