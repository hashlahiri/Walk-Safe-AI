package com.v1.walksafeai_backend.service;

import com.v1.walksafeai_backend.repository.IncidentRepository;
import com.v1.walksafeai_backend.repository.PoliceStationRepository;
import com.v1.walksafeai_backend.repository.SafetyFacilityRepository;
import com.v1.walksafeai_backend.repository.StreetLightRepository;
import com.v1.walksafeai_backend.service.routing.RoutingModels;
import com.v1.walksafeai_backend.utility.GeoSamplingUtil;
import com.v1.walksafeai_backend.utility.NormalizationUtil;
import com.v1.walksafeai_backend.utility.ScoringWeights;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteSafetyScoringService {

    private final StreetLightRepository lightRepo;
    private final PoliceStationRepository policeRepo;
    private final SafetyFacilityRepository facilityRepo;
    private final IncidentRepository incidentRepo;

    public ScoredRoute score(RoutingModels.RouteResult route, boolean night, double sampleRadiusMeters) {
        if (route == null || route.getGeometry() == null || route.getGeometry().isEmpty()) {
            return ScoredRoute.empty(route);
        }

        // Very simple sampling: every 5th coordinate for MVP.
        List<double[]> samples = GeoSamplingUtil.sampleEveryNth(route.getGeometry(), 5);

        double lights = 0, police = 0, facilities = 0, incidents = 0;

        for (double[] c : samples) {
            GeoJsonPoint p = new GeoJsonPoint(c[0], c[1]);

            lights += lightRepo.findNear(p, sampleRadiusMeters).size();
            police += policeRepo.findNear(p, sampleRadiusMeters).size();
            facilities += facilityRepo.findNear(p, sampleRadiusMeters).size();
            incidents += incidentRepo.findNear(p, sampleRadiusMeters).size();
        }

        // Average counts per sample point
        int n = Math.max(samples.size(), 1);
        lights /= n;
        police /= n;
        facilities /= n;
        incidents /= n;

        // Normalize with soft caps (tweak per city)
        double lightingScore = NormalizationUtil.cappedRatio(lights, 6);
        double policeScore = NormalizationUtil.cappedRatio(police, 2);
        double facilityScore = NormalizationUtil.cappedRatio(facilities, 2);
        double incidentRisk = NormalizationUtil.cappedRatio(incidents, 3);

        ScoringWeights w = new ScoringWeights(night);

        double overall =
                (lightingScore * w.getLightingWeight()) +
                        (policeScore * w.getPoliceWeight()) +
                        (facilityScore * w.getFacilityWeight()) +
                        (NormalizationUtil.invert(incidentRisk) * w.getIncidentWeight());

        overall = NormalizationUtil.clamp01(overall);

        return ScoredRoute.builder()
                .route(route)
                .overall(overall)
                .lightingScore(lightingScore)
                .policeScore(policeScore)
                .facilityScore(facilityScore)
                .incidentRisk(incidentRisk)
                .night(night)
                .build();
    }

    public boolean isNight(LocalTime t) {
        if (t == null) return false;
        return t.isAfter(LocalTime.of(19, 0)) || t.isBefore(LocalTime.of(6, 0));
    }

    @Data
    @Builder(toBuilder = true)
    public static class ScoredRoute {
        private final RoutingModels.RouteResult route;
        private final double overall;
        private final double lightingScore;
        private final double policeScore;
        private final double facilityScore;
        private final double incidentRisk;
        private final boolean night;

        public static ScoredRoute empty(RoutingModels.RouteResult r) {
            return ScoredRoute.builder().route(r).overall(0).build();
        }
    }
}

