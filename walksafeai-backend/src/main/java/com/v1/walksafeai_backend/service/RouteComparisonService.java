package com.v1.walksafeai_backend.service;

import com.v1.walksafeai_backend.service.routing.RoutingClient;
import com.v1.walksafeai_backend.service.routing.RoutingModels;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteComparisonService {

    private final RoutingClient routingClient;
    private final RouteSafetyScoringService scoringService;

    @org.springframework.beans.factory.annotation.Value("${walksafe.scoring.sample-radius-m:60}")
    private double sampleRadius;

    public Result compare(double[] origin, double[] dest, LocalTime time) {

        boolean night = scoringService.isNight(time);

        RoutingModels.RouteResult shortest = routingClient.getShortest(origin, dest);
        List<RoutingModels.RouteResult> alternatives = routingClient.getAlternatives(origin, dest);

        RouteSafetyScoringService.ScoredRoute shortestScored = scoringService.score(shortest, night, sampleRadius);

        RouteSafetyScoringService.ScoredRoute bestAlt = alternatives.stream()
                .map(r -> scoringService.score(r, night, sampleRadius))
                .max(Comparator.comparingDouble(RouteSafetyScoringService.ScoredRoute::getOverall))
                .orElse(null);

        // Pick safer route only if meaningfully better
        RouteSafetyScoringService.ScoredRoute safer = (bestAlt != null && bestAlt.getOverall() > shortestScored.getOverall() + 0.08)
                ? bestAlt : shortestScored;

        return new Result(shortestScored, safer);
    }

    public record Result(RouteSafetyScoringService.ScoredRoute shortest, RouteSafetyScoringService.ScoredRoute safer) {}
}

