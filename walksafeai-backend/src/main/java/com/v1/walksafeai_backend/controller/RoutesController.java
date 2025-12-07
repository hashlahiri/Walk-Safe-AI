package com.v1.walksafeai_backend.controller;
import com.v1.walksafeai_backend.service.RouteComparisonService;
import com.v1.walksafeai_backend.service.RouteSafetyScoringService;
import lombok.*;

import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RoutesController {

    private final RouteComparisonService comparisonService;

    @PostMapping("/compare")
    public CompareResponse compare(@RequestBody CompareRequest req) {

        double[] origin = new double[]{req.originLng, req.originLat};
        double[] dest = new double[]{req.destLng, req.destLat};

        LocalTime t = (req.localTime == null || req.localTime.isBlank())
                ? LocalTime.now()
                : LocalTime.parse(req.localTime);

        var result = comparisonService.compare(origin, dest, t);

        return CompareResponse.from(result.shortest(), result.safer());
    }

    @Getter @Setter
    public static class CompareRequest {
        public double originLng;
        public double originLat;
        public double destLng;
        public double destLat;
        public String localTime; // "HH:mm:ss" optional
    }

    @Getter @AllArgsConstructor
    public static class CompareResponse {
        public RouteView shortest;
        public RouteView safer;

        public static CompareResponse from(RouteSafetyScoringService.ScoredRoute s, RouteSafetyScoringService.ScoredRoute safer) {
            return new CompareResponse(RouteView.from(s), RouteView.from(safer));
        }
    }

    @Getter @AllArgsConstructor
    public static class RouteView {
        public String providerRouteId;
        public double distanceMeters;
        public double durationSeconds;
        public double overall;
        public double lightingScore;
        public double policeScore;
        public double facilityScore;
        public double incidentRisk;
        public boolean night;

        public static RouteView from(RouteSafetyScoringService.ScoredRoute sr) {
            var r = sr.getRoute();
            return new RouteView(
                    r != null ? r.getProviderRouteId() : null,
                    r != null ? r.getDistanceMeters() : 0,
                    r != null ? r.getDurationSeconds() : 0,
                    sr.getOverall(),
                    sr.getLightingScore(),
                    sr.getPoliceScore(),
                    sr.getFacilityScore(),
                    sr.getIncidentRisk(),
                    sr.isNight()
            );
        }
    }
}

