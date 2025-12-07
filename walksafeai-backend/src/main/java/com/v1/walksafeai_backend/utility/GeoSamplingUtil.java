package com.v1.walksafeai_backend.utility;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class GeoSamplingUtil {

    /**
     * Very lightweight polyline sampling.
     * Input: list of [lng, lat] points from routing provider.
     * We do a simple "every Nth point" fallback for MVP.
     */
    public List<double[]> sampleEveryNth(List<double[]> line, int step) {
        List<double[]> out = new ArrayList<>();
        if (line == null || line.isEmpty()) return out;

        for (int i = 0; i < line.size(); i += Math.max(step, 1)) {
            out.add(line.get(i));
        }
        if (!out.get(out.size() - 1).equals(line.get(line.size() - 1))) {
            out.add(line.get(line.size() - 1));
        }
        return out;
    }
}

