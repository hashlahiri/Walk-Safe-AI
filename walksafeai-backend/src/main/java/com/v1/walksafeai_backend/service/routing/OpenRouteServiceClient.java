package com.v1.walksafeai_backend.service.routing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

/**
 * Minimal ORS skeleton
 */
@Component
@RequiredArgsConstructor
public class OpenRouteServiceClient implements RoutingClient {

    private final WebClient routingWebClient;

    @Value("${walksafe.routing.api-key:}")
    private String apiKey;

    @Override
    public RoutingModels.RouteResult getShortest(double[] origin, double[] dest) {
        // TODO: Implement actual ORS call + parsing
        // For now, return null so service can fall back to mock if needed
        return null;
    }

    @Override
    public List<RoutingModels.RouteResult> getAlternatives(double[] origin, double[] dest) {
        // TODO: Implement actual ORS "alternatives" logic
        return Collections.emptyList();
    }
}

